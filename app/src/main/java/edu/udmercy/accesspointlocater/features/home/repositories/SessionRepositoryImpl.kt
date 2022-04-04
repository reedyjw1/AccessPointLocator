package edu.udmercy.accesspointlocater.features.home.repositories

import android.content.Context
import androidx.room.Room
import edu.udmercy.accesspointlocater.AppDatabase
import edu.udmercy.accesspointlocater.features.home.room.Session
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
        scaleNumber: Double,
        scaleUnits: String,
        pixelDistance: Double,
        areApLocationsKnown: Boolean
    ) {
        sessionDb.insertAll(
            Session(
                uuid = uuid,
                sessionLabel = sessionLabel,
                timestamp = timestamp,
                building = buildingName,
                scaleNumber = scaleNumber,
                scaleUnits = scaleUnits,
                pixelDistance = pixelDistance,
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
}