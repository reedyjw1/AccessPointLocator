package edu.udmercy.accesspointlocater.features.placeAccessPoints.view

import android.graphics.Bitmap
import android.graphics.PointF
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.udmercy.accesspointlocater.features.create.repositories.BuildingImageRepository
import edu.udmercy.accesspointlocater.features.create.room.BuildingImage
import edu.udmercy.accesspointlocater.features.home.repositories.SessionRepository
import edu.udmercy.accesspointlocater.features.placeAccessPoints.model.APPointLocation
import edu.udmercy.accesspointlocater.features.viewSession.repositories.APLocationRepository
import edu.udmercy.accesspointlocater.features.viewSession.room.APLocation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject

class PlaceAccessPointsViewModel : ViewModel(), KoinComponent {

    private val sessionRepo: SessionRepository by inject()
    private val buildingImageRepo: BuildingImageRepository by inject()
    private val accessPointRepo: APLocationRepository by inject()
    private var floorCount = 0


    val apPoints: MutableLiveData<MutableList<APPointLocation>> = MutableLiveData()
    val currentDisplayImage: MutableLiveData<BuildingImage> = MutableLiveData()
    val currentFloorNumber: MutableLiveData<Int> = MutableLiveData()


    fun initializeImages(uuid: String){
        viewModelScope.launch(Dispatchers.IO) {
            val session = sessionRepo.getCurrentSession(uuid)
            val firstFloorImage = buildingImageRepo.getFloorImage(uuid, 0)
            apPoints.postValue(mutableListOf())
            currentDisplayImage.postValue(firstFloorImage)
            currentFloorNumber.postValue(0)
            floorCount = buildingImageRepo.getFloorCount(uuid)
        }
    }

    fun changeFloor(uuid: String, changeValue: Int){
        // tempCurrentFloor is the index of the current floor, 0 index = 1st floor, index = 2nd floor....
        val tempCurrentFloor = currentFloorNumber.value ?: return
        val tempNextFloor = tempCurrentFloor + changeValue

        //check if requested floor is available
        if(tempNextFloor in 0 until floorCount) {
            viewModelScope.launch(Dispatchers.IO) {
                currentFloorNumber.postValue(tempNextFloor)
                val floorImage = buildingImageRepo.getFloorImage(uuid, tempNextFloor)
                currentDisplayImage.postValue(floorImage)
                apPoints.postValue(apPoints.value)
            }
        }
    }

    fun savePointsToDB(uuid: String){
        //save points for floor
        viewModelScope.launch(Dispatchers.IO) {
            val locations = apPoints.value?.map { APLocation(floor = it.floor, uuid = uuid, xCoordinate = it.point.x.toDouble(), yCoordinate = it.point.y.toDouble(), zCoordinate = -1.0, ssid = it.macAddress) }
            locations?.let {
                accessPointRepo.saveAccessPointLocations(it)
            }
        }
    }

    fun markSessionComplete(uuid:String){
        viewModelScope.launch(Dispatchers.IO) {
            sessionRepo.markSessionComplete(uuid)
        }
    }
}