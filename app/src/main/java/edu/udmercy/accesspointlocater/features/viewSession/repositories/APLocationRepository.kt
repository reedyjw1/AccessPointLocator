package edu.udmercy.accesspointlocater.features.viewSession.repositories

import edu.udmercy.accesspointlocater.features.viewSession.room.APLocation
import kotlinx.coroutines.flow.Flow

/**
 * Interface of all required functions to interact with APs saved in the database
 */
interface APLocationRepository {
    fun getAllAccessPoints(uuid: String): Flow<List<APLocation>>
    fun createNewLocation(apLocation: APLocation)
    fun saveAccessPointLocations(list: List<APLocation>)
    fun deleteSession(uuid:String)
}