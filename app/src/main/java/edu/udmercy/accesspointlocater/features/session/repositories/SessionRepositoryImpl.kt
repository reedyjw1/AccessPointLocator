package edu.udmercy.accesspointlocater.features.session.repositories

import android.content.Context
import android.graphics.Bitmap
import androidx.room.Room
import edu.udmercy.accesspointlocater.AppDatabase
import edu.udmercy.accesspointlocater.features.session.room.Session
import kotlinx.coroutines.flow.Flow

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
    ) {
        sessionDb.insertAll(
            Session(
                uuid = uuid,
                sessionLabel = sessionLabel,
                timestamp = timestamp,
                building = buildingName
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
}