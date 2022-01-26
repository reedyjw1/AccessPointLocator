package edu.udmercy.accesspointlocater.features.session.sub.execute

import android.graphics.PointF
import android.net.wifi.ScanResult
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import edu.udmercy.accesspointlocater.features.session.repositories.AccessPointRepository
import edu.udmercy.accesspointlocater.features.session.repositories.BuildingImageRepository
import edu.udmercy.accesspointlocater.features.session.repositories.SessionRepository

import edu.udmercy.accesspointlocater.features.session.room.AccessPoint
import edu.udmercy.accesspointlocater.features.session.room.BuildingImage
import edu.udmercy.accesspointlocater.features.session.room.Session
import edu.udmercy.accesspointlocater.features.viewSession.repositories.APLocationRepository
import edu.udmercy.accesspointlocater.features.viewSession.room.APLocation
import edu.udmercy.accesspointlocater.utils.Event
import edu.udmercy.accesspointlocater.utils.Multilateration
import edu.udmercy.accesspointlocater.utils.ReferencePoint
import edu.udmercy.accesspointlocater.utils.Units
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.KoinComponent
import org.koin.core.inject
import kotlin.math.abs
import kotlin.math.log10
import kotlin.math.pow

class ExecuteSessionViewModel(
    private val savedStateHandle: SavedStateHandle
): ViewModel(), KoinComponent {
    private val sessionRepo: SessionRepository by inject()
    private val accessPointRepo: AccessPointRepository by inject()
    private val buildingImageRepo: BuildingImageRepository by inject()
    private val apLocationRepo: APLocationRepository by inject()

    var _isScanning = false
    var isScanning = MutableLiveData<Event<Boolean>>()

    private var session: Session? = null
    private var image: BuildingImage?= null
    private var floorCount: Int? = null
    var _savedPoints: List<AccessPoint> = emptyList()
    var savedPoints: MutableLiveData<List<AccessPoint>> = MutableLiveData(listOf())
    var altitude: Double = 0.0

    val currentBitmap: MutableLiveData<BuildingImage> = MutableLiveData()
    var currentPosition: PointF? = null

    private var scaleValue = 0.0
    private var scaleUnit = "Meters"
    private var pointDistance = 0.0

    var floor: MutableLiveData<Int> = MutableLiveData(0)

    companion object {
        private const val TAG = "ExecuteSessionViewModel"
    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            savedStateHandle.getLiveData<String>("uuid").value?.let {
                session = sessionRepo.getCurrentSession(it)

                scaleValue = session?.scaleNumber ?: 0.0
                scaleUnit = session?.scaleUnits ?: "Meters"
                pointDistance = session?.pixelDistance ?: 0.0

                image = buildingImageRepo.getFloorImage(it, 0)
                floorCount = buildingImageRepo.getFloorCount(it)
                currentBitmap.postValue(null)
                currentBitmap.postValue(image)
                accessPointRepo.getAllScans(it).collect { list ->
                    _savedPoints = list
                    savedPoints.postValue(list.filter { pred -> pred.floor == floor.value })
                }
            }
        }
    }

    fun saveResults(list: List<ScanResult>, uuid: String, altitude: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            list.forEach {
                val distance = calculateDistanceInMeters(it.level, it.frequency)
                val sessionSafe = session ?: return@launch
                val position = currentPosition ?: return@launch
                val floorVal = floor.value ?: return@launch

                accessPointRepo.saveAccessPointScan(
                    AccessPoint(
                    uuid = sessionSafe.uuid,
                    currentLocationX = position.x.toDouble(),
                    currentLocationY =  position.y.toDouble(),
                    currentLocationZ = altitude,
                    floor = floorVal,
                    distance = distance,
                    ssid = it.BSSID
                    )
                )

            }
        }
    }

    fun calculateResults(completion: ()->Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val sessionTemp = session ?: return@launch
            sessionRepo.updateSession(
                Session(
                    uuid = sessionTemp.uuid,
                    sessionLabel = sessionTemp.sessionLabel,
                    timestamp = sessionTemp.timestamp,
                    building = sessionTemp.building,
                    true
                )
            )
            val apLocations = calculateMultilateration(_savedPoints, sessionTemp.uuid)
            apLocationRepo.saveAccessPointLocations(apLocations)

            withContext(Dispatchers.Main) {
                completion()
            }

        }

    }

    fun moveImage(number: Int, uuid: String) {
        viewModelScope.launch(Dispatchers.IO) {
            if (number == 1 || number == -1) {
                val count = floorCount ?: return@launch
                if ((floor.value == count-1 && number == 1) || (floor.value == 0 && number == -1)) return@launch
                val floorVal = floor.value ?: return@launch
                floor.postValue(floorVal+number)
                currentBitmap.postValue(null)
                getAccessPoints(floorVal+number)
                val buildingImage = buildingImageRepo.getFloorImage(uuid, floorVal+number)
                currentBitmap.postValue(buildingImage)
            }
        }
    }

    private fun getAccessPoints(floor: Int) {
        savedPoints.postValue(_savedPoints.filter { pred -> pred.floor == floor })
    }

    fun onPause() {
        currentBitmap.value?.image?.recycle()
    }

    private fun calculateDistanceInMeters(signalLevelInDb: Int, freqInMHz: Int): Double {
        val exp = (27.55 - 20 * log10(freqInMHz.toDouble()) + abs(signalLevelInDb)) / 20.0
        val dist = 10.0.pow(exp)
        return (dist *100.0 ) / 1000.0
    }

    /*private fun calculateTrilateration(list: List<AccessPoint>, uuid: String): List<APLocation> {
        val apLocationList = mutableListOf<APLocation>()
        val ssidList = list.map { it.ssid }.distinct()
        for (ssid in ssidList) {
            val positions = mutableListOf<DoubleArray>()
            for (item in list.filter { it.ssid == ssid }) {
                positions.add(
                    doubleArrayOf(
                        item.currentLocationX,
                        item.currentLocationY
                    )
                )
            }
            val distances = list.filter{ it.ssid == ssid }.map { it.distance }.toDoubleArray()

            val solver = NonLinearLeastSquaresSolver(
                TrilaterationFunction(
                    positions.toTypedArray(),
                    distances
                ), LevenbergMarquardtOptimizer()
            )
            val optimum = solver.solve()
            val centroid = optimum.point.toArray().toList()
            apLocationList.add(APLocation(0, uuid, centroid[0], centroid[1], 0.0,0, ssid))
        }
        return apLocationList
    }*/

    private fun calculateMultilateration(list: List<AccessPoint>, uuid: String): List<APLocation> {
        val apLocationList = mutableListOf<APLocation>()
        val ssidList = list.map { it.ssid }.distinct()
        val scale = calculateScale(pointDistance, scaleValue, scaleUnit)
        for (ssid in ssidList) {
            val positions = mutableListOf<ReferencePoint>()
            for (item in list.filter { it.ssid == ssid }) {
                positions.add(
                    ReferencePoint(item.currentLocationX / scale, item.currentLocationY / scale, item.currentLocationZ,item.distance, Units.METERS)
                )
            }
            val solution = Multilateration.calculate(positions)?.array
            if (solution != null) {
                apLocationList.add(APLocation(0, uuid, solution[1][0] * scale, solution[2][0] * scale, solution[3][0], 0, ssid))
            }
        }
        return apLocationList
    }

    // Returns scale in pixels/meter
    private fun calculateScale(distance: Double, scale: Double, scaleUnit: String): Double {
        return when(scaleUnit) {
            "Meters" -> {
                distance/scale
            }
            "Feet" -> {
                distance / (scale * 0.3048)
            }
            "Inches" -> {
                distance / (scale * 0.0254)
            }
            else -> {
                -1.0
            }
        }
    }
}