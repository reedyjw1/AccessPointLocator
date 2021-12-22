package edu.udmercy.accesspointlocater

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import edu.udmercy.accesspointlocater.features.session.room.*

@Database(entities = [Session::class, AccessPoint::class, BuildingImage::class], version = 2)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun sessionDao(): SessionDAO
    abstract fun accessPointDao(): AccessPointDao
    abstract fun buildingImageDao(): BuildingImageDao
}