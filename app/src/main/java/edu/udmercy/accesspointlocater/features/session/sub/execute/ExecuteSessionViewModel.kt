package edu.udmercy.accesspointlocater.features.session.sub.execute

import android.graphics.Bitmap
import android.graphics.PointF
import android.net.wifi.ScanResult
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.udmercy.accesspointlocater.features.session.repositories.SessionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject
import kotlin.math.abs
import kotlin.math.log10
import kotlin.math.pow

class ExecuteSessionViewModel: ViewModel(), KoinComponent {
    private val sessionRepo: SessionRepository by inject()
    val currentBitmap: MutableLiveData<Bitmap> = MutableLiveData<Bitmap>()
    val sessionName: MutableLiveData<String> = MutableLiveData()
    var currentPosition: PointF? = null
    val allowedNumberOfPoints = MutableLiveData<Int>(1)

    fun getCurrentSession(uuid: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val session =sessionRepo.getCurrentSession(uuid)
            currentBitmap.postValue(session.image)
            sessionName.postValue(session.sessionLabel)

        }
    }

    fun saveResults(list: List<ScanResult>, uuid: String) {
        viewModelScope.launch(Dispatchers.IO) {
            list.forEach {
                val distance = calculateDistanceInMeters(it.level, it.frequency)
                val session = sessionRepo.getCurrentSession(uuid)
            }
        }
    }

    private fun calculateDistanceInMeters(signalLevelInDb: Int, freqInMHz: Int): Double {
        val exp = (27.55 - 20 * log10(freqInMHz.toDouble()) + abs(signalLevelInDb)) / 20.0
        val dist = 10.0.pow(exp)
        return (dist *100.0 ) / 1000.0
    }
}