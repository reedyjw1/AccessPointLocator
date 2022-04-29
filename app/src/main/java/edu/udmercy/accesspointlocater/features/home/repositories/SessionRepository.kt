package edu.udmercy.accesspointlocater.features.home.repositories

import edu.udmercy.accesspointlocater.features.home.room.Session
import kotlinx.coroutines.flow.Flow

/**
 * Interface of all required functions for access database about Session data
 */
interface SessionRepository {
    fun createNewSession(uuid: String, timestamp: String, sessionLabel: String, buildingName: String, areApLocationsKnown: Boolean)
    fun getAllSessions(): Flow<List<Session>>
    fun getCurrentSession(uuid: String): Session
    fun updateSession(session: Session)
    fun markSessionComplete(uuid:String)
    fun deleteSession(uuid:String)
}