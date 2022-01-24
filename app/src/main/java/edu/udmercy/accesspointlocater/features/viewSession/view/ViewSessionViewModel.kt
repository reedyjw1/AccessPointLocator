package edu.udmercy.accesspointlocater.features.viewSession.view

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.udmercy.accesspointlocater.features.session.model.SessionUI
import edu.udmercy.accesspointlocater.features.session.repositories.AccessPointRepository
import edu.udmercy.accesspointlocater.features.session.repositories.SessionRepository
import edu.udmercy.accesspointlocater.features.viewSession.model.AccessPointInfo
import edu.udmercy.accesspointlocater.features.viewSession.repositories.APLocationRepository
import edu.udmercy.accesspointlocater.features.viewSession.room.APLocation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject

class ViewSessionViewModel: ViewModel(), KoinComponent {

    private val sessionRepo: SessionRepository by inject()
    private val accessPointRepo: APLocationRepository by inject()
    val currentBitmap: MutableLiveData<Bitmap> = MutableLiveData<Bitmap>()
    val sessionName: MutableLiveData<String> = MutableLiveData()
    val accessPointInfoList = MutableLiveData<MutableList<AccessPointInfo>>()

    fun getCurrentSession(uuid: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val session =sessionRepo.getCurrentSession(uuid)
            //currentBitmap.postValue(session.images)
            sessionName.postValue(session.sessionLabel)
            accessPointRepo.getAllAccessPoints(uuid).collect { list ->
                accessPointInfoList.postValue(list.map {
                    AccessPointInfo(it.floor.toString(), it.ssid, it.uuid)
                } as MutableList<AccessPointInfo>)

            }

        }
    }

    /*init {
        viewModelScope.launch(Dispatchers.IO) {
            accessPointRepo.createNewLocation(APLocation(0, "268288bf-b4c8-4159-898d-136615985cd1", 0f, 0f, 0, "02:15:b2:00:01:00"))
            accessPointRepo.createNewLocation(APLocation(0, "268288bf-b4c8-4159-898d-136615985cd1", 0f, 0f, 1, "02:15:b2:00:01:00"))
            accessPointRepo.createNewLocation(APLocation(0, "268288bf-b4c8-4159-898d-136615985cd1", 0f, 0f, 2, "02:15:b2:00:01:00"))
        }

    }*/
}