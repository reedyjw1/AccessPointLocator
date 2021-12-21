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
    ).build().sessionDao()

    override fun createNewSession(
        timestamp: String,
        sessionLabel: String,
        buildingName: String,
        image: Bitmap
    ) {
        sessionDb.insertAll(
            Session(
                sessionLabel = sessionLabel,
                timestamp = timestamp,
                image = image,
                building = buildingName
            )
        )
    }

    override fun getAllSessions(): Flow<List<Session>> {
        return sessionDb.getAll()
    }
}