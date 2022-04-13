package edu.udmercy.accesspointlocater.features.accessPointChooser.repositories

import android.content.Context
import androidx.room.Room
import edu.udmercy.accesspointlocater.AppDatabase
import edu.udmercy.accesspointlocater.features.accessPointChooser.model.AccessPointUI
import edu.udmercy.accesspointlocater.features.accessPointChooser.room.AccessPointChooser

class AccessPointReferenceRepositoryImpl(private val appContext: Context): AccessPointReferenceRepository {

    private val accessPointChooserDao = Room.databaseBuilder(
        appContext,
        AppDatabase::class.java, "FingerPrintingDb"
    ).fallbackToDestructiveMigration().build().accessPointChooserDao()

    override fun saveAccessPointScan(ap: AccessPointUI, uuid: String, distance: Double) {
        accessPointChooserDao.insertAll(
            AccessPointChooser(0, ap.macAddress, uuid, ap.frequency, ap.rssi, distance)
        )
    }

    override fun getReferenceAccessPoint(uuid: String): AccessPointChooser {
        return accessPointChooserDao.getReferenceAccessPoint(uuid)
    }
}