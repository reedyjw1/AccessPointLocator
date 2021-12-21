package edu.udmercy.accesspointlocater.features.session.room

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class AccessPoint(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo
    val uuid: String,
    @ColumnInfo
    val currentLocationX: Float,
    @ColumnInfo
    val currentLocationY: Float,
    @ColumnInfo
    val floor: Int,
    @ColumnInfo
    val distance: Double,
)
