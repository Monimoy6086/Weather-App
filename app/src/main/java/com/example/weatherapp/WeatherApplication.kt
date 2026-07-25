package com.example.weatherapp

import android.app.Application
import com.example.weatherapp.di.appModules
import com.example.weatherapp.worker.NotificationHelper
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class WeatherApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Initialize Koin Dependency Injection
        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@WeatherApplication)
            workManagerFactory()
            modules(appModules)
        }

        NotificationHelper.createNotificationChannel(this)
    }
}
