package edu.udmercy.accesspointlocater

import androidx.room.Database
import androidx.room.RoomDatabase
import edu.udmercy.accesspointlocater.features.session.room.Session
import edu.udmercy.accesspointlocater.features.session.room.SessionDAO

@Database(entities = [Session::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun sessionDao(): SessionDAO
}