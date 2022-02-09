package edu.udmercy.accesspointlocater.features.create.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

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