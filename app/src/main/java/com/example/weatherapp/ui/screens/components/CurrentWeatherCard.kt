package com.example.weatherapp.ui.screens.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Grain
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Thunderstorm
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherapp.domain.model.CurrentWeather
import com.example.weatherapp.domain.model.WeatherCondition
import com.example.weatherapp.ui.theme.GlassCardBackground
import com.example.weatherapp.ui.theme.GlassCardBorder
import com.example.weatherapp.ui.theme.SoftCyanAccent
import com.example.weatherapp.ui.theme.SunGold
import com.example.weatherapp.ui.theme.TextSubtle
import com.example.weatherapp.ui.theme.TextWhite
import com.example.weatherapp.ui.viewmodel.TemperatureUnit
import kotlin.math.roundToInt

@Composable
fun CurrentWeatherCard(
    weather: CurrentWeather,
    unit: TemperatureUnit,
    modifier: Modifier = Modifier
) {
    val displayTemp = formatTemp(weather.temperatureC, unit)
    val displayFeelsLike = formatTemp(weather.feelsLikeC, unit)

    val gradientBg = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF1E293B),
            Color(0xFF0F172A)
        )
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(gradientBg)
            .border(1.dp, GlassCardBorder, RoundedCornerShape(24.dp))
            .padding(20.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Location Header
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Navigation,
                    contentDescription = null,
                    tint = SoftCyanAccent,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = weather.cityName,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = TextWhite,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Weather Icon & Temp Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = getWeatherIcon(weather.condition),
                    contentDescription = weather.conditionText,
                    tint = getWeatherIconColor(weather.condition),
                    modifier = Modifier.size(72.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = displayTemp,
                        style = MaterialTheme.typography.displayMedium.copy(
                            color = TextWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 48.sp
                        )
                    )
                    Text(
                        text = weather.conditionText,
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = SoftCyanAccent,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Feels like $displayFeelsLike",
                style = MaterialTheme.typography.bodyMedium.copy(color = TextSubtle)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Weather Metrics Grid
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(GlassCardBackground)
                    .padding(vertical = 12.dp, horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                WeatherMetricItem(
                    icon = Icons.Default.WaterDrop,
                    label = "Humidity",
                    value = "${weather.humidity}%"
                )
                WeatherMetricItem(
                    icon = Icons.Default.Air,
                    label = "Wind",
                    value = "${weather.windSpeedKmh} km/h"
                )
                WeatherMetricItem(
                    icon = Icons.Default.WbSunny,
                    label = "UV Index",
                    value = "${weather.uvIndex}"
                )
                WeatherMetricItem(
                    icon = Icons.Default.Grain,
                    label = "AQI",
                    value = "${weather.airQualityIndex}"
                )
            }
        }
    }
}

@Composable
private fun WeatherMetricItem(
    icon: ImageVector,
    label: String,
    value: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = SoftCyanAccent,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = TextWhite,
                fontWeight = FontWeight.Bold
            )
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(color = TextSubtle)
        )
    }
}

fun getWeatherIcon(condition: WeatherCondition): ImageVector {
    return when (condition) {
        WeatherCondition.SUNNY -> Icons.Default.WbSunny
        WeatherCondition.PARTLY_CLOUDY -> Icons.Default.Cloud
        WeatherCondition.CLOUDY -> Icons.Default.Cloud
        WeatherCondition.RAINY -> Icons.Default.Grain
        WeatherCondition.HEAVY_RAIN -> Icons.Default.Grain
        WeatherCondition.THUNDERSTORM -> Icons.Default.Thunderstorm
        WeatherCondition.SNOWY -> Icons.Default.Grain
        WeatherCondition.WINDY -> Icons.Default.Air
        WeatherCondition.FOGGY -> Icons.Default.Cloud
        WeatherCondition.CLEAR_NIGHT -> Icons.Default.WbSunny
    }
}

fun getWeatherIconColor(condition: WeatherCondition): Color {
    return when (condition) {
        WeatherCondition.SUNNY -> SunGold
        WeatherCondition.PARTLY_CLOUDY -> SoftCyanAccent
        WeatherCondition.THUNDERSTORM -> Color(0xFFFF9E00)
        else -> SoftCyanAccent
    }
}

fun formatTemp(celsius: Double, unit: TemperatureUnit): String {
    return if (unit == TemperatureUnit.CELSIUS) {
        "${celsius.roundToInt()}°C"
    } else {
        val fahrenheit = (celsius * 9 / 5) + 32
        "${fahrenheit.roundToInt()}°F"
    }
}
