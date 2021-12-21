package edu.udmercy.accesspointlocater.di

import edu.udmercy.accesspointlocater.features.session.repositories.AccessPointRepository
import edu.udmercy.accesspointlocater.features.session.repositories.AccessPointRepositoryImpl
import edu.udmercy.accesspointlocater.features.session.repositories.SessionRepository
import edu.udmercy.accesspointlocater.features.session.repositories.SessionRepositoryImpl
import org.koin.dsl.module

val appDependencies = module {

    // Singleton (returns always the same unique instance of the object)
    single<SessionRepository> { SessionRepositoryImpl(get()) }
    single<AccessPointRepository> { AccessPointRepositoryImpl(get()) }

}