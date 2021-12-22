package edu.udmercy.accesspointlocater.features.session.repositories

import edu.udmercy.accesspointlocater.features.session.room.AccessPoint
import kotlinx.coroutines.flow.Flow

interface AccessPointRepository {
    fun saveAccessPointScan(ap: AccessPoint)
    fun getAllScans(uuid: String): Flow<List<AccessPoint>>
}