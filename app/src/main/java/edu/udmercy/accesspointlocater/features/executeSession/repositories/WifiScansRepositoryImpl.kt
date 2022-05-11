package edu.udmercy.accesspointlocater.features.executeSession.repositories

import android.content.Context
import androidx.room.Room
import edu.udmercy.accesspointlocater.AppDatabase
import edu.udmercy.accesspointlocater.features.executeSession.room.WifiScans
import kotlinx.coroutines.flow.Flow

/**
 * Implementation of the Interface to access WifiData in the database
 */
class WifiScansRepositoryImpl(private val appContext: Context): WifiScansRepository {
    private val accessPointRepo = Room.databaseBuilder(
        appContext,
        AppDatabase::class.java, "FingerPrintingDb"
    ).fallbackToDestructiveMigration().build().accessPointDao()

    override fun saveAccessPointScan(ap: WifiScans) {
        accessPointRepo.insertAll(ap)
    }

    override fun getAllScans(uuid: String): Flow<List<WifiScans>> {
        return accessPointRepo.getAllFromSession(uuid)
    }

    override fun getScanList(uuid: String): List<WifiScans> {
        return accessPointRepo.getScansFromSession(uuid)
    }

    override fun deleteSession(uuid: String) {
        accessPointRepo.deleteAllFromSession(uuid)
    }

    override fun insertRoomNumber(scanUUID: String, roomNumber:String){
        accessPointRepo.insertRoomNumber(scanUUID, roomNumber)
    }

    override fun retrieveRoomNumbers(uuid: String): List<String> {
        return accessPointRepo.getRoomNumbers(uuid)
    }
}