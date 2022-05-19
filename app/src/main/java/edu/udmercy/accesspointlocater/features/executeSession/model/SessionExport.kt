package edu.udmercy.accesspointlocater.features.executeSession.model

import edu.udmercy.accesspointlocater.features.executeSession.room.WifiScans
import edu.udmercy.accesspointlocater.features.home.room.Session

/**
 * This model class is used to get all the required data for exporting and convert it to JSON
 */
data class SessionExport(
    val session: Session,
    val wifiScans: List<WifiScans>,
)
