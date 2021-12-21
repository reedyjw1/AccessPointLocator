package edu.udmercy.accesspointlocater.features.session.repositories

import android.content.Context
import android.graphics.Bitmap
import androidx.room.Room
import edu.udmercy.accesspointlocater.AppDatabase
import edu.udmercy.accesspointlocater.features.session.room.BuildingImage
import edu.udmercy.accesspointlocater.features.session.room.Session
import kotlinx.coroutines.flow.Flow

class BuildingImageRepositoryImpl(private val appContext: Context): BuildingImageRepository {
    private val buildingImageRepo = Room.databaseBuilder(
        appContext,
        AppDatabase::class.java, "FingerPrintingDb"
    ).build().buildingImageDao()

    override fun addImagesToSession(buildingImageList: List<BuildingImage>) {
        buildingImageRepo.insertAll(*buildingImageList.toTypedArray())
    }

    override fun getAllBitmapsFromSession(uuid: String): List<BuildingImage> {
        return buildingImageRepo.getAll(uuid)
    }
}