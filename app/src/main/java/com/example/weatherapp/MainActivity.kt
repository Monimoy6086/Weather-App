package com.example.weatherapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.weatherapp.ui.screens.WeatherHomeScreen
import com.example.weatherapp.ui.theme.WeatherAppTheme
import com.example.weatherapp.ui.viewmodel.WeatherViewModel
import com.example.weatherapp.ui.viewmodel.WeatherUiState
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {

    private val weatherViewModel: WeatherViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WeatherAppTheme {
                WeatherHomeScreen(viewModel = weatherViewModel)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // If we were in an error state due to permissions, try re-fetching
        if (weatherViewModel.uiState.value is WeatherUiState.Error) {
            weatherViewModel.fetchLocationAndLoadWeather()
        }
    }
}
