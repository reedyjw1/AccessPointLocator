package edu.udmercy.accesspointlocater.features.session.room

import androidx.room.*

@Dao
interface SessionDAO {
    @Query("SELECT * FROM Session")
    fun getAll(): List<Session>

    @Insert
    fun insertAll(vararg users: Session)

    @Delete
    fun delete(user: Session)

    @Query("DELETE FROM Session where uuid==(:uuid)")
    fun deleteAllSessions(uuid: String)
}