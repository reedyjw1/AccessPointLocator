package edu.udmercy.accesspointlocater.features.session.sub.create

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.udmercy.accesspointlocater.features.session.repositories.SessionRepository
import edu.udmercy.accesspointlocater.utils.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.util.*

class CreateSessionViewModel: ViewModel(), KoinComponent {

    private val dbRepository: SessionRepository by inject()
    val saved = MutableLiveData<Event<Boolean>>()
    var buildingImage: Bitmap? = null

    fun addNewSession(sessionName: String, buildingName: String, image: Bitmap) {
        viewModelScope.launch(Dispatchers.IO) {
            dbRepository.createNewSession(
                Calendar.getInstance().time.toString(),
                sessionName,
                buildingName,
                image
            )
            saved.postValue(Event(true))
        }
    }
}