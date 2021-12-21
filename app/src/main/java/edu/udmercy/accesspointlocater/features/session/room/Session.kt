package edu.udmercy.accesspointlocater.features.session.room

import android.graphics.Bitmap
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
    val image: Bitmap,
    @ColumnInfo
    var isFinished: Boolean = false
)
