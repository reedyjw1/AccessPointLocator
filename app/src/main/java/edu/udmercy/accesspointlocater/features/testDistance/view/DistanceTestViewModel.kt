package edu.udmercy.accesspointlocater.features.testDistance.view

import android.app.Application
import android.net.wifi.ScanResult
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import edu.udmercy.accesspointlocater.features.accessPointChooser.repositories.AccessPointReferenceRepository
import edu.udmercy.accesspointlocater.features.testDistance.model.DistanceUI
import edu.udmercy.accesspointlocater.utils.MathUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject

class DistanceTestViewModel(
    application: Application,
    private val savedStateHandle: SavedStateHandle
): AndroidViewModel(application), KoinComponent {

    private val apChooser: AccessPointReferenceRepository by inject()
    val accessPointList: MutableLiveData<MutableList<DistanceUI>> = MutableLiveData(mutableListOf())
    var isRunning = false

    fun updateList(list: List<ScanResult>) {
        viewModelScope.launch(Dispatchers.IO) {
            val uuid = savedStateHandle.getLiveData<String>("uuid").value ?: return@launch
            val ap = apChooser.getReferenceAccessPoint(uuid)

            val temp = list.map {
                DistanceUI(
                    it.BSSID,
                    it.level,
                    it.frequency,
                    MathUtils.calculateDistanceInMeters(
                        it.level, 3.0, ap.distance, ap.level.toDouble()
                    )
                )
            }.toMutableList()

            temp.sortByDescending { it.rssi }
            accessPointList.postValue(
                temp
            )
        }
    }
}