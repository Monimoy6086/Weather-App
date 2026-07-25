package com.example.weatherapp.ui.screens.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.weatherapp.domain.model.HourlyForecast
import com.example.weatherapp.ui.theme.GlassCardBackground
import com.example.weatherapp.ui.theme.GlassCardBorder
import com.example.weatherapp.ui.theme.SoftCyanAccent
import com.example.weatherapp.ui.theme.TextSubtle
import com.example.weatherapp.ui.theme.TextWhite
import com.example.weatherapp.ui.viewmodel.TemperatureUnit

@Composable
fun HourlyForecastRow(
    hourlyForecasts: List<HourlyForecast>,
    unit: TemperatureUnit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Hourly Forecast",
            style = MaterialTheme.typography.titleMedium.copy(
                color = TextWhite,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            items(hourlyForecasts) { item ->
                HourlyCardItem(item = item, unit = unit)
            }
        }
    }
}

@Composable
private fun HourlyCardItem(
    item: HourlyForecast,
    unit: TemperatureUnit
) {
    val isNow = item.timeFormatted == "Now"
    val bgColor = if (isNow) Color(0xFF2563EB) else GlassCardBackground
    val borderColor = if (isNow) SoftCyanAccent else GlassCardBorder

    Box(
        modifier = Modifier
            .width(76.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(20.dp))
            .padding(vertical = 12.dp, horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = item.timeFormatted,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = if (isNow) TextWhite else TextSubtle,
                    fontWeight = if (isNow) FontWeight.Bold else FontWeight.Normal
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Icon(
                imageVector = getWeatherIcon(item.condition),
                contentDescription = null,
                tint = getWeatherIconColor(item.condition),
                modifier = Modifier.size(26.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = formatTemp(item.temperatureC, unit),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = TextWhite,
                    fontWeight = FontWeight.SemiBold
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.WaterDrop,
                    contentDescription = null,
                    tint = SoftCyanAccent,
                    modifier = Modifier.size(10.dp)
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = "${item.precipitationChance}%",
                    style = MaterialTheme.typography.labelSmall.copy(color = SoftCyanAccent)
                )
            }
        }
    }
}
