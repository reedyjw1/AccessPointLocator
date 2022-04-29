package edu.udmercy.accesspointlocater.features.accessPointChooser.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

/**
 * This is a feature of Jetpack Room, a Google Library that allows functions to be directly correlated
 * to SQL functions. These functions are used to insert into the database the reference access point for calculation,
 * however, it is no longer used.
 */
@Dao
interface AccessPointChooserDao {
    @Insert
    fun insertAll(vararg aps: AccessPointChooser)

    @Query("SELECT * FROM AccessPointChooser where sessionUuid == (:uuid)")
    fun getReferenceAccessPoint(uuid: String): AccessPointChooser

}