package edu.udmercy.accesspointlocater.utils

import android.graphics.PointF
import kotlin.math.pow
import kotlin.math.sqrt

object MathUtils {
    fun euclideanDistance(point1: PointF, point2: PointF): Float {
        val x = (point1.x - point2.x).pow(2)
        val y = (point1.y - point2.y).pow(2)
        return sqrt(x + y)
    }
}