package edu.udmercy.accesspointlocater.features.accessPointChooser.repositories

import edu.udmercy.accesspointlocater.features.accessPointChooser.model.AccessPointUI
import edu.udmercy.accesspointlocater.features.accessPointChooser.room.AccessPointChooser

interface AccessPointReferenceRepository {
    fun saveAccessPointScan(ap: AccessPointUI, uuid: String, distance: Double)
    fun getReferenceAccessPoint(uuid: String): AccessPointChooser
}