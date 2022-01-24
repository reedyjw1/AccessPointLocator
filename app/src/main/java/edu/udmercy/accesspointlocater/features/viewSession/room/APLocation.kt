package edu.udmercy.accesspointlocater.features.viewSession.room

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
    val xCoordinate: Float,
    @ColumnInfo
    val yCoordinate: Float,
    @ColumnInfo
    val floor: Int,
    @ColumnInfo
    val ssid: String,
)
