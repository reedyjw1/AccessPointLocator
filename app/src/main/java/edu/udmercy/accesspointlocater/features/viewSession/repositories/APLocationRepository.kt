package edu.udmercy.accesspointlocater.features.viewSession.repositories

import edu.udmercy.accesspointlocater.features.viewSession.room.APLocation
import kotlinx.coroutines.flow.Flow

interface APLocationRepository {
    fun getAllAccessPoints(uuid: String): Flow<List<APLocation>>
    fun createNewLocation(apLocation: APLocation)
    fun saveAccessPointLocations(list: List<APLocation>)
}