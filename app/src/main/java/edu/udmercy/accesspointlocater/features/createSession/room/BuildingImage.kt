package edu.udmercy.accesspointlocater.features.createSession.room

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Model class that is to be the fields of the Table in the database related to the floor plans
 */
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
