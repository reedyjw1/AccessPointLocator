package edu.udmercy.accesspointlocater.features.executeSession.roomInputDialog.view

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import edu.udmercy.accesspointlocater.features.executeSession.repositories.WifiScansRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject

class RoomInputViewModel(application: Application, savedStateHandle: SavedStateHandle): AndroidViewModel(application), KoinComponent {

    private val wifiScansRepo: WifiScansRepository by inject()
    val roomNumberList = MutableLiveData(listOf<String>())
    val lastRoomLiveData = MutableLiveData("")
    var enteredRoomNumber = false

    companion object {
        private const val TAG = "RoomInputViewModel"
    }

    init {
        savedStateHandle.getLiveData<String>("uuid").value?.let {
            retrieveRoomNumberList(it)
        }
        savedStateHandle.getLiveData<String>("lastRoom").value?.let {
            Log.i(TAG, "posting: $it")
            lastRoomLiveData.postValue(it)
        }
    }

    private fun retrieveRoomNumberList(sessionUuid: String) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.i(TAG, "retrieveRoomNumberList: calling Rooms")
            val rooms = wifiScansRepo.retrieveRoomNumbers(sessionUuid).distinct()
            Log.i(TAG, "retrieveRoomNumberList: rooms=$rooms")
            roomNumberList.postValue(rooms)
        }
    }
}