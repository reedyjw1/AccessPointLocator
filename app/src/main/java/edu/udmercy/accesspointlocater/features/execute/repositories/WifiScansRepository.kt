package edu.udmercy.accesspointlocater.features.execute.repositories

import edu.udmercy.accesspointlocater.features.execute.room.WifiScans
import kotlinx.coroutines.flow.Flow

interface WifiScansRepository {
    fun saveAccessPointScan(ap: WifiScans)
    fun getAllScans(uuid: String): Flow<List<WifiScans>>
    fun getScanList(uuid: String): List<WifiScans>
}