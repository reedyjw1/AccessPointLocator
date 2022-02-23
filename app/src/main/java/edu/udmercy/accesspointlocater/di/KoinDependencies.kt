package edu.udmercy.accesspointlocater.di

import edu.udmercy.accesspointlocater.features.create.repositories.BuildingImageRepository
import edu.udmercy.accesspointlocater.features.create.repositories.BuildingImageRepositoryImpl
import edu.udmercy.accesspointlocater.features.execute.repositories.WifiScansRepository
import edu.udmercy.accesspointlocater.features.execute.repositories.WifiScansRepositoryImpl
import edu.udmercy.accesspointlocater.features.home.repositories.*
import edu.udmercy.accesspointlocater.features.viewSession.repositories.APLocationRepository
import edu.udmercy.accesspointlocater.features.viewSession.repositories.APLocationRepositoryImpl
import edu.udmercy.accesspointlocater.utils.sp.ISharedPrefsHelper
import edu.udmercy.accesspointlocater.utils.sp.SharedPrefsHelper
import org.koin.dsl.module

val appDependencies = module {

    // Singleton (returns always the same unique instance of the object)
    single<SessionRepository> { SessionRepositoryImpl(get()) }
    single<WifiScansRepository> { WifiScansRepositoryImpl(get()) }
    single<BuildingImageRepository> { BuildingImageRepositoryImpl(get()) }
    single<APLocationRepository> { APLocationRepositoryImpl(get()) }
    single<ISharedPrefsHelper> { SharedPrefsHelper(get()) }

}