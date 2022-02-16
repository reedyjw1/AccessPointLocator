package edu.udmercy.accesspointlocater.features.create.repositories

import edu.udmercy.accesspointlocater.features.create.room.BuildingImage

interface BuildingImageRepository {
    fun addImagesToSession(buildingImageList: List<BuildingImage>)
    fun getAllBitmapsFromSession(uuid: String): List<BuildingImage>
    fun getFloorImage(uuid: String, floor: Int): BuildingImage
    fun getFloorCount(uuid: String): Int
    fun getFloorHeights(uuid: String, floorCounts: Int): List<Double>
}