package edu.udmercy.accesspointlocater.features.session.repositories

import android.content.Context
import androidx.room.Room
import edu.udmercy.accesspointlocater.AppDatabase
import edu.udmercy.accesspointlocater.features.session.room.APLocation

class APLocationRepositoryImpl(appContext: Context): APLocationRepository {
    private val apLocationRepo = Room.databaseBuilder(
        appContext,
        AppDatabase::class.java, "FingerPrintingDb"
    ).fallbackToDestructiveMigration().build().apLocationDao()

    override fun saveAccessPointLocations(list: List<APLocation>) {
        apLocationRepo.insertAll(*list.toTypedArray())
    }


}