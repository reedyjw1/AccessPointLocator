package edu.udmercy.accesspointlocater.features.session.sub.create

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

    fun addNewSession(sessionName: String, buildingName: String, imagePath: String = "Test") {
        viewModelScope.launch(Dispatchers.IO) {
            dbRepository.createNewSession(
                Calendar.getInstance().time.toString(),
                sessionName,
                buildingName,
                imagePath
            )
            saved.postValue(Event(true))
        }
    }
}