package edu.udmercy.accesspointlocater.features.home.repositories

import edu.udmercy.accesspointlocater.features.home.room.Session
import kotlinx.coroutines.flow.Flow

interface SessionRepository {
    fun createNewSession(uuid: String, timestamp: String, sessionLabel: String, buildingName: String, scaleNumber: Double, scaleUnits: String, pixelDistance: Double, areApLocationsKnown: Boolean)
    fun getAllSessions(): Flow<List<Session>>
    fun getCurrentSession(uuid: String): Session
    fun updateSession(session: Session)
    fun markSessionComplete(uuid:String)
    fun deleteSession(uuid:String)
}