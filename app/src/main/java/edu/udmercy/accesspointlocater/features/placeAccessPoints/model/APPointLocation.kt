package edu.udmercy.accesspointlocater.features.placeAccessPoints.model

import android.graphics.PointF

/**
 * Data Model to be used on the UI. APLocation data will be mapped to this and presented on the UI
 * The companion object is utilized by the recycler adapter to automatically know when the data changes
 */
class APPointLocation(
    val point: PointF,
    val floor: Int,
    val macAddress: String,
    val roomNumber: String,
) {

    fun logValues(): String{
        return "\nMAC: ${macAddress}\nPoint: ${point}\nFloor: $floor"
    }

}

