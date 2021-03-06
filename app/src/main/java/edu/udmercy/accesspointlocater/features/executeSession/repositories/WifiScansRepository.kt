package edu.udmercy.accesspointlocater.features.executeSession.repositories

import edu.udmercy.accesspointlocater.features.executeSession.room.WifiScans
import kotlinx.coroutines.flow.Flow

/**
 * Interface which has all functions that need to be implemented to access Wifi Data in the database
 */
interface WifiScansRepository {
    fun saveAccessPointScan(ap: WifiScans)
    fun getAllScans(uuid: String): Flow<List<WifiScans>>
    fun getScanList(uuid: String): List<WifiScans>
    fun deleteSession(uuid:String)
    fun insertRoomNumber(scanUUID: String, roomNumber:String)
    fun retrieveRoomNumbers(uuid: String): List<String>
}