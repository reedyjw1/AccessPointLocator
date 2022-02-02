package edu.udmercy.accesspointlocater.features.execute.view

import android.graphics.PointF
import android.net.wifi.ScanResult
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import edu.udmercy.accesspointlocater.features.create.repositories.BuildingImageRepository
import edu.udmercy.accesspointlocater.features.home.repositories.SessionRepository

import edu.udmercy.accesspointlocater.features.create.room.BuildingImage
import edu.udmercy.accesspointlocater.features.execute.repositories.WifiScansRepository
import edu.udmercy.accesspointlocater.features.execute.room.WifiScans
import edu.udmercy.accesspointlocater.features.home.room.Session
import edu.udmercy.accesspointlocater.features.viewSession.repositories.APLocationRepository
import edu.udmercy.accesspointlocater.features.viewSession.room.APLocation
import edu.udmercy.accesspointlocater.utils.Event
import edu.udmercy.accesspointlocater.utils.Multilateration
import edu.udmercy.accesspointlocater.utils.Multilateration.calculateMultilateration
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
    private val wifiScansRepo: WifiScansRepository by inject()
    private val buildingImageRepo: BuildingImageRepository by inject()
    private val apLocationRepo: APLocationRepository by inject()

    var _isScanning = false
    var isScanning = MutableLiveData<Event<Boolean>>()

    private var session: Session? = null
    private var image: BuildingImage?= null
    private var floorCount: Int? = null
    var _savedPoints: List<WifiScans> = emptyList()
    var savedPoints: MutableLiveData<List<WifiScans>> = MutableLiveData(listOf())
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
                wifiScansRepo.getAllScans(it).collect { list ->
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

                wifiScansRepo.saveAccessPointScan(
                    WifiScans(
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
            val scans = wifiScansRepo.getScanList(sessionTemp.uuid)
            val apLocations = calculateMultilateration(_savedPoints, sessionTemp.uuid, scans, pointDistance, scaleValue, scaleUnit)
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


}