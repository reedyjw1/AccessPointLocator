package edu.udmercy.accesspointlocater.features.session.sub.execute

import android.graphics.Bitmap
import android.graphics.PointF
import android.net.wifi.ScanResult
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.udmercy.accesspointlocater.features.session.repositories.AccessPointRepository
import edu.udmercy.accesspointlocater.features.session.repositories.BuildingImageRepository
import edu.udmercy.accesspointlocater.features.session.repositories.SessionRepository
import edu.udmercy.accesspointlocater.features.session.room.AccessPoint
import edu.udmercy.accesspointlocater.features.session.room.BuildingImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject
import kotlin.math.abs
import kotlin.math.log10
import kotlin.math.pow

class ExecuteSessionViewModel: ViewModel(), KoinComponent {
    private val sessionRepo: SessionRepository by inject()
    private val accessPointRepo: AccessPointRepository by inject()
    private val buildingImageRepo: BuildingImageRepository by inject()

    private var floorCount = -1
    val currentBitmap: MutableLiveData<BuildingImage> = MutableLiveData()
    val sessionName: MutableLiveData<String> = MutableLiveData()
    var currentPosition: PointF? = null
    var savedPoints: MutableLiveData<MutableList<PointF>> = MutableLiveData(mutableListOf())
    var floor: MutableLiveData<Int> = MutableLiveData(0)

    companion object {
        private const val TAG = "ExecuteSessionViewModel"
    }

    init {

    }

    fun getCurrentSession(uuid: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val floorValue = floor.value ?: return@launch
            val session =sessionRepo.getCurrentSession(uuid)
            val image = buildingImageRepo.getFloorImage(uuid, floorValue)
            floorCount = buildingImageRepo.getFloorCount(uuid)
            currentBitmap.postValue(null)
            currentBitmap.postValue(image)
            sessionName.postValue(session.sessionLabel)


        }
    }

    fun saveResults(list: List<ScanResult>, uuid: String) {
        viewModelScope.launch(Dispatchers.IO) {
            list.forEach {
                val distance = calculateDistanceInMeters(it.level, it.frequency)
                val session = sessionRepo.getCurrentSession(uuid)
                val position = currentPosition ?: return@launch
                val floorVal = floor.value ?: return@launch

                accessPointRepo.saveAccessPointScan(AccessPoint(
                    uuid = session.uuid,
                    currentLocationX = position.x,
                    currentLocationY =  position.y,
                    floor = floorVal,
                    distance = distance,
                    ssid = it.BSSID
                ))

            }
        }
    }

    fun moveImage(number: Int, uuid: String) {
        viewModelScope.launch(Dispatchers.IO) {
            if (number == 1 || number == -1) {
                if ((floor.value == floorCount-1 && number == 1) || (floor.value == 0 && number == -1)) return@launch
                val floorVal = floor.value ?: return@launch
                floor.postValue(floorVal+number)
                currentBitmap.postValue(null)
                val buildingImage = buildingImageRepo.getFloorImage(uuid, floorVal+number)
                currentBitmap.postValue(buildingImage)
            }
        }
    }

    fun onPause() {
        currentBitmap.value?.image?.recycle()
    }

    private fun calculateDistanceInMeters(signalLevelInDb: Int, freqInMHz: Int): Double {
        val exp = (27.55 - 20 * log10(freqInMHz.toDouble()) + abs(signalLevelInDb)) / 20.0
        val dist = 10.0.pow(exp)
        return (dist *100.0 ) / 1000.0
    }
}