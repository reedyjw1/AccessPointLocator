package edu.udmercy.accesspointlocater.utils

import org.junit.Assert.*

import org.junit.Test

class MultilaterationTest {

    @Test
    fun calculate() {
        val list = listOf<ReferencePoint>(
            ReferencePoint(5.0,4.0,1.0, 2.828, Units.METERS),
            ReferencePoint(18.0,9.0,12.0, 16.093, Units.METERS),
            ReferencePoint(25.0,3.0,17.0, 24.597, Units.METERS),
            ReferencePoint(-2.0,-3.0,16.0, 17.291, Units.METERS),
            ReferencePoint(-7.0,8.0,10.0, 14.036, Units.METERS),
            ReferencePoint(3.0,1.0,5.0, 5.745, Units.METERS),
            ReferencePoint(-1.0,6.0,4.0, 6.083, Units.METERS),
        )

        val solution = Multilateration.calculate(list)?.array
        if (solution == null) {
            assert(false)
        } else {
            val expected: Array<DoubleArray> = arrayOf(doubleArrayOf(5.0,6.0,3.0))
            assertEquals(solution[1][0], expected[0][0], 0.1)
            assertEquals(solution[2][0], expected[0][1], 0.1)
            assertEquals(solution[3][0], expected[0][2], 0.1)
        }
    }


}