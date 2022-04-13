package edu.udmercy.accesspointlocater.features.placeAccessPoints.model

import android.graphics.PointF

interface TouchPointListener {
    fun onPointAdded(point: PointF)
    fun onPointRemoved(point: PointF)
}