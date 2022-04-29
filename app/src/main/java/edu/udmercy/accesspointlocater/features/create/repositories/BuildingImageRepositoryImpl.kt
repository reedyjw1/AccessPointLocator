package edu.udmercy.accesspointlocater.features.create.repositories

import android.content.Context
import androidx.room.Room
import edu.udmercy.accesspointlocater.AppDatabase
import edu.udmercy.accesspointlocater.features.create.room.BuildingImage

/**
 * Implementation of the Interface
 */
class BuildingImageRepositoryImpl(private val appContext: Context): BuildingImageRepository {
    private val buildingImageRepo = Room.databaseBuilder(
        appContext,
        AppDatabase::class.java, "FingerPrintingDb"
    ).fallbackToDestructiveMigration().build().buildingImageDao()

    override fun addImagesToSession(buildingImageList: List<BuildingImage>) {
        buildingImageRepo.insertAll(*buildingImageList.toTypedArray())
    }

    override fun getAllBitmapsFromSession(uuid: String): List<BuildingImage> {
        return buildingImageRepo.getAll(uuid)
    }

    override fun getFloorImage(uuid: String, floor: Int): BuildingImage {
        return buildingImageRepo.getFloorImage(uuid, floor)
    }

    override fun getFloorCount(uuid: String): Int {
        return buildingImageRepo.getFloorCount(uuid)
    }

    override fun deleteSession(uuid: String) {
        buildingImageRepo.deleteAllImages(uuid)
    }
}