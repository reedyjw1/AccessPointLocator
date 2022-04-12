package edu.udmercy.accesspointlocater.features.execute.view

import android.app.Application
import android.graphics.PointF
import android.net.Uri
import android.net.wifi.ScanResult
import com.google.gson.Gson

import edu.udmercy.accesspointlocater.features.create.repositories.BuildingImageRepository
import edu.udmercy.accesspointlocater.features.home.repositories.SessionRepository

import edu.udmercy.accesspointlocater.features.create.room.BuildingImage
import edu.udmercy.accesspointlocater.features.execute.model.SessionExport
import edu.udmercy.accesspointlocater.features.execute.repositories.WifiScansRepository
import edu.udmercy.accesspointlocater.features.execute.room.WifiScans
import edu.udmercy.accesspointlocater.features.home.room.Session
import edu.udmercy.accesspointlocater.features.viewSession.repositories.APLocationRepository
import edu.udmercy.accesspointlocater.utils.Event
import edu.udmercy.accesspointlocater.utils.Multilateration.calculateMultilateration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.KoinComponent
import org.koin.core.inject
import android.os.Environment
import android.util.Log
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.*
import edu.udmercy.accesspointlocater.features.accessPointChooser.repositories.AccessPointReferenceRepository
import edu.udmercy.accesspointlocater.features.execute.room.estimateLocations
import edu.udmercy.accesspointlocater.utils.MathUtils
import java.io.File
import java.io.FileOutputStream


class ExecuteSessionViewModel(
    application: Application,
    private val savedStateHandle: SavedStateHandle
): AndroidViewModel(application), KoinComponent {
    private val sessionRepo: SessionRepository by inject()
    private val wifiScansRepo: WifiScansRepository by inject()
    private val buildingImageRepo: BuildingImageRepository by inject()
    private val apLocationRepo: APLocationRepository by inject()
    private val apChooser: AccessPointReferenceRepository by inject()

    var _isScanning = false
    var isScanning = MutableLiveData<Event<Boolean>>()

    private var floorHeights = listOf<Double>()

    // 4 feet in meters
    private val phoneHeight = 1.2192

    private var session: Session? = null
    private var image: BuildingImage?= null
    private var floorCount: Int? = null

    private var _savedPoints: List<WifiScans> = emptyList()
    var savedPoints: MutableLiveData<List<WifiScans>> = MutableLiveData(listOf())

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
                val tempCount = buildingImageRepo.getFloorCount(it)
                floorCount = tempCount
                floorHeights = buildingImageRepo.getFloorHeights(it, tempCount)

                currentBitmap.postValue(null)
                currentBitmap.postValue(image)
                wifiScansRepo.getAllScans(it).collect { list ->
                    _savedPoints = list
                    savedPoints.postValue(list.filter { pred -> pred.floor == floor.value })
                }
            }
        }
    }

    fun saveResults(list: List<ScanResult>) {
        viewModelScope.launch(Dispatchers.IO) {
            list.forEach {
                val sessionSafe = session ?: return@launch
                val position = currentPosition ?: return@launch
                val floorVal = floor.value ?: return@launch

                wifiScansRepo.saveAccessPointScan(
                    WifiScans(
                        uuid = sessionSafe.uuid,
                        currentLocationX = position.x.toDouble(),
                        currentLocationY =  position.y.toDouble(),
                        currentLocationZ = MathUtils.calculateHeightFromFloors(floorHeights, phoneHeight, floorVal),
                        floor = floorVal,
                        ssid = it.BSSID,
                        capabilities = it.capabilities,
                        centerFreq0 = it.centerFreq0,
                        centerFreq1 = it.centerFreq1,
                        channelWidth = it.channelWidth,
                        frequency = it.frequency,
                        level = it.level,
                        timestamp = it.timestamp,
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
                    isFinished = true,
                    scaleUnits = sessionTemp.scaleUnits,
                    scaleNumber = sessionTemp.scaleNumber,
                    pixelDistance = sessionTemp.pixelDistance
                )
            )
            val scans = wifiScansRepo.getScanList(sessionTemp.uuid)
            val reference = apChooser.getReferenceAccessPoint(sessionTemp.uuid)
            // val apLocations = calculateMultilateration(_savedPoints, sessionTemp.uuid, scans, pointDistance, scaleValue, scaleUnit, reference.level.toDouble(), reference.distance)
            val apLocations = _savedPoints.estimateLocations(sessionTemp.uuid)
            apLocationRepo.saveAccessPointLocations(apLocations)

            withContext(Dispatchers.Main) {
                completion()
            }

        }

    }

    fun saveFile(uri: Uri?) {
        viewModelScope.launch(Dispatchers.IO) {
            val sessionTemp = session ?: return@launch
            val scans = wifiScansRepo.getScanList(sessionTemp.uuid)
            val fileName = "${sessionTemp.sessionLabel}.json"

            val export = Gson().toJson(
                SessionExport(
                    sessionTemp,
                    scans,
                )
            )

            // Called if the SAF is not needed
            if (uri == null) {
                val target = File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
                    fileName
                )
                target.delete()
                target.createNewFile()
                target.writeText(export)

            } else {
                val context = getApplication<Application>().applicationContext
                val dir = DocumentFile.fromTreeUri(context, uri)
                val file = dir?.createFile("test/json", fileName)

                file?.let {
                    context.contentResolver.openFileDescriptor(it.uri, "w")
                        ?.use { parcelFileDescriptor ->
                            FileOutputStream(parcelFileDescriptor.fileDescriptor).use { output ->
                                output.write(export.toByteArray())
                            }
                        }
                }
            }

        }
    }

    fun loadFile(uri: Uri) {
        Log.i(TAG, "loadFile: $uri")
        try {
            val bytes = getApplication<Application>().applicationContext.contentResolver.openInputStream(uri)?.readBytes()
                ?: throw Exception("File was empty")
            val sessionLabel = session?.sessionLabel ?: return
            val sessionUuid = session?.uuid ?: return

            val jsonString = String(bytes)
            val loadedSession = Gson().fromJson(jsonString, SessionExport::class.java)

            viewModelScope.launch(Dispatchers.IO) {
                loadedSession.wifiScans.forEach {
                    wifiScansRepo.saveAccessPointScan(
                        WifiScans(
                            uuid = sessionUuid,
                            currentLocationX = it.currentLocationX,
                            currentLocationY = it.currentLocationY,
                            currentLocationZ = it.currentLocationZ,
                            floor = it.floor,
                            ssid = it.ssid,
                            capabilities = it.capabilities,
                            centerFreq0 = it.centerFreq0,
                            centerFreq1 = it.centerFreq1,
                            channelWidth = it.channelWidth,
                            frequency = it.frequency,
                            level = it.level,
                            timestamp = it.timestamp
                        )
                    )
                }
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "loadFile: Could not load uri=$uri because ${e.localizedMessage}")
        }
    }

    fun moveImage(number: Int, uuid: String) {
        viewModelScope.launch(Dispatchers.IO) {
            if (number == 1 || number == -1) {
                val count = floorCount ?: return@launch
                if ((floor.value == count-1 && number == 1) || (floor.value == 0 && number == -1)) return@launch
                val floorVal = floor.value ?: return@launch
                //currentBitmap.value?.image?.recycle()
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


}