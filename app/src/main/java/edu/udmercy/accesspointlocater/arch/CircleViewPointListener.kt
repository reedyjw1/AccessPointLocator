package edu.udmercy.accesspointlocater.arch

import android.graphics.PointF

interface CircleViewPointListener {
    fun onPointsChanged(list: List<PointF>)
}