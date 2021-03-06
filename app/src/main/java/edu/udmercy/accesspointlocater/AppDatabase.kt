package edu.udmercy.accesspointlocater

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import edu.udmercy.accesspointlocater.features.createSession.room.BuildingImage
import edu.udmercy.accesspointlocater.features.createSession.room.BuildingImageDao
import edu.udmercy.accesspointlocater.features.home.room.*
import edu.udmercy.accesspointlocater.features.executeSession.room.WifiScans
import edu.udmercy.accesspointlocater.features.executeSession.room.WifiScansDao
import edu.udmercy.accesspointlocater.features.viewSession.room.APLocation
import edu.udmercy.accesspointlocater.features.viewSession.room.APLocationDao

/**
 * For Jetpack Room Database, specifies all tables and the class that constructs each table, and the DB version number
 */
@Database(entities = [Session::class, WifiScans::class, BuildingImage::class, APLocation::class], version = 14)
@TypeConverters(Converters::class)
// All Data Access Objects (DAOs) (which have the functions to interact with DB) are specified here
abstract class AppDatabase : RoomDatabase() {
    abstract fun sessionDao(): SessionDAO
    abstract fun accessPointDao(): WifiScansDao
    abstract fun buildingImageDao(): BuildingImageDao
    abstract fun apLocationDao(): APLocationDao
}