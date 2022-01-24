package edu.udmercy.accesspointlocater.features.session.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class APLocation(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo
    val uuid: String,
    @ColumnInfo
    val xCoordinate: Double,
    @ColumnInfo
    val yCoordinate: Double,
    @ColumnInfo
    val zCoordinate: Double,
    @ColumnInfo
    val floor: Int,
    @ColumnInfo
    val ssid: String,
)
