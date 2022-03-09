package edu.udmercy.accesspointlocater.features.accessPointChooser.room

import androidx.room.Dao
import androidx.room.Insert

@Dao
interface AccessPointChooserDao {
    @Insert
    fun insertAll(vararg aps: AccessPointChooser)

}