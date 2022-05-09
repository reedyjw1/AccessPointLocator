package edu.udmercy.accesspointlocater.features.executeSession.interfaces

interface CompletedPointTouchedListener {
    fun onPointTouched(scanUUID: String, roomNumber: String)
}