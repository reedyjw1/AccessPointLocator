package edu.udmercy.accesspointlocater.features.home.room

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * This is a feature of Jetpack Room, a Google Library that allows functions to be directly correlated
 * to SQL functions. These functions are used to insert, retrieve, and delete sessions in the database
 */
@Dao
interface SessionDAO {
    @Query("SELECT * FROM Session")
    fun getAll(): Flow<List<Session>>

    @Query("SELECT * FROM Session where uuid==(:uuid)")
    fun getCurrentSession(uuid: String): Session

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg users: Session)

    @Delete
    fun delete(user: Session)

    @Query("DELETE FROM Session where uuid==(:uuid)")
    fun deleteAllSessions(uuid: String)

    @Query("UPDATE Session SET isFinished = 1 WHERE uuid=(:uuid)")
    fun markAsFinished(uuid: String)

}