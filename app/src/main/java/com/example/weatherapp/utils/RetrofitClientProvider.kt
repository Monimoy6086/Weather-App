package com.example.weatherapp.utils

import android.content.Context
import com.example.weatherapp.data.remote.WeatherApi
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

object RetrofitClientProvider {
    private const val CACHE_SIZE = 10L * 1024 * 1024 // 10MB

    // We use a nullable Retrofit instance to ensure it's built only once
    private var retrofit: Retrofit? = null

    /**
     * Initializes the Retrofit client. Call this in your Application class.
     */
    fun getClient(context: Context): Retrofit {
        if (retrofit == null) {
            // Configure Caching
            val cacheDirectory = File(context.cacheDir, "http_responses")
            val cache = Cache(cacheDirectory, CACHE_SIZE)

            // Configure Logging Interceptor
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                // Use BODY for debug builds, NONE for production
                level = HttpLoggingInterceptor.Level.BODY
            }

            // Build OkHttpClient with Timeouts
            val okHttpClient = OkHttpClient.Builder()
                .cache(cache)
                .addInterceptor(loggingInterceptor)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build()

            // Build Retrofit Instance
            retrofit = Retrofit.Builder()
                .baseUrl(WeatherApi.BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        return retrofit!!
    }
}