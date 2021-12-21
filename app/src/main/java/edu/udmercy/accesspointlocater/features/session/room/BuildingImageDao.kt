package edu.udmercy.accesspointlocater.features.session.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BuildingImageDao {
    @Query("SELECT * FROM BuildingImage where uuid == (:uuid)")
    fun getAll(uuid: String): List<BuildingImage>

    @Insert
    fun insertAll(vararg images: BuildingImage)

    @Delete
    fun delete(image: BuildingImage)

    @Query("DELETE FROM BuildingImage where uuid==(:uuid)")
    fun deleteAllImages(uuid: String)
}