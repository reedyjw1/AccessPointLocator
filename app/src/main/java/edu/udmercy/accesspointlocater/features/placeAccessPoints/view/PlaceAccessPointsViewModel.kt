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

    val currentDisplayImage: MutableLiveData<BuildingImage> = MutableLiveData()

    fun initializeImages(uuid: String){
        viewModelScope.launch(Dispatchers.IO) {
            val session = sessionRepo.getCurrentSession(uuid)
            val firstFloorImage = buildingImageRepo.getFloorImage(uuid, 0)
            currentDisplayImage.postValue(firstFloorImage)
        }
    }

}