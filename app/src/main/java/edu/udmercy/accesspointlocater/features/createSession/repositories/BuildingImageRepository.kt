package edu.udmercy.accesspointlocater.features.createSession.repositories

import edu.udmercy.accesspointlocater.features.createSession.room.BuildingImage

/**
 * Interface to handle all functions that need to be implemented to show/delete the floor plans
 */
interface BuildingImageRepository {
    fun addImagesToSession(buildingImageList: List<BuildingImage>)
    fun getAllBitmapsFromSession(uuid: String): List<BuildingImage>
    fun getFloorImage(uuid: String, floor: Int): BuildingImage
    fun getFloorCount(uuid: String): Int
    fun deleteSession(uuid:String)
}