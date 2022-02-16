package edu.udmercy.accesspointlocater.features.execute.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

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