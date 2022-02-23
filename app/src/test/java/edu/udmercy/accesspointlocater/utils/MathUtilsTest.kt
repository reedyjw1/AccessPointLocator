package edu.udmercy.accesspointlocater.utils

import org.junit.Assert.*

import org.junit.Test

class MathUtilsTest {

    @Test
    fun convertUnitToMeters() {
        val meter1 = 4.2
        val feet1 = 3.5
        val feet2 = 10.32
        val feet3 = 8.6
        val inches1 = 22.5
        val inches2 = 88.3

        val expected1 = MathUtils.convertUnitToMeters(meter1, Units.METERS)
        val expected2 = MathUtils.convertUnitToMeters(feet1, Units.FEET)
        val expected3 = MathUtils.convertUnitToMeters(feet2, Units.FEET)
        val expected4 = MathUtils.convertUnitToMeters(feet3, Units.FEET)
        val expected5 = MathUtils.convertUnitToMeters(inches1, Units.INCHES)
        val expected6 = MathUtils.convertUnitToMeters(inches2, Units.INCHES)

        assertEquals(expected1, 4.2, 0.001)
        assertEquals(expected2, 1.0668, 0.001)
        assertEquals(expected3, 3.145536, 0.001)
        assertEquals(expected4, 2.62128, 0.001)
        assertEquals(expected5, 0.5715, 0.001)
        assertEquals(expected6, 2.24282, 0.001)
    }

    @Test
    fun calculateHeightFromFloors() {
        val floorHeights = mutableListOf(11.0, 11.0, 9.0).toList()
        val phoneHeight = 1.2192

        val expected1 = MathUtils.calculateHeightFromFloors(floorHeights, phoneHeight, 0)
        val expected2 = MathUtils.calculateHeightFromFloors(floorHeights, phoneHeight, 1)
        val expected3 = MathUtils.calculateHeightFromFloors(floorHeights, phoneHeight, 2)

        assertEquals(expected1, phoneHeight, 0.001)
        assertEquals(expected2, floorHeights[0] + phoneHeight, 0.001)
        assertEquals(expected3, floorHeights[0] + floorHeights[1] + phoneHeight, 0.001)
    }
}