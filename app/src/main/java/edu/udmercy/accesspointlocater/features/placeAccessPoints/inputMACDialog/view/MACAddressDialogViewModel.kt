package edu.udmercy.accesspointlocater.features.placeAccessPoints.inputMACDialog.view

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import org.koin.core.KoinComponent

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