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
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.weatherapp.domain.model.DailyForecast
import com.example.weatherapp.ui.theme.GlassCardBackground
import com.example.weatherapp.ui.theme.GlassCardBorder
import com.example.weatherapp.ui.theme.SoftCyanAccent
import com.example.weatherapp.ui.theme.TextSubtle
import com.example.weatherapp.ui.theme.TextWhite
import com.example.weatherapp.ui.viewmodel.TemperatureUnit

@Composable
fun WeeklyForecastList(
    dailyForecasts: List<DailyForecast>,
    unit: TemperatureUnit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "7-Day Forecast",
            style = MaterialTheme.typography.titleMedium.copy(
                color = TextWhite,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
        )

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            dailyForecasts.forEach { item ->
                DailyCardItem(item = item, unit = unit)
            }
        }
    }
}

@Composable
private fun DailyCardItem(
    item: DailyForecast,
    unit: TemperatureUnit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(GlassCardBackground)
            .border(1.dp, GlassCardBorder, RoundedCornerShape(16.dp))
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Day & Date
            Column(modifier = Modifier.width(90.dp)) {
                Text(
                    text = item.dayOfWeek,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = TextWhite,
                        fontWeight = FontWeight.SemiBold
                    )
                )
                Text(
                    text = item.dateText,
                    style = MaterialTheme.typography.labelSmall.copy(color = TextSubtle)
                )
            }

            // Weather Condition & Rain Chance
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = getWeatherIcon(item.condition),
                    contentDescription = null,
                    tint = getWeatherIconColor(item.condition),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = item.conditionSummary,
                        style = MaterialTheme.typography.bodyMedium.copy(color = TextWhite)
                    )
                    if (item.precipitationChance > 20) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.WaterDrop,
                                contentDescription = null,
                                tint = SoftCyanAccent,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "${item.precipitationChance}% rain",
                                style = MaterialTheme.typography.labelSmall.copy(color = SoftCyanAccent)
                            )
                        }
                    }
                }
            }

            // Max & Min Temperature
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = formatTemp(item.maxTempC, unit),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = TextWhite,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = formatTemp(item.minTempC, unit),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = TextSubtle
                    )
                )
            }
        }
    }
}
