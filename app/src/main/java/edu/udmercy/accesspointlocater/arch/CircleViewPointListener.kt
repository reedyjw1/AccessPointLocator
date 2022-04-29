package edu.udmercy.accesspointlocater.arch

import android.graphics.PointF

/**
 * Interface to notify a Fragment when the image is clicked, and where.
 */
interface CircleViewPointListener {
    fun onPointsChanged(currentPoint: PointF?)
}