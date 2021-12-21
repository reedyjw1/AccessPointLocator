package edu.udmercy.accesspointlocater.features.session.repositories

import android.graphics.Bitmap
import edu.udmercy.accesspointlocater.features.session.room.BuildingImage

interface BuildingImageRepository {
    fun addImagesToSession(buildingImageList: List<BuildingImage>)
    fun getAllBitmapsFromSession(uuid: String): List<BuildingImage>
}