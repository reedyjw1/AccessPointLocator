package edu.udmercy.accesspointlocater

import android.app.Application
import edu.udmercy.accesspointlocater.di.appDependencies
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

/**
 * Application class to enable Koin Dependency Injection
 */
class App: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin{
            androidContext(this@App)
            modules(appDependencies)
        }
    }
}