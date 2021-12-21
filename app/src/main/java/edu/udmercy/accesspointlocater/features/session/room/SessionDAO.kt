package edu.udmercy.accesspointlocater.features.session.room

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDAO {
    @Query("SELECT * FROM Session")
    fun getAll(): Flow<List<Session>>

    @Insert
    fun insertAll(vararg users: Session)

    @Delete
    fun delete(user: Session)

    @Query("DELETE FROM Session where uuid==(:uuid)")
    fun deleteAllSessions(uuid: String)
}