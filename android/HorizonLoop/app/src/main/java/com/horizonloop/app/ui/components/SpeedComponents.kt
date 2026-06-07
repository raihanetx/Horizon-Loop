package com.horizonloop.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import com.horizonloop.app.ui.theme.AppIcons
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.horizonloop.app.ui.theme.Dark
import com.horizonloop.app.ui.theme.Mid
import com.horizonloop.app.ui.theme.Muted

@Composable
fun SpeedTab(
    currentSpeed: Float,
    speeds: List<Float>,
    onSpeedChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Current speed value
        Text(
            text = "${currentSpeed}x",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Dark
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Speed controls with +/- buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            speeds.forEachIndexed { index, speed ->
                val isActive = speed == currentSpeed
                Box(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(if (isActive) Dark else Muted)
                        .clickable { onSpeedChange(index) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = AppIcons.PlayTriangle,
                        contentDescription = null,
                        tint = if (isActive) Dark else Mid,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}