package edu.udmercy.accesspointlocater.features.viewSession.repositories

import android.content.Context
import androidx.room.Room
import edu.udmercy.accesspointlocater.AppDatabase
import edu.udmercy.accesspointlocater.features.session.room.Session
import edu.udmercy.accesspointlocater.features.viewSession.model.AccessPointInfo
import edu.udmercy.accesspointlocater.features.viewSession.room.APLocation
import kotlinx.coroutines.flow.Flow

class APLocationRepositoryImpl(appContext: Context): APLocationRepository {
    private val apLocationRepo = Room.databaseBuilder(
        appContext,
        AppDatabase::class.java, "FingerPrintingDb"
    ).fallbackToDestructiveMigration().build().apLocationDao()

    override fun getAllAccessPoints(uuid: String): Flow<List<APLocation>> {
        return apLocationRepo.getAllLocations(uuid)
    }

    override fun createNewLocation(apLocation: APLocation) {
        apLocationRepo.insertAll(
            apLocation
        )
    }

    override fun saveAccessPointLocations(list: List<APLocation>) {
        apLocationRepo.insertAll(*list.toTypedArray())
    }
}