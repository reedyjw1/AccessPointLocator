package edu.udmercy.accesspointlocater.features.create.room

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class BuildingImage(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo
    val uuid: String,
    @ColumnInfo
    val image: Bitmap,
    @ColumnInfo
    val floor: Int,

)
