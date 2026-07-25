package com.example.weatherapp.ui.screens.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.weatherapp.ui.theme.OfflineOrange
import com.example.weatherapp.ui.theme.TextWhite
import java.text.SimpleDateFormat
import java.util.Date
import androidx.compose.ui.platform.LocalLocale

@Composable
fun OfflineStatusBadge(
    lastRefreshedAt: Long,
    modifier: Modifier = Modifier
) {
    val dateText = SimpleDateFormat("h:mm a, MMM d", LocalLocale.current.platformLocale).format(
        Date(lastRefreshedAt)
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(OfflineOrange.copy(alpha = 0.2f))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.CloudOff,
            contentDescription = "Offline Mode",
            tint = OfflineOrange,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Offline Mode • Showing cached data from $dateText",
            style = MaterialTheme.typography.labelSmall.copy(
                color = TextWhite,
                fontWeight = FontWeight.Medium
            )
        )
    }
}
