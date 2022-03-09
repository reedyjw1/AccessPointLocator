package edu.udmercy.accesspointlocater.features.accessPointChooser.repositories

import edu.udmercy.accesspointlocater.features.accessPointChooser.model.AccessPointUI

interface AccessPointReferenceRepository {
    fun saveAccessPointScan(ap: AccessPointUI, uuid: String)
}