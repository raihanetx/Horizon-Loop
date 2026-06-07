package com.horizonloop.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.horizonloop.app.ui.theme.Dark
import com.horizonloop.app.ui.theme.Deep
import com.horizonloop.app.ui.theme.Mid
import com.horizonloop.app.ui.theme.Muted
import com.horizonloop.app.ui.theme.Surface

@Composable
fun AudioControls(
    isPlaying: Boolean,
    currentTime: String,
    totalTime: String,
    progress: Float,
    currentSpeed: Float,
    activeTab: String,
    activeLoopId: Int?,
    onPlayPause: () -> Unit,
    onRewind: () -> Unit,
    onForward: () -> Unit,
    onSeek: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Surface)
            .padding(16.dp)
            .padding(bottom = 20.dp)
    ) {
        Box(
            modifier = Modifier
                .width(48.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(Mid.copy(alpha = 0.3f))
                .align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = activeTab.replaceFirstChar { it.uppercase() },
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Dark,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text("Mode:${activeTab.replaceFirstChar { it.uppercase() }}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Mid)
            Text(" | ", fontSize = 10.sp, color = Muted)
            Text("Loop:${activeLoopId ?: "None"}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Mid)
            Text(" | ", fontSize = 10.sp, color = Muted)
            Text("Speed: ${currentSpeed}x", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Mid)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(currentTime, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Mid, fontFamily = FontFamily.Monospace)
            Spacer(modifier = Modifier.width(12.dp))
            ProgressBar(progress = progress, onSeek = onSeek, modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.width(12.dp))
            Text(totalTime, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Mid, fontFamily = FontFamily.Monospace)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Muted)
                    .clickable(onClick = onRewind)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text("-5s", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = Mid)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Dark)
                    .clickable(onClick = onPlayPause)
                    .padding(horizontal = 28.dp, vertical = 10.dp)
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    tint = Deep,
                    modifier = Modifier.height(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Muted)
                    .clickable(onClick = onForward)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text("+5s", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = Mid)
            }
        }
    }
}

@Composable
private fun ProgressBar(progress: Float, onSeek: (Float) -> Unit, modifier: Modifier = Modifier) {
    var barWidth by remember { mutableStateOf(0f) }
    Box(
        modifier = modifier
            .height(12.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(Muted)
            .onSizeChanged { barWidth = it.width.toFloat() }
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    if (barWidth > 0) {
                        val newProgress = (offset.x / barWidth).coerceIn(0f, 1f)
                        onSeek(newProgress)
                    }
                }
            }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progress)
                .height(12.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Dark)
        )
    }
}