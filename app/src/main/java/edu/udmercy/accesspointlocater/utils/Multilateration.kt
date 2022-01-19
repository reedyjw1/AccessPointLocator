package edu.udmercy.accesspointlocater.utils

import Jama.Matrix
import kotlin.math.pow

object Multilateration {

    private const val TAG = "Multilateration"

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
}