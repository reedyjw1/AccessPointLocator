package edu.udmercy.accesspointlocater.features.session.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AccessPoint(
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
