package edu.udmercy.accesspointlocater.utils

import Jama.Matrix
import android.graphics.PointF
import kotlin.math.*

object MathUtils {

    fun euclideanDistance(point1: PointF, point2: PointF): Float {
        val x: Float = (abs(point1.x - point2.x)).pow(2)
        val y: Float = (abs(point1.y - point2.y)).pow(2)
        val res: Float = x + y
        return sqrt(res)
    }

    fun calculateDistanceInMeters(rssi: Int, n: Double, refDist: Double, refApLevel: Double): Double {
        val temp = refApLevel - rssi.toDouble()
        return 10.0.pow(temp / (10.0 * n)) * refDist
    }

    fun calculateFreeSpacePathLossReference(frequency: Double, meters: Double = 1.0): Double{
        // 32.44 + 10𝑛 log (𝑑) + 10𝑛 log (𝑓)
        // return 32.44 + (10 * buildingType * log(meters, 10.0)) + (10 * buildingType * log(frequency, 10.0))
        return (20 * log(meters, 10.0) * 20 * log(frequency * 1000000, 10.0) - 147.55)
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

fun Double.roundTo(decimals: Int): Double {
    val factor = 10.0.pow(decimals.toDouble())
    return (this * factor).roundToInt() / factor
}

fun strung(m: Matrix): String {
    val sb = StringBuffer()
    for (r in 0 until m.rowDimension) {
        for (c in 0 until m.columnDimension) sb.append(m.get(r, c)).append("\t")
        sb.append("\n")
    }
    return sb.toString()
}