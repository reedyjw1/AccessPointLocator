package edu.udmercy.accesspointlocater.utils

import Jama.Matrix
import android.util.Log
import com.lemmingapex.trilateration.NonLinearLeastSquaresSolver
import com.lemmingapex.trilateration.TrilaterationFunction
import edu.udmercy.accesspointlocater.features.execute.model.FloorZ
import edu.udmercy.accesspointlocater.features.execute.room.WifiScans
import edu.udmercy.accesspointlocater.features.execute.room.average
import edu.udmercy.accesspointlocater.features.viewSession.room.APLocation
import org.apache.commons.math3.fitting.leastsquares.LevenbergMarquardtOptimizer
import kotlin.math.pow

object Multilateration {

    private const val TAG = "Multilateration"
    private const val buildingType = 3

    // Uses matrix library to execute multilateration (followed from paper)
    fun calculate(rps: List<ReferencePoint>): Matrix? {
        val aTemp2dArray: MutableList<DoubleArray> = mutableListOf()
        val bTemp2dArray: MutableList<DoubleArray> = mutableListOf()

        rps.forEach { referencePoint ->
            val aTempDoubleArray = DoubleArray(4)
            aTempDoubleArray[0] = 1.0
            aTempDoubleArray[1] = -2 * referencePoint.x
            aTempDoubleArray[2] = -2 * referencePoint.y
            aTempDoubleArray[3] = -2 * referencePoint.z

            val bTempDoubleArray = DoubleArray(1)
            bTempDoubleArray[0] = referencePoint.distance.pow(2) - referencePoint.x.pow(2) - referencePoint.y.pow(2)- referencePoint.z.pow(2)

            aTemp2dArray.add(aTempDoubleArray)
            bTemp2dArray.add(bTempDoubleArray)

        }


        val aMatrix = Matrix(aTemp2dArray.toTypedArray())
        val bMatrix = Matrix(bTemp2dArray.toTypedArray())

        return try {
            aMatrix.solve(bMatrix)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Calculates position of unknown location using known
     * reference points with respect to 3d space
     */
    fun calculateMultilateration(list: List<WifiScans>, uuid: String, refPoints: List<WifiScans>, pointDistance: Double, scaleValue: Double, scaleUnit: String, freeSpacePathLoss: Double, refDist: Double): List<APLocation> {
        val apLocationList = mutableListOf<APLocation>()
        val ssidList = list.map { it.ssid }.distinct()
        val scale = calculateScale(pointDistance, scaleValue, scaleUnit)
        // For each access poitn
        for (ssid in ssidList) {
            val positions = mutableListOf<ReferencePoint>()
            Log.i(TAG, "calculateMultilateration: scaleValue = ${scale}")
            // Filter reference points by ssid and only use when RSSI > -70
            for (item in list.filter { it.ssid == ssid }) {
                if(item.level >= -70) {
                    val rp = ReferencePoint(
                        item.currentLocationX / scale,
                        item.currentLocationY / scale,
                        item.currentLocationZ,
                        MathUtils.calculateDistanceInMeters(
                            item.level,
                            buildingType.toDouble(),
                            refDist,
                            freeSpacePathLoss
                        ).roundTo(2),
                        Units.METERS
                    )
                    positions.add(rp)
                    Log.i(TAG, "calculateMultilateration: x=${rp.x},y=${rp.y},z=${rp.z}, distance=${rp.distance}")
                }
            }

            for (item in positions){
                Log.i(TAG, "calculateMultilateration: distance=${item.distance}, x=${item.x}, y=${item.y}, z=${item.z},")
            }

            if(positions.isNotEmpty()) {
                // Calculate the AP location
                val data = calculate(positions)
                val solution = data?.array
                if (data != null) {
                    Log.i(TAG, "calculateMultilateration: matrix=${strung(data)}")
                }

                if (solution != null) {
                    Log.i(TAG, "calculateMultilateration: zHeight = ${solution[3][0]}")
                    // Calculate the floor from the estimated height of the AP
                    val floorZList = calculateFloorsFromZ(refPoints, .001)
                    val calculatedFloor =
                        floorZList.firstOrNull { solution[3][0] >= it.lowerBound && solution[3][0] <= it.upperBound }
                    // Save the AP data to the list with other aps
                    val ap = APLocation(
                        0,
                        uuid,
                        solution[1][0] * scale, // Applies distance scale back so that it can be displayed on the map (meters -> pixels)
                        solution[2][0] * scale,
                        solution[3][0],
                        calculatedFloor?.floor ?: -1,
                        ssid
                    )
                    apLocationList.add(ap)
                    Log.i(TAG, "calculateMultilateration: APLocation (meters): x=${solution[1][0]},y=${solution[2][0]},z=${solution[3][0]}")
                }
            }
        }
        return apLocationList
    }

    private fun calculateFloorsFromZ(refPoints: List<WifiScans>, floorThreshold: Double): List<FloorZ> {
        val floorArray = mutableListOf<Double>()

        for (i in refPoints.map { it.floor }.distinct().sorted()) {
            val scansFromFloor = refPoints.filter { it.floor == i }
            floorArray.add(scansFromFloor.average())
        }

        return floorArray.mapIndexed { index, d ->
            if(index == floorArray.size-1) {
                FloorZ(index, d, Double.MAX_VALUE)
            } else {
                FloorZ(index, d, floorArray[index+1]-floorThreshold)
            }
        }

    }

    // Returns scale in pixels/meter
    private fun calculateScale(distance: Double, scale: Double, scaleUnit: String): Double {
        return when(scaleUnit) {
            "Meters" -> {
                distance/scale
            }
            "Feet" -> {
                distance / (scale / 3.281)
            }
            "Inches" -> {
                distance / (scale / 39.37)
            }
            else -> {
                -1.0
            }
        }
    }

    /**
     * Calculates position of unknown location using known
     * reference points with respect to 2d space
     */
    fun calculateTrilateration(list: List<WifiScans>, uuid: String): List<APLocation> {
        val apLocationList = mutableListOf<APLocation>()
        val ssidList = list.map { it.ssid }.distinct()
        for (ssid in ssidList) {
            val positions = mutableListOf<DoubleArray>()
            for (item in list.filter { it.ssid == ssid }) {
                positions.add(
                    doubleArrayOf(
                        item.currentLocationX,
                        item.currentLocationY
                    )
                )
            }
            /*val distances = list.filter{ it.ssid == ssid }.map { MathUtils.calculateDistanceInMeters(it.level, it.frequency) }.toDoubleArray()

            val solver = NonLinearLeastSquaresSolver(
                TrilaterationFunction(
                    positions.toTypedArray(),
                    distances
                ), LevenbergMarquardtOptimizer()
            )
            val optimum = solver.solve()
            val centroid = optimum.point.toArray().toList()
            apLocationList.add(APLocation(0, uuid, centroid[0], centroid[1], 0.0,0, ssid))*/
        }
        return apLocationList
    }
}