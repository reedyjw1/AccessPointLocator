package edu.udmercy.accesspointlocater.features.accessPointChooser.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Table Structure for the Database, this class was used for selecting a reference access point
 * per session, however, it is no longer used.
 */
@Entity
data class AccessPointChooser(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo
    val bssid: String,
    @ColumnInfo
    val sessionUuid: String,
    @ColumnInfo
    val frequency: Int,
    @ColumnInfo
    val level: Int,
    @ColumnInfo
    val distance: Double,
)