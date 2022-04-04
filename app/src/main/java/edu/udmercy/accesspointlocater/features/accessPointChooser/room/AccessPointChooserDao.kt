package edu.udmercy.accesspointlocater.features.accessPointChooser.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface AccessPointChooserDao {
    @Insert
    fun insertAll(vararg aps: AccessPointChooser)

    @Query("SELECT * FROM AccessPointChooser where sessionUuid == (:uuid)")
    fun getReferenceAccessPoint(uuid: String): AccessPointChooser

}