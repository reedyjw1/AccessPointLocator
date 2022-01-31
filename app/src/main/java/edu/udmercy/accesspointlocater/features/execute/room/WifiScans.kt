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
    val distance: Double,
    @ColumnInfo
    val ssid: String
)
