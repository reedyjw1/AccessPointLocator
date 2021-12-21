package edu.udmercy.accesspointlocater.features.session.repositories

import android.content.Context
import androidx.room.Room
import edu.udmercy.accesspointlocater.AppDatabase

class SessionRepositoryImpl(private val appContext: Context): SessionRepository {
    private val sessionDb = Room.databaseBuilder(
        appContext,
        AppDatabase::class.java, "FingerPrintingDb"
    ).build().sessionDao()
}