package edu.udmercy.accesspointlocater.di

import edu.udmercy.accesspointlocater.features.accessPointChooser.repositories.AccessPointReferenceRepository
import edu.udmercy.accesspointlocater.features.accessPointChooser.repositories.AccessPointReferenceRepositoryImpl
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

/**
 * Koin is a dependency injection framework. This allows repositories, which in our case accesses a local
 * database, to be injected into any class that needs it. This way context to each repository does not
 * need to be manually managed and prevents memory leaks/bugs
 * This file maps all Koin interfaces to there respective implementation to create singletons
 */
val appDependencies = module {

    // Singleton (returns always the same unique instance of the object)
    single<SessionRepository> { SessionRepositoryImpl(get()) }
    single<WifiScansRepository> { WifiScansRepositoryImpl(get()) }
    single<BuildingImageRepository> { BuildingImageRepositoryImpl(get()) }
    single<APLocationRepository> { APLocationRepositoryImpl(get()) }
    single<AccessPointReferenceRepository> { AccessPointReferenceRepositoryImpl(get()) }
    single<ISharedPrefsHelper> { SharedPrefsHelper(get()) }

}