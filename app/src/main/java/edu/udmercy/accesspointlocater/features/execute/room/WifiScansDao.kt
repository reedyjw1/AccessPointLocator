package edu.udmercy.accesspointlocater.features.execute.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

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
}