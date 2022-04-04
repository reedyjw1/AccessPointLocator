package edu.udmercy.accesspointlocater.features.home.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Session(
    @PrimaryKey
    val uuid: String = UUID.randomUUID().toString(),
    @ColumnInfo
    val sessionLabel: String,
    @ColumnInfo
    val timestamp: String,
    @ColumnInfo
    val building: String,
    @ColumnInfo
    var isFinished: Boolean = false,
    @ColumnInfo
    var scaleNumber: Double = 0.0,
    @ColumnInfo
    var scaleUnits: String = "Meter",
    @ColumnInfo
    var pixelDistance: Double = 0.0,
    @ColumnInfo
    val areApLocationsKnown: Boolean = false
)
