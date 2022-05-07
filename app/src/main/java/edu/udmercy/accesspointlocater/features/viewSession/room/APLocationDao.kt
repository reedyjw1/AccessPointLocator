package edu.udmercy.accesspointlocater.features.viewSession.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * This is a feature of Jetpack Room, a Google Library that allows functions to be directly correlated
 * to SQL functions. These functions are used to insert, retrieve, and delete records in the database related to estimated
 * APLocations per session
 */
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

    @Query("SELECT roomNumber from APLocation where uuid == (:sessionUuid)")
    fun getRoomNumbers(sessionUuid: String): List<String>
}