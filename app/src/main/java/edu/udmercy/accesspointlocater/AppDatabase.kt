package edu.udmercy.accesspointlocater

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import edu.udmercy.accesspointlocater.features.session.room.*
import edu.udmercy.accesspointlocater.features.session.room.AccessPoint
import edu.udmercy.accesspointlocater.features.session.room.AccessPointDao
import edu.udmercy.accesspointlocater.features.viewSession.room.APLocation
import edu.udmercy.accesspointlocater.features.viewSession.room.APLocationDao

@Database(entities = [Session::class, AccessPoint::class, BuildingImage::class, APLocation::class], version = 4)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun sessionDao(): SessionDAO
    abstract fun accessPointDao(): AccessPointDao
    abstract fun buildingImageDao(): BuildingImageDao
    abstract fun apLocationDao(): APLocationDao
}