package edu.udmercy.accesspointlocater.features.session.sub.execute

import android.graphics.Bitmap
import android.graphics.PointF
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.udmercy.accesspointlocater.features.session.repositories.SessionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject

class ExecuteSessionViewModel: ViewModel(), KoinComponent {
    private val sessionRepo: SessionRepository by inject()
    val currentBitmap: MutableLiveData<Bitmap> = MutableLiveData<Bitmap>()
    val sessionName: MutableLiveData<String> = MutableLiveData()
    var currentPosition: PointF? = null
    val allowedNumberOfPoints = MutableLiveData<Int>(2)

    fun getCurrentSession(uuid: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val session =sessionRepo.getCurrentSession(uuid)
            currentBitmap.postValue(session.image)
            sessionName.postValue(session.sessionLabel)

        }
    }
}