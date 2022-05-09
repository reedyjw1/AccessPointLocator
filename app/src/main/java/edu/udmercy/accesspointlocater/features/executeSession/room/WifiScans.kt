package edu.udmercy.accesspointlocater.features.executeSession.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import edu.udmercy.accesspointlocater.features.viewSession.room.APLocation
import org.apache.commons.math3.distribution.WeibullDistribution
import kotlin.math.ceil

/**
 * Model class to be used as fields in the table/database for the WifiScan data that is collected
 */
@Entity
data class WifiScans(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo
    val uuid: String,
    @ColumnInfo
    val scanUUID: String,
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
    @ColumnInfo
    val roomNumber: String,
)

fun List<WifiScans>.average(): Double {
    var total = 0.0
    this.forEach {
        total += it.currentLocationZ
    }
    return  total/this.size
}

/**
 * Extension function to do the post processing
 */
fun List<WifiScans>.estimateLocations(uuid: String, greaterThanFrequency: Int = 0): List<APLocation> {
    val apLocationList = mutableListOf<APLocation>()
    // Gets all unique ssids (total number of access points found)
    val ssidList = map { it.ssid }.distinct()
    // Can be used if we would like to filter based on a distribution
    val weibullDistribution = WeibullDistribution(105.0, 4.5)

    // For each Access point
    for (ssid in ssidList) {
        // Get all scans of an access point with RSSI greater than -75 (filters noisy or untrustworthy data)
        var wifiScansFiltered = filter { it.ssid == ssid }.filter { it.level >= -75 }
        if (greaterThanFrequency > 0) {
            // Removes the 2.7 GHz band if wanted (Since now a lot of aps have both 2.7GHz and 5Ghz in the same AP)
            wifiScansFiltered = wifiScansFiltered.filter { it.frequency > greaterThanFrequency}
        }
        // Make the suspected floor the floor with the most scans to the access point
        val suspectedFloor = wifiScansFiltered.groupingBy { it.floor }.eachCount().maxByOrNull { it.value }?.key ?: continue
        var sumX = 0.0
        var sumY = 0.0
        var weightSum = 0.0
        // Get top 25% of wifi scans by RSSI and average their scan location to get estimation
        val top25Percentile =  wifiScansFiltered.filter { it.floor == suspectedFloor }.getTopPercentile(0.25f)
       top25Percentile.forEach {
            // val weight = weibullDistribution.cumulativeProbability((it.level + 100).toDouble())
            // val weight = E.pow((it.level + 100).toDouble() / 18.0)
            val weight = it.level + 100
            sumX += (weight * it.currentLocationX)
            sumY += (weight * it.currentLocationY)
            weightSum += weight
        }
        // Estimate the room number
        val roomList = mutableListOf<String>()
        val roomCountHashMap = top25Percentile.groupingBy { it.roomNumber }.eachCount()
        val highestCount = roomCountHashMap.maxOfOrNull { it.value } ?: continue
        roomCountHashMap.forEach {
            if(it.value == highestCount && !roomList.contains(it.key)) {
                roomList.add(it.key)
            }
        }
        // Adds the newly found estimated location to a list with the other estimated locations
        val xLocation = sumX / weightSum
        val yLocation = sumY / weightSum
        val apEstimate = APLocation(
            id = 0,
            uuid = uuid,
            xCoordinate = xLocation,
            yCoordinate = yLocation,
            zCoordinate = 0.0,
            floor = suspectedFloor,
            ssid = ssid,
            roomNumber = roomList
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