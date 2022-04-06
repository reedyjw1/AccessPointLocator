package edu.udmercy.accesspointlocater.features.placeAccessPoints.view

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.udmercy.accesspointlocater.features.create.repositories.BuildingImageRepository
import edu.udmercy.accesspointlocater.features.create.room.BuildingImage
import edu.udmercy.accesspointlocater.features.home.repositories.SessionRepository
import edu.udmercy.accesspointlocater.features.viewSession.repositories.APLocationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject

class PlaceAccessPointsViewModel : ViewModel(), KoinComponent {

    private val sessionRepo: SessionRepository by inject()
    private val buildingImageRepo: BuildingImageRepository by inject()
    private val accessPointRepo: APLocationRepository by inject()
    private var floorCount = 0

    val currentDisplayImage: MutableLiveData<BuildingImage> = MutableLiveData()
    val currentFloorNumber: MutableLiveData<Int> = MutableLiveData()

    fun initializeImages(uuid: String){
        viewModelScope.launch(Dispatchers.IO) {
            val session = sessionRepo.getCurrentSession(uuid)
            val firstFloorImage = buildingImageRepo.getFloorImage(uuid, 0)
            currentDisplayImage.postValue(firstFloorImage)
            currentFloorNumber.postValue(0)
            floorCount = buildingImageRepo.getFloorCount(uuid)
        }
    }

    fun changeFloor(uuid: String, changeValue: Int){
        var tempCurrentFloor = currentFloorNumber.value ?: return
        tempCurrentFloor += changeValue

        if(tempCurrentFloor in 0 until floorCount) {
            viewModelScope.launch(Dispatchers.IO) {
                currentFloorNumber.postValue(tempCurrentFloor)
                val floorImage = buildingImageRepo.getFloorImage(uuid, tempCurrentFloor)
                currentDisplayImage.postValue(floorImage)
            }
        }
    }
}