package edu.udmercy.accesspointlocater.features.placeAccessPoints.model

import android.graphics.PointF

class APPointLocation(
    val point: PointF,
    val floor: Int,
    val macAddress: String
) {

    fun logValues(): String{
        return "\nMAC: ${macAddress}\nPoint: ${point}\nFloor: $floor"
    }

}

