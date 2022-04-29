package edu.udmercy.accesspointlocater.features.placeAccessPoints.model

import android.graphics.PointF

/**
 * Interface to notify listener when points are added or removed.
 */
interface TouchPointListener {
    fun onPointAdded(point: PointF)
    fun onPointRemoved(point: PointF)
}