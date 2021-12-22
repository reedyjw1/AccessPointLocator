package edu.udmercy.accesspointlocater.features.session.repositories

import edu.udmercy.accesspointlocater.features.session.room.AccessPoint

interface AccessPointRepository {
    fun saveAccessPointScan(ap: AccessPoint)
}