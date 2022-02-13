package edu.udmercy.accesspointlocater.features.execute.model

import edu.udmercy.accesspointlocater.features.create.room.BuildingImage
import edu.udmercy.accesspointlocater.features.execute.room.WifiScans
import edu.udmercy.accesspointlocater.features.home.room.Session

data class SessionExport(
    val session: Session,
    val wifiScans: List<WifiScans>,
    val images: List<BuildingImage>
)
