package edu.udmercy.accesspointlocater.features.session.repositories

import android.content.Context
import androidx.room.Room
import edu.udmercy.accesspointlocater.AppDatabase
import edu.udmercy.accesspointlocater.features.session.room.AccessPoint

class AccessPointRepositoryImpl(private val appContext: Context): AccessPointRepository {
    private val accessPointRepo = Room.databaseBuilder(
        appContext,
        AppDatabase::class.java, "FingerPrintingDb"
    ).build().accessPointDao()

    override fun saveAccessPointScan(list: List<AccessPoint>) {
        accessPointRepo.insertAll(*list.toTypedArray())
    }
}