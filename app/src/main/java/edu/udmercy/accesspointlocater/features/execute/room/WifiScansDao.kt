package edu.udmercy.accesspointlocater.features.execute.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * This is a feature of Jetpack Room, a Google Library that allows functions to be directly correlated
 * to SQL functions. These functions are used to insert, retrieve, and delete WiFi records per session
 */
@Dao
interface WifiScansDao {
    @Query("SELECT * FROM WifiScans where uuid == (:uuid)")
    fun getAllFromSession(uuid: String): Flow<List<WifiScans>>

    @Query("SELECT * FROM WifiScans where uuid == (:uuid)")
    fun getScansFromSession(uuid: String): List<WifiScans>

    @Insert
    fun insertAll(vararg wifiScans: WifiScans)

    @Delete
    fun delete(wifiScans: WifiScans)

    @Query("DELETE FROM WifiScans where uuid == (:uuid)")
    fun deleteAllFromSession(uuid: String)

    @Query("UPDATE WifiScans SET roomNumber = (:roomNumber) WHERE scanUUID == (:scanUUID)")
    fun insertRoomNumber(scanUUID: String, roomNumber:Int)
}