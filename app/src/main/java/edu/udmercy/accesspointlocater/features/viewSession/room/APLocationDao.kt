package edu.udmercy.accesspointlocater.features.viewSession.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface APLocationDao {
    @Query("SELECT * FROM APLocation where uuid == (:uuid)")
    fun getAllLocations(uuid: String): Flow<List<APLocation>>

    @Insert
    fun insertAll(vararg aps: APLocation)

    @Delete
    fun delete(ap: APLocation)

    @Query("DELETE FROM APLocation where uuid == (:uuid)")
    fun deleteAll(uuid: String)
}