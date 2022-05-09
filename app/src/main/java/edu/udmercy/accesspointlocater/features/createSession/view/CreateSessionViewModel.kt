package edu.udmercy.accesspointlocater.features.createSession.view

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.udmercy.accesspointlocater.features.createSession.repositories.BuildingImageRepository
import edu.udmercy.accesspointlocater.features.createSession.room.BuildingImage
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
    var apKnownLocationsCheckbox: Boolean = false

    // Calls the database functions to save the session
    fun addNewSession(sessionName: String, buildingName: String, images: List<Bitmap>) {
        viewModelScope.launch(Dispatchers.IO) {
            val uuid = UUID.randomUUID().toString()
            sessionRepo.createNewSession(
                uuid,
                Calendar.getInstance().time.toString(),
                sessionName,
                buildingName,
                apKnownLocationsCheckbox
            )

            buildingImageRepo.addImagesToSession(images.mapIndexed { index, bitmap ->
                BuildingImage(uuid = uuid, image = bitmap, floor = index)
            })
            saved.postValue(Event(true))
        }
    }




}