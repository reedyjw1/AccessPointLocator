package edu.udmercy.accesspointlocater.utils

enum class Units {
    METERS, FEET, INCHES, CENTIMETERS
}

data class ReferencePoint(val x: Double, val y: Double, val z: Double, val distance: Double, val units: Units)