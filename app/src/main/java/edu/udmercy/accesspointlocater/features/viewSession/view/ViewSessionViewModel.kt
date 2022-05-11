package edu.udmercy.accesspointlocater.features.viewSession.view

import android.app.Application
import android.graphics.PointF
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.*
import edu.udmercy.accesspointlocater.features.createSession.repositories.BuildingImageRepository
import edu.udmercy.accesspointlocater.features.home.repositories.SessionRepository
import edu.udmercy.accesspointlocater.features.createSession.room.BuildingImage
import edu.udmercy.accesspointlocater.features.viewSession.model.AccessPointInfo
import edu.udmercy.accesspointlocater.features.viewSession.repositories.APLocationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.io.File
import java.io.FileOutputStream
import java.util.*
import kotlin.text.StringBuilder

class ViewSessionViewModel(application: Application, private val savedStateHandle: SavedStateHandle): AndroidViewModel(application), KoinComponent {

    private val TAG = "ViewSessionViewModel"

    private val sessionRepo: SessionRepository by inject()
    private val buildingImageRepo: BuildingImageRepository by inject()
    private val accessPointRepo: APLocationRepository by inject()
    val currentBitmap: MutableLiveData<BuildingImage> = MutableLiveData<BuildingImage>()
    val sessionName: MutableLiveData<String> = MutableLiveData()
    val accessPointInfoList = MutableLiveData<MutableList<AccessPointInfo>>()
    private var _accessPointInfoList = mutableListOf<AccessPointInfo>()
    val accessPointLocations = MutableLiveData<MutableList<Pair<Int, PointF>>>()
    val currentFloor = MutableLiveData<Int>(0)
    private var floorCount: Int? = null
    private var image: BuildingImage?= null


    fun getCurrentSession(uuid: String) {
        // Retrieves any necessary information about the session
        viewModelScope.launch(Dispatchers.IO) {

            val session =sessionRepo.getCurrentSession(uuid)
            //currentBitmap.postValue(session.images)
            sessionName.postValue(session.sessionLabel)
            image = buildingImageRepo.getFloorImage(uuid, 0)
            floorCount = buildingImageRepo.getFloorCount(uuid)
            currentBitmap.postValue(null)
            currentBitmap.postValue(image)

            accessPointRepo.getAllAccessPoints(uuid).collect { list ->
                _accessPointInfoList = list.mapIndexed { index, apLocation ->
                    AccessPointInfo(apLocation.floor, apLocation.ssid, apLocation.uuid, index, apLocation.xCoordinate, apLocation.yCoordinate, apLocation.zCoordinate)
                } as MutableList<AccessPointInfo>
                getAccessPoints(0)
                accessPointInfoList.postValue(_accessPointInfoList.filter { it.floorNumber == currentFloor.value }.toMutableList())
            }

        }
    }

    // Displays new image on the ImageView
    fun moveImage(number: Int, uuid: String) {
        viewModelScope.launch(Dispatchers.IO) {
            if (number == 1 || number == -1) {
                val count = floorCount ?: return@launch
                if ((currentFloor.value == count-1 && number == 1) || (currentFloor.value == 0 && number == -1)) return@launch
                val floorVal = currentFloor.value ?: return@launch
                currentFloor.postValue(floorVal+number)
                currentBitmap.postValue(null)
                getAccessPoints(floorVal+number)
                val buildingImage = buildingImageRepo.getFloorImage(uuid, floorVal+number)
                currentBitmap.postValue(buildingImage)
            }
        }
    }

    // Updates the Map with the access points only the floor they are looking at
    private fun getAccessPoints(floor: Int) {
        val aps = _accessPointInfoList.filter { pred -> pred.floorNumber == floor }
        accessPointInfoList.postValue(aps.toMutableList())
        val apPointFs = aps.map { Pair(it.apNumber, PointF(it.xCoordinate.toFloat(), it.yCoordinate.toFloat())) }.toMutableList()
        Log.d(TAG, "getAccessPoints: apPoints: $apPointFs")
        accessPointLocations.postValue(apPointFs)
    }

    fun saveFile(uri: Uri?, completion: (Boolean) -> (Unit)) {
        Log.i(TAG, "saveFile: Saving File")
        viewModelScope.launch(Dispatchers.IO) {
            // Get Access points and their associated rooms
            try {
                val sessionUUID =  savedStateHandle.getLiveData<String>("uuid").value ?: throw NullPointerException("Could not retrieve UUID")
                val accessPoints = accessPointRepo.retrieveAccessPoints(sessionUUID)

                Log.i(TAG, "saveFile: Creating Lists")
                // Construct DataStructure to Hold Room information
                val rowsToWrite = mutableListOf<MutableList<String>>()
                accessPoints.forEach {
                    val apList = mutableListOf(it.ssid)
                    apList.addAll(it.roomNumber)
                    rowsToWrite.add(apList)
                }

                Log.i(TAG, "saveFile: Appending to StringBuilder")
                // String Builder to Construct CSV
                val csvStringBuilder = StringBuilder()
                rowsToWrite.forEach {
                    csvStringBuilder.append(it.toString().removePrefix("[").removeSuffix("]"))
                    csvStringBuilder.append("\n")
                }
                csvStringBuilder.append("\r\n")
                val export = csvStringBuilder.toString()

                Log.i(TAG, "saveFile: Creating CSV Name")
                // Create csv file name
                val sbFileName = StringBuilder(sessionName.value ?: Calendar.getInstance().time.toString())
                sbFileName.append("-zones.csv")
                val fileName = sbFileName.toString()

                Log.i(TAG, "saveFile: Saveing to FileSystem")
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
                    val file = dir?.createFile("text/csv", fileName)

                    file?.let {
                        context.contentResolver.openFileDescriptor(it.uri, "w")
                            ?.use { parcelFileDescriptor ->
                                FileOutputStream(parcelFileDescriptor.fileDescriptor).use { output ->
                                    output.write(export.toByteArray())
                                }
                            }
                    }
                }
                withContext(Dispatchers.Main) {
                    completion(true)
                }
            } catch (e: Exception) {
                Log.i(TAG, "saveFile: ${e.localizedMessage}")
                withContext(Dispatchers.Main) {
                    completion(false)
                }
            }
        }
    }

    fun onPause() {
        currentBitmap.value?.image?.recycle()
    }
}