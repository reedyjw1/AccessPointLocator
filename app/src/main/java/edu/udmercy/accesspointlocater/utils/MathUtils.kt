package edu.udmercy.accesspointlocater.utils

import android.graphics.PointF
import kotlin.math.abs
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.sqrt

object MathUtils {
    fun euclideanDistance(point1: PointF, point2: PointF): Float {
        val x = (point1.x - point2.x).pow(2)
        val y = (point1.y - point2.y).pow(2)
        return sqrt(x + y)
    }

    fun calculateDistanceInMeters(signalLevelInDb: Int, freqInMHz: Int): Double {
        val exp = (27.55 - 20 * log10(freqInMHz.toDouble()) + abs(signalLevelInDb)) / 20.0
        val dist = 10.0.pow(exp)
        return (dist *100.0 ) / 1000.0
    }

    fun convertUnitToMeters(distance: Double, unit: Units): Double {
        return when (unit) {
            Units.METERS -> {
                distance
            }
            Units.FEET -> {
                distance / 0.3048
            }
            Units.INCHES-> {
                distance / 0.0254
            }
        }
    }
}