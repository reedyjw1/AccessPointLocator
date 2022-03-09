package edu.udmercy.accesspointlocater.utils

import android.graphics.PointF
import kotlin.math.*

object MathUtils {
    fun euclideanDistance(point1: PointF, point2: PointF): Float {
        val x: Float = (abs(point1.x - point2.x)).pow(2)
        val y: Float = (abs(point1.y - point2.y)).pow(2)
        val res: Float = x + y
        return sqrt(res)
    }

    fun calculateDistanceInMeters(rssi: Int, n: Int, freeSpacePathLoss: Double): Double {
        val temp = freeSpacePathLoss - rssi.toDouble()
        return 10.0.pow(temp / (10.0 * n.toDouble()))
    }

    fun calculateFreeSpacePathLosReference(frequency: Double, meters: Double = 1.0, n: Int): Double{
        // 32.44 + 10𝑛 log (𝑑) + 10𝑛 log (𝑓)
        return 32.44 + (10 * n * log(meters, 10.0)) + (10 * n * log(frequency, 10.0))
    }

    /*fun calculateDistanceInMeters(signalLevelInDb: Int, freqInMHz: Int): Double {
        val exp = (27.55 - 20 * log10(freqInMHz.toDouble()) + abs(signalLevelInDb)) / 20.0
        val dist = 10.0.pow(exp)
        return (dist *100.0 ) / 1000.0
    }*/

    fun convertUnitToMeters(distance: Double, unit: Units): Double {
        return when (unit) {
            Units.METERS -> {
                distance
            }
            Units.FEET -> {
                distance / 3.281
            }
            Units.INCHES-> {
                distance / 39.37
            }
        }
    }

    fun calculateHeightFromFloors(floorHeights: List<Double>, offset: Double, floor: Int): Double {
        // Converts the inputted floor heights to height calculations and converts them to meters
        var floorCounter = 0.0
        if(floor == 0) {
            return offset
        } else {
            for (i in 0 until floor) {
                floorCounter+=floorHeights[i]
            }
        }
        return floorCounter+offset
    }
}