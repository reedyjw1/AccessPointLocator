package edu.udmercy.accesspointlocater.features.session.repositories

import android.content.Context
import androidx.room.Room
import edu.udmercy.accesspointlocater.AppDatabase
import edu.udmercy.accesspointlocater.features.session.room.Session

class SessionRepositoryImpl(private val appContext: Context): SessionRepository {
    private val sessionDb = Room.databaseBuilder(
        appContext,
        AppDatabase::class.java, "FingerPrintingDb"
    ).build().sessionDao()

    override fun createNewSession(
        timestamp: String,
        sessionLabel: String,
        buildingName: String,
        path: String
    ) {
        sessionDb.insertAll(
            Session(
                sessionLabel = sessionLabel,
                timestamp = timestamp,
                path = path,
                building = buildingName
            )
        )
    }
}