package com.example.weatherapp.ui.screens.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Warning
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
import com.example.weatherapp.domain.model.SeverityLevel
import com.example.weatherapp.domain.model.WeatherAlert
import com.example.weatherapp.ui.theme.AlertRed
import com.example.weatherapp.ui.theme.TextWhite

@Composable
fun SevereAlertBanner(
    alerts: List<WeatherAlert>,
    modifier: Modifier = Modifier
) {
    if (alerts.isEmpty()) return

    val topAlert = alerts.first()
    val alertColor = when (topAlert.severity) {
        SeverityLevel.CRITICAL -> AlertRed
        SeverityLevel.WARNING -> Color(0xFFFF9E00)
        SeverityLevel.INFO -> Color(0xFF0F82FF)
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Severe Weather Alerts",
            style = MaterialTheme.typography.titleMedium.copy(
                color = alertColor,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(alertColor.copy(alpha = 0.15f))
                .border(1.dp, alertColor, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Alert Warning",
                    tint = alertColor,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = topAlert.title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = TextWhite,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = topAlert.description,
                style = MaterialTheme.typography.bodyMedium.copy(color = TextWhite.copy(alpha = 0.9f))
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Issued: ${topAlert.issueTimeFormatted}",
                style = MaterialTheme.typography.labelSmall.copy(color = alertColor)
            )
        }
    }
}
