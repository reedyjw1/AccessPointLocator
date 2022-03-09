package edu.udmercy.accesspointlocater.features.accessPointChooser.view

import android.net.wifi.ScanResult
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.udmercy.accesspointlocater.features.accessPointChooser.model.AccessPointUI
import edu.udmercy.accesspointlocater.features.accessPointChooser.repositories.AccessPointReferenceRepository
import edu.udmercy.accesspointlocater.features.viewSession.repositories.APLocationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.KoinComponent
import org.koin.core.inject

class AccessPointChooserViewModel: ViewModel(), KoinComponent {

    companion object {
        private const val TAG = "AccessPointChooserViewM"
    }

    val accessPointList: MutableLiveData<MutableList<AccessPointUI>> = MutableLiveData(mutableListOf())
    private val chooserRepo: AccessPointReferenceRepository by inject()

    fun saveReferenceAccessPointData(uuid: String?, distance: Double, completion: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val selected = accessPointList.value?.firstOrNull() { it.selected } ?: return@launch
            uuid ?: return@launch

            chooserRepo.saveAccessPointScan(selected, uuid, distance)

            withContext(Dispatchers.Main) {
                completion()
            }
        }

    }

    fun updateList(list: List<ScanResult>) {
        accessPointList.postValue(
            list.map { AccessPointUI(it.BSSID, it.level, it.frequency, false) }.toMutableList()
        )
    }

    fun accessPointClicked(ap: AccessPointUI) {
        val temp = accessPointList.value?.map {
            if(ap.macAddress == it.macAddress){
                AccessPointUI(it.macAddress, it.rssi, it.frequency, true)
            } else {
                AccessPointUI(it.macAddress, it.rssi, it.frequency, false)
            }
        }?.toMutableList()

        accessPointList.postValue(temp)
    }
}