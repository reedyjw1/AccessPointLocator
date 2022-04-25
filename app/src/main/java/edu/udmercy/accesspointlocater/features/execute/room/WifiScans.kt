package edu.udmercy.accesspointlocater.features.execute.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import edu.udmercy.accesspointlocater.features.viewSession.room.APLocation
import org.apache.commons.math3.distribution.WeibullDistribution
import kotlin.math.E
import kotlin.math.ceil
import kotlin.math.exp
import kotlin.math.pow

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

fun List<WifiScans>.estimateLocations(uuid: String, greaterThanFrequency: Int = 0): List<APLocation> {
    val apLocationList = mutableListOf<APLocation>()
    val ssidList = map { it.ssid }.distinct()
    val weibullDistribution = WeibullDistribution(105.0, 4.5)

    for (ssid in ssidList) {
        var wifiScansFiltered = filter { it.ssid == ssid }.filter { it.level >= -75 }
        if (greaterThanFrequency > 0) {
            wifiScansFiltered = wifiScansFiltered.filter { it.frequency > greaterThanFrequency}
        }
        val suspectedFloor = wifiScansFiltered.groupingBy { it.floor }.eachCount().maxByOrNull { it.value }?.key ?: continue
        var sumX = 0.0
        var sumY = 0.0
        var weightSum = 0.0
        wifiScansFiltered.filter { it.floor == suspectedFloor }.getTopPercentile(0.25f).forEach {
            // val weight = weibullDistribution.cumulativeProbability((it.level + 100).toDouble())
            // val weight = E.pow((it.level + 100).toDouble() / 18.0)
            val weight = it.level + 100
            sumX += (weight * it.currentLocationX)
            sumY += (weight * it.currentLocationY)
            weightSum += weight
        }
        val xLocation = sumX / weightSum
        val yLocation = sumY / weightSum
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

fun List<WifiScans>.getTopPercentile(percentile: Float): List<WifiScans> {
    val size = this.size
    val numberOfScans = ceil(percentile * size).toInt()
    return this.sortedByDescending { it.level }.take(numberOfScans)

}