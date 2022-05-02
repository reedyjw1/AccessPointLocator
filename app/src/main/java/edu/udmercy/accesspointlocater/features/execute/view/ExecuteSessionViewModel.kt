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
import edu.udmercy.accesspointlocater.features.execute.room.estimateLocations
import java.io.File
import java.io.FileOutputStream
import java.util.*


class ExecuteSessionViewModel(
    application: Application,
    private val savedStateHandle: SavedStateHandle
): AndroidViewModel(application), KoinComponent {

    private val sessionRepo: SessionRepository by inject()
    private val wifiScansRepo: WifiScansRepository by inject()
    private val buildingImageRepo: BuildingImageRepository by inject()
    private val apLocationRepo: APLocationRepository by inject()
    var _isScanning = false
    var isScanning = MutableLiveData<Event<Boolean>>()

    private var session: Session? = null
    private var image: BuildingImage?= null
    private var floorCount: Int? = null

    private var _savedPoints: List<WifiScans> = emptyList()
    var savedPoints: MutableLiveData<List<WifiScans>> = MutableLiveData(listOf())

    val currentBitmap: MutableLiveData<BuildingImage> = MutableLiveData()
    var currentPosition: PointF? = null
    var currentScanUUID: String? = null
    var roomValue: String? = null
    var lastSelectedRoom = ""

    var floor: MutableLiveData<Int> = MutableLiveData(0)

    companion object {
        private const val TAG = "ExecuteSessionViewModel"
    }

    init {
        // Gets the Session Data from the Database on fragment creation
        viewModelScope.launch(Dispatchers.IO) {
            savedStateHandle.getLiveData<String>("uuid").value?.let {
                session = sessionRepo.getCurrentSession(it)

                image = buildingImageRepo.getFloorImage(it, 0)
                val tempCount = buildingImageRepo.getFloorCount(it)
                floorCount = tempCount

                currentBitmap.postValue(null)
                currentBitmap.postValue(image)
                wifiScansRepo.getAllScans(it).collect { list ->
                    _savedPoints = list
                    savedPoints.postValue(list.filter { pred -> pred.floor == floor.value })
                }
            }
        }
    }
    // Saves the wifi scan data to the database through the repository
    fun saveResults(list: List<ScanResult>) {
        viewModelScope.launch(Dispatchers.IO) {
            list.forEach {
                val sessionSafe = session ?: return@launch
                val position = currentPosition ?: return@launch
                val scanUUID = currentScanUUID?: return@launch
                val floorVal = floor.value ?: return@launch
                val roomNumber = roomValue ?: return@launch

                wifiScansRepo.saveAccessPointScan(
                    WifiScans(
                        uuid = sessionSafe.uuid,
                        currentLocationX = position.x.toDouble(),
                        currentLocationY =  position.y.toDouble(),
                        // currentLocationZ = MathUtils.calculateHeightFromFloors(floorHeights, phoneHeight, floorVal),
                        currentLocationZ = 0.0,
                        floor = floorVal,
                        ssid = it.BSSID,
                        capabilities = it.capabilities,
                        centerFreq0 = it.centerFreq0,
                        centerFreq1 = it.centerFreq1,
                        channelWidth = it.channelWidth,
                        frequency = it.frequency,
                        level = it.level,
                        timestamp = it.timestamp,
                        scanUUID = scanUUID,
                        roomNumber = roomNumber
                    )
                )

            }
        }
    }

    // Calls function to estimate the AP locations and saves them to DB
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
                )
            )
            val apLocations = _savedPoints.estimateLocations(sessionTemp.uuid, 5000)
            apLocationRepo.saveAccessPointLocations(apLocations)

            withContext(Dispatchers.Main) {
                completion()
            }

        }

    }

    fun updateRoomValue(scanUUID: String, roomValue: String){
        viewModelScope.launch(Dispatchers.IO) {
            wifiScansRepo.insertRoomNumber(scanUUID, roomValue)
        }
    }

    // Converts data to json, writes it to file, and saves to the user specified location
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

    // Reads JSON file and saves data back into database with new session UUID
    fun loadFile(uri: Uri) {
        Log.i(TAG, "loadFile: $uri")
        try {
            val bytes = getApplication<Application>().applicationContext.contentResolver.openInputStream(uri)?.readBytes()
                ?: throw Exception("File was empty")
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
                            timestamp = it.timestamp,
                            scanUUID = it.scanUUID,
                            roomNumber = it.roomNumber
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