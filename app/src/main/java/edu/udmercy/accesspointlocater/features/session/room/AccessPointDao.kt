package edu.udmercy.accesspointlocater.features.session.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AccessPointDao {
    @Query("SELECT * FROM AccessPoint where uuid == (:uuid)")
    fun getAllFromSession(uuid: String): Flow<List<AccessPoint>>

    @Insert
    fun insertAll(vararg accessPoints: AccessPoint)

    @Delete
    fun delete(accessPoint: AccessPoint)

    @Query("DELETE FROM AccessPoint where uuid == (:uuid)")
    fun deleteAllFromSession(uuid: String)
}