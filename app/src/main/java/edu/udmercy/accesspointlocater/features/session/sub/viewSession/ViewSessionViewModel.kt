package edu.udmercy.accesspointlocater.features.session.sub.viewSession

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.udmercy.accesspointlocater.features.session.repositories.SessionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject

class ViewSessionViewModel: ViewModel(), KoinComponent {

    private val sessionRepo: SessionRepository by inject()
    val currentBitmap: MutableLiveData<Bitmap> = MutableLiveData<Bitmap>()
    val sessionName: MutableLiveData<String> = MutableLiveData()

    fun getCurrentSession(uuid: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val session =sessionRepo.getCurrentSession(uuid)
            //currentBitmap.postValue(session.images)
            sessionName.postValue(session.sessionLabel)

        }
    }
}