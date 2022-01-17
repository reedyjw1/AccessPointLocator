package edu.udmercy.accesspointlocater.di

import edu.udmercy.accesspointlocater.features.session.repositories.*
import org.koin.dsl.module

val appDependencies = module {

    // Singleton (returns always the same unique instance of the object)
    single<SessionRepository> { SessionRepositoryImpl(get()) }
    single<AccessPointRepository> { AccessPointRepositoryImpl(get()) }
    single<BuildingImageRepository> { BuildingImageRepositoryImpl(get()) }
    single<APLocationRepository> { APLocationRepositoryImpl(get()) }

}