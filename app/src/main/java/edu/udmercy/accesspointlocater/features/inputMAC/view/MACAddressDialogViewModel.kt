package edu.udmercy.accesspointlocater.features.inputMAC.view

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import edu.udmercy.accesspointlocater.features.execute.repositories.WifiScansRepository
import edu.udmercy.accesspointlocater.features.viewSession.repositories.APLocationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject

class MACAddressDialogViewModel(application: Application, savedStateHandle: SavedStateHandle): AndroidViewModel(application), KoinComponent {

    val roomNumberList = MutableLiveData(listOf<String>())
    val lastRoomLiveData = MutableLiveData("")
    var enteredRoomNumber = false

    companion object {
        private const val TAG = "MacAddrViewModel"
    }

    init {
        savedStateHandle.getLiveData<String>("lastRoom").value?.let {
            Log.i(TAG, "posting: $it")
            lastRoomLiveData.postValue(it)
        }
        savedStateHandle.getLiveData<List<String>>("roomList").value?.let {
            roomNumberList.postValue(it)
        }
    }
}