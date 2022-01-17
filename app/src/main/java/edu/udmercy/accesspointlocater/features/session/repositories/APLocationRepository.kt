package edu.udmercy.accesspointlocater.features.session.repositories

import edu.udmercy.accesspointlocater.features.session.room.APLocation

interface APLocationRepository {
    fun saveAccessPointLocations(list: List<APLocation>)
}