package edu.udmercy.accesspointlocater.features.viewSession.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Data model in which fields are table columns used for saving AP estimated locations
 */
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
