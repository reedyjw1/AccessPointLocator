package edu.udmercy.accesspointlocater.features.accessPointChooser.view

import android.net.wifi.ScanResult
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import edu.udmercy.accesspointlocater.features.accessPointChooser.model.AccessPointUI
import org.koin.core.KoinComponent

class AccessPointChooserViewModel: ViewModel(), KoinComponent {

    val accessPointList: MutableLiveData<MutableList<AccessPointUI>> = MutableLiveData(mutableListOf())

    fun saveReferenceAccessPointData(data: AccessPointUI, uuid: String) {

    }

    fun updateList(list: List<ScanResult>) {
        accessPointList.postValue(
            list.map { AccessPointUI(it.BSSID, it.level, it.frequency) }.toMutableList()
        )
    }
}