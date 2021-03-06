package edu.udmercy.accesspointlocater.features.home.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

/**
 * Model data that is the format of the table in the database related to sessions
 */
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
    val areApLocationsKnown: Boolean = false
)
