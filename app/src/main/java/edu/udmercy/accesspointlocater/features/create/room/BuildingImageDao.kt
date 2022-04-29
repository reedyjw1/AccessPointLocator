package edu.udmercy.accesspointlocater.features.create.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

/**
 * This is a feature of Jetpack Room, a Google Library that allows functions to be directly correlated
 * to SQL functions. These functions are used to insert, retrieve, and delete records in the database related to floor
 * plans per session
 */
@Dao
interface BuildingImageDao {
    @Query("SELECT * FROM BuildingImage where uuid == (:uuid)")
    fun getAll(uuid: String): List<BuildingImage>

    @Query("SELECT * FROM BuildingImage where uuid == (:uuid) and floor == (:floor)")
    fun getFloorImage(uuid: String, floor: Int): BuildingImage

    @Query("SELECT COUNT(id) FROM BuildingImage where uuid == (:uuid)")
    fun getFloorCount(uuid: String): Int

    @Insert
    fun insertAll(vararg images: BuildingImage)

    @Delete
    fun delete(image: BuildingImage)

    @Query("DELETE FROM BuildingImage where uuid==(:uuid)")
    fun deleteAllImages(uuid: String)

}