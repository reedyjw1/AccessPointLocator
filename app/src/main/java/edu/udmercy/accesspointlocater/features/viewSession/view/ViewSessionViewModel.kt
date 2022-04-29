package edu.udmercy.accesspointlocater.features.viewSession.view

import android.graphics.PointF
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.udmercy.accesspointlocater.features.create.repositories.BuildingImageRepository
import edu.udmercy.accesspointlocater.features.home.repositories.SessionRepository
import edu.udmercy.accesspointlocater.features.create.room.BuildingImage
import edu.udmercy.accesspointlocater.features.viewSession.model.AccessPointInfo
import edu.udmercy.accesspointlocater.features.viewSession.repositories.APLocationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject

class ViewSessionViewModel: ViewModel(), KoinComponent {

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



    fun onPause() {
        currentBitmap.value?.image?.recycle()
    }
}