package edu.udmercy.accesspointlocater.features.execute.model

// Was used for multilateration to get the estimated lower and upper bound of the floor heights
// This is no longer used.
data class FloorZ(
    val floor: Int,
    val lowerBound: Double,
    val upperBound: Double
)