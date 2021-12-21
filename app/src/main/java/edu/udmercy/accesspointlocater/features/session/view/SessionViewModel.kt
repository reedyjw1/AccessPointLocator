package edu.udmercy.accesspointlocater.features.session.view

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.udmercy.accesspointlocater.features.session.model.SessionUI
import edu.udmercy.accesspointlocater.features.session.repositories.SessionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject

class SessionViewModel: ViewModel(), KoinComponent {

    private val sessionRepo: SessionRepository by inject()
    val sessionList = MutableLiveData<MutableList<SessionUI>>()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            sessionRepo.getAllSessions().collect { list ->
                sessionList.postValue(list.map {
                    SessionUI(it.uuid, it.sessionLabel, it.building + " - " +it.timestamp )
                } as MutableList<SessionUI>)
            }
        }
    }

    fun getSessions() {

    }
}