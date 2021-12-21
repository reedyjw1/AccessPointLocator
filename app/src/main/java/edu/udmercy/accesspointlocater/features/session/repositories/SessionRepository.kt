package edu.udmercy.accesspointlocater.features.session.repositories

import android.graphics.Bitmap
import edu.udmercy.accesspointlocater.features.session.room.Session
import kotlinx.coroutines.flow.Flow

interface SessionRepository {
    fun createNewSession(timestamp: String, sessionLabel: String, buildingName: String, image: Bitmap)
    fun getAllSessions(): Flow<List<Session>>
}