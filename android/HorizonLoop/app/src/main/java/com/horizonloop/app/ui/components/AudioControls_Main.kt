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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.horizonloop.app.ui.theme.AppIcons
import com.horizonloop.app.ui.theme.Dark
import com.horizonloop.app.ui.theme.Deep
import com.horizonloop.app.ui.theme.Mid
import com.horizonloop.app.ui.theme.Muted

/**
 * AudioControls_Main.kt
 * Main audio controls composable with play/pause, seek, progress bar
 */

@Composable
fun AudioControls(
    title: String, isPlaying: Boolean, currentTime: String, totalTime: String, progress: Float,
    currentSpeed: Float, activeTab: String, activeLoopId: Int?, isCollapsed: Boolean = false,
    onPlayPause: () -> Unit, onRewind: () -> Unit, onForward: () -> Unit, onSeek: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val isExpanded = true
    Column(modifier = modifier.fillMaxWidth().background(Muted).padding(horizontal = 16.dp).padding(top = 8.dp, bottom = if (isExpanded) 16.dp else 8.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                Text(text = title, fontSize = 17.sp, fontWeight = FontWeight.ExtraBold, color = Dark, maxLines = 1, overflow = TextOverflow.Ellipsis, textAlign = TextAlign.Center)
            }
        }
        if (isExpanded) {
            Spacer(modifier = Modifier.height(6.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                Text("Mode:${activeTab.replaceFirstChar { it.uppercase() }}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Mid)
                Text(" | ", fontSize = 10.sp, color = Muted)
                Text("Loop:${activeLoopId ?: "None"}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Mid)
                Text(" | ", fontSize = 10.sp, color = Muted)
                Text("Speed: ${currentSpeed}x", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Mid)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(currentTime, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Mid, fontFamily = FontFamily.Monospace)
                Spacer(modifier = Modifier.width(12.dp))
                ProgressBar(progress = progress, onSeek = onSeek, modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.width(12.dp))
                Text(totalTime, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Mid, fontFamily = FontFamily.Monospace)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.clip(RoundedCornerShape(24.dp)).background(Muted).clickable(onClick = onRewind).padding(horizontal = 16.dp, vertical = 8.dp)) { Text("-5s", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = Mid) }
                Spacer(modifier = Modifier.width(14.dp))
                Row(modifier = Modifier.clip(RoundedCornerShape(24.dp)).background(Dark).clickable(onClick = onPlayPause).padding(horizontal = 16.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                    Icon(imageVector = if (isPlaying) AppIcons.Pause else AppIcons.Play, contentDescription = if (isPlaying) "Pause" else "Play", tint = Deep, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = if (isPlaying) "Pause" else "Play", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Deep)
                }
                Spacer(modifier = Modifier.width(14.dp))
                Box(modifier = Modifier.clip(RoundedCornerShape(24.dp)).background(Muted).clickable(onClick = onForward).padding(horizontal = 16.dp, vertical = 8.dp)) { Text("+5s", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = Mid) }
            }
        }
    }
}
