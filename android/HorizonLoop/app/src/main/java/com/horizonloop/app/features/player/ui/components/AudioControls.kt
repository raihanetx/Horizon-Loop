package com.horizonloop.app.features.player.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.horizonloop.app.core.ui.theme.AppIcons
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.horizonloop.app.core.ui.theme.Dark
import com.horizonloop.app.core.ui.theme.Deep
import com.horizonloop.app.core.ui.theme.Mid
import com.horizonloop.app.core.ui.theme.Muted
import com.horizonloop.app.features.player.ui.components.ProgressBar

@Composable
fun AudioControls(
    title: String,
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
            .background(Muted)
            .padding(horizontal = 16.dp)
            .padding(top = 10.dp, bottom = 16.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = Dark, maxLines = 1, overflow = TextOverflow.Ellipsis, textAlign = TextAlign.Center)
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            Text("Mode:${activeTab.replaceFirstChar { it.uppercase() }}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Mid)
            Text(" | ", fontSize = 11.sp, color = Muted)
            Text("Loop:${activeLoopId ?: "None"}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Mid)
            Text(" | ", fontSize = 11.sp, color = Muted)
            Text("Speed: ${currentSpeed}x", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Mid)
        }
        Spacer(modifier = Modifier.height(20.dp))
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(currentTime, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Mid, fontFamily = FontFamily.Monospace)
            Spacer(modifier = Modifier.width(14.dp))
            ProgressBar(
                progress = progress,
                onSeek = onSeek,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Text(totalTime, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Mid, fontFamily = FontFamily.Monospace)
        }
        Spacer(modifier = Modifier.height(20.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            // Rewind button — tap calls onRewind() once.
            // CRITICAL: .clickable must be AFTER .padding so the padded area is clickable.
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(28.dp))
                    .background(Muted)
                    .padding(horizontal = 20.dp, vertical = 12.dp)
                    .clickable { onRewind() },
                contentAlignment = Alignment.Center
            ) {                    Text("-5s", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = Mid)
            }
            Spacer(modifier = Modifier.width(16.dp))
            // Play/Pause button — single tap toggles play/pause.
            // CRITICAL: .clickable must be AFTER .padding so the entire padded area is clickable.
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(28.dp))
                    .background(Dark)
                    .padding(horizontal = 20.dp, vertical = 12.dp)
                    .clickable { onPlayPause() },
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                    Icon(imageVector = if (isPlaying) AppIcons.Pause else AppIcons.Play, contentDescription = if (isPlaying) "Pause" else "Play", tint = Deep, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = if (isPlaying) "Pause" else "Play", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Deep)
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            // Forward button — tap calls onForward() once.
            // CRITICAL: .clickable must be AFTER .padding so the padded area is clickable.
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(28.dp))
                    .background(Muted)
                    .padding(horizontal = 20.dp, vertical = 12.dp)
                    .clickable { onForward() },
                contentAlignment = Alignment.Center
            ) {                    Text("+5s", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = Mid)
            }
        }
    }
}