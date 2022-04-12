package edu.udmercy.accesspointlocater.features.execute.room

import android.util.Log
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import edu.udmercy.accesspointlocater.features.viewSession.room.APLocation
import edu.udmercy.accesspointlocater.utils.*
import org.apache.commons.math3.distribution.WeibullDistribution

@Entity
data class WifiScans(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo
    val uuid: String,
    @ColumnInfo
    val currentLocationX: Double,
    @ColumnInfo
    val currentLocationY: Double,
    @ColumnInfo
    val currentLocationZ: Double,
    @ColumnInfo
    val floor: Int,
    @ColumnInfo
    val ssid: String,
    @ColumnInfo
    val capabilities: String,
    @ColumnInfo
    val centerFreq0: Int,
    @ColumnInfo
    val centerFreq1: Int,
    @ColumnInfo
    val channelWidth: Int,
    @ColumnInfo
    val frequency: Int,
    @ColumnInfo
    val level: Int,
    @ColumnInfo
    val timestamp: Long,
)

fun List<WifiScans>.average(): Double {
    var total = 0.0
    this.forEach {
        total += it.currentLocationZ
    }
    return  total/this.size
}

fun List<WifiScans>.estimateLocations(uuid: String): List<APLocation> {
    val apLocationList = mutableListOf<APLocation>()
    val ssidList = map { it.ssid }.distinct()
    val weibullDistribution = WeibullDistribution(105.0, 4.5)

    for (ssid in ssidList) {
        val wifiScansFiltered = filter { it.ssid == ssid }.filter { it.level >= -75 }.filter { it.frequency > 5000 }
        val suspectedFloor = wifiScansFiltered.groupingBy { it.floor }.eachCount().maxByOrNull { it.value }?.key ?: continue
        var sumX = 0.0
        var sumY = 0.0
        var sumProcRSSI = 0.0
        wifiScansFiltered.filter { it.floor == suspectedFloor }.forEach {
            val procRSSI = it.level + 100
            sumX += (procRSSI * it.currentLocationX)
            sumY += (procRSSI * it.currentLocationY)
            sumProcRSSI += procRSSI
        }
        val xLocation = sumX / sumProcRSSI
        val yLocation = sumY / sumProcRSSI
        val apEstimate = APLocation(
            id = 0,
            uuid = uuid,
            xCoordinate = xLocation,
            yCoordinate = yLocation,
            zCoordinate = 0.0,
            floor = suspectedFloor,
            ssid = ssid
        )
        apLocationList.add(apEstimate)

    }
    return apLocationList

}