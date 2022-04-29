package edu.udmercy.accesspointlocater.features.home.repositories

import android.content.Context
import androidx.room.Room
import edu.udmercy.accesspointlocater.AppDatabase
import edu.udmercy.accesspointlocater.features.home.room.Session
import kotlinx.coroutines.flow.Flow

/**
 * Implementation of interface to access database about session data
 */
class SessionRepositoryImpl(private val appContext: Context): SessionRepository {
    private val sessionDb = Room.databaseBuilder(
        appContext,
        AppDatabase::class.java, "FingerPrintingDb"
    ).fallbackToDestructiveMigration().build().sessionDao()

    override fun createNewSession(
        uuid: String,
        timestamp: String,
        sessionLabel: String,
        buildingName: String,
        areApLocationsKnown: Boolean
    ) {
        sessionDb.insertAll(
            Session(
                uuid = uuid,
                sessionLabel = sessionLabel,
                timestamp = timestamp,
                building = buildingName,
                areApLocationsKnown = areApLocationsKnown
            )
        )
    }

    override fun getAllSessions(): Flow<List<Session>> {
        return sessionDb.getAll()
    }

    override fun getCurrentSession(uuid: String): Session {
        return sessionDb.getCurrentSession(uuid)
    }

    override fun updateSession(session: Session) {
        sessionDb.insertAll(session)
    }

    override fun markSessionComplete(uuid: String) {
        sessionDb.markAsFinished(uuid)
    }

    override fun deleteSession(uuid: String) {
        sessionDb.deleteAllSessions(uuid)
    }
}