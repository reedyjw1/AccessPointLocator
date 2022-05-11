package edu.udmercy.accesspointlocater.features.home.view

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.udmercy.accesspointlocater.features.createSession.repositories.BuildingImageRepository
import edu.udmercy.accesspointlocater.features.executeSession.repositories.WifiScansRepository
import edu.udmercy.accesspointlocater.features.home.model.SessionUI
import edu.udmercy.accesspointlocater.features.home.repositories.SessionRepository
import edu.udmercy.accesspointlocater.features.viewSession.repositories.APLocationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject

class SessionViewModel: ViewModel(), KoinComponent {

    private val sessionRepo: SessionRepository by inject()
    private val apLocationRepo: APLocationRepository by inject()
    private val wifiScansRepo: WifiScansRepository by inject()
    private val buildingImageRepo: BuildingImageRepository by inject()
    val sessionList = MutableLiveData<MutableList<SessionUI>>()

    init {
        // Displays all of the sessions in the DB right when they are added or deleted
        viewModelScope.launch(Dispatchers.IO) {
            sessionRepo.getAllSessions().collect { list ->
                sessionList.postValue(list.map {
                    SessionUI(it.uuid, it.sessionLabel, it.building, it.timestamp,it.isFinished,it.areApLocationsKnown)
                } as MutableList<SessionUI>)
            }
        }
    }

    // Function to delete a session and its corresponding data in other tables from the DB
    fun deleteSession(uuid: String){
        viewModelScope.launch(Dispatchers.IO) {
            apLocationRepo.deleteSession(uuid)
            wifiScansRepo.deleteSession(uuid)
            buildingImageRepo.deleteSession(uuid)
            sessionRepo.deleteSession(uuid)
        }
    }
}