package edu.udmercy.accesspointlocater.features.session.repositories

import android.content.Context
import androidx.room.Room
import edu.udmercy.accesspointlocater.AppDatabase
import edu.udmercy.accesspointlocater.features.session.room.AccessPoint
import kotlinx.coroutines.flow.Flow

class AccessPointRepositoryImpl(private val appContext: Context): AccessPointRepository {
    private val accessPointRepo = Room.databaseBuilder(
        appContext,
        AppDatabase::class.java, "FingerPrintingDb"
    ).fallbackToDestructiveMigration().build().accessPointDao()

    override fun saveAccessPointScan(ap: AccessPoint) {
        accessPointRepo.insertAll(ap)
    }

    override fun getAllScans(uuid: String): Flow<List<AccessPoint>> {
        return accessPointRepo.getAllFromSession(uuid)
    }
}