package com.example.weatherapp.di

import com.example.weatherapp.data.local.WeatherDatabase
import com.example.weatherapp.data.remote.WeatherApi
import com.example.weatherapp.data.remote.WeatherApiService
import com.example.weatherapp.data.remote.WeatherApiServiceImpl
import com.example.weatherapp.data.repository.WeatherRepository
import com.example.weatherapp.data.repository.WeatherRepositoryImpl
import com.example.weatherapp.ui.viewmodel.WeatherViewModel
import com.example.weatherapp.utils.LocationHelper
import com.example.weatherapp.utils.RetrofitClientProvider
import com.example.weatherapp.worker.WeatherSyncWorker
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.dsl.worker
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import retrofit2.Retrofit

val databaseModule = module {
    single { WeatherDatabase.getInstance(androidContext()) }
    single { get<WeatherDatabase>().weatherDao() }
}

val networkModule = module {
    single<Retrofit> { RetrofitClientProvider.getClient(androidContext()) }
    single<WeatherApi> {
        get<Retrofit>().create(WeatherApi::class.java)
    }
    single<WeatherApiService> {
        WeatherApiServiceImpl(
            weatherApi = get(),
            apiKey = "fb3adbc1b5b29c3466ab64b0b6b96526",
            locationHelper = get(),
            weatherDao = get()
        )
    }
}

val locationModule = module {
    single { LocationHelper(androidContext()) }
}

val repositoryModule = module {
    single<WeatherRepository> {
        WeatherRepositoryImpl(
            weatherDao = get(),
            apiService = get(),
            locationHelper = get(),
            application = androidApplication()
        )
    }
}

val viewModelModule = module {
    viewModelOf(::WeatherViewModel)
}

val workerModule = module {
    worker {
        WeatherSyncWorker(
            context = get(),
            workerParams = get(),
            repository = get(),
            locationHelper = get(),
            weatherDao = get()
        )
    }
}

val appModules = listOf(
    databaseModule,
    networkModule,
    locationModule,
    repositoryModule,
    viewModelModule,
    workerModule
)
