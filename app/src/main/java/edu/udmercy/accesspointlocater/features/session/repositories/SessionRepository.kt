package edu.udmercy.accesspointlocater.features.session.repositories

import edu.udmercy.accesspointlocater.features.session.room.Session
import kotlinx.coroutines.flow.Flow

interface SessionRepository {
    fun createNewSession(timestamp: String, sessionLabel: String, buildingName: String, path: String)
    fun getAllSessions(): Flow<List<Session>>
}