package edu.udmercy.accesspointlocater.features.create.view

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.udmercy.accesspointlocater.features.create.repositories.BuildingImageRepository
import edu.udmercy.accesspointlocater.features.create.room.BuildingImage
import edu.udmercy.accesspointlocater.features.home.repositories.SessionRepository
import edu.udmercy.accesspointlocater.utils.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.util.*

class CreateSessionViewModel: ViewModel(), KoinComponent {

    private val sessionRepo: SessionRepository by inject()
    private val buildingImageRepo: BuildingImageRepository by inject()
    val saved = MutableLiveData<Event<Boolean>>()
    val buildingImages: MutableList<Bitmap> = mutableListOf()
    val presentedBitmap: MutableLiveData<Bitmap> = MutableLiveData()

    fun addNewSession(sessionName: String, buildingName: String, images: List<Bitmap>, scaleNumber: Double, scaleUnit: String, pixelDistance: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            val uuid = UUID.randomUUID().toString()
            sessionRepo.createNewSession(
                uuid,
                Calendar.getInstance().time.toString(),
                sessionName,
                buildingName,
                scaleNumber,
                scaleUnit,
                pixelDistance
            )
            buildingImageRepo.addImagesToSession(images.mapIndexed { index, bitmap ->
                BuildingImage(uuid = uuid, image = bitmap, floor = index)
            })
            saved.postValue(Event(true))
        }
    }
}