package com.horizonloop.app.features.player.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.horizonloop.app.core.ui.theme.Mid
import com.horizonloop.app.core.ui.theme.Muted
import com.horizonloop.app.core.ui.theme.Surface

/**
 * Bottom audio control bar.
 *
 * Hierarchy (top → bottom):
 *   1. Title          — 15sp ExtraBold, primary text
 *   2. Mode/Loop/Speed — 11sp SemiBold, muted text, bullet-separated
 *   3. Progress       — 11sp SemiBold Monospace time labels
 *   4. Transport      — 64dp Accent circular play, pill-shaped ±5s flanks
 *
 * Spacing follows an 8-point grid: 8 / 16 / 20 dp vertical gaps,
 * 20 dp between transport buttons.
 */
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
            .background(Surface)
            .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {
        // ── 1. Title ────────────────────────────────────────────────────
        Text(
            text = title,
            fontSize = 15.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Dark,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        // ── 2. Mode / Loop / Speed ─────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Mode: ${activeTab.replaceFirstChar { it.uppercase() }}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Mid)
            Text("•", fontSize = 11.sp, color = Muted)
            Text("Loop: ${activeLoopId ?: "None"}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Mid)
            Text("•", fontSize = 11.sp, color = Muted)
            Text("Speed: ${currentSpeed}x", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Mid)
        }

        Spacer(Modifier.height(16.dp))

        // ── 3. Progress ─────────────────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(currentTime, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Mid, fontFamily = FontFamily.Monospace)
            Spacer(Modifier.width(12.dp))
            ProgressBar(
                progress = progress,
                onSeek = onSeek,
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(12.dp))
            Text(totalTime, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Mid, fontFamily = FontFamily.Monospace)
        }

        Spacer(Modifier.height(20.dp))

        // ── 4. Transport (rewind | play | forward) — text buttons ──────
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TransportTextButton(
                primary = "-5s",
                secondary = "Backward",
                emphasis = false,
                onClick = onRewind
            )
            Spacer(Modifier.width(16.dp))
            TransportTextButton(
                primary = if (isPlaying) "Pause" else "Play",
                secondary = null,
                emphasis = true,
                onClick = onPlayPause
            )
            Spacer(Modifier.width(16.dp))
            TransportTextButton(
                primary = "+5s",
                secondary = "Forward",
                emphasis = false,
                onClick = onForward
            )
        }
    }
}

/**
 * Outlined text button with fully rounded corners. No icon, no fill —
 * just text inside a rounded rectangle stroke. The `emphasis` flag
 * thickens the stroke and uses white (Dark) for the primary action
 * (play/pause); the skip buttons use the gray (Mid) stroke.
 */
@Composable
private fun TransportTextButton(
    primary: String,
    secondary: String?,
    emphasis: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (emphasis) Dark else Mid
    val textColor = Dark
    val borderWidth = if (emphasis) 1.5.dp else 1.dp
    val horizontalPad = if (emphasis) 28.dp else 20.dp
    val verticalPad = if (emphasis) 7.dp else 12.dp

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(28.dp))
            .background(androidx.compose.ui.graphics.Color.Transparent)
            .border(borderWidth, borderColor, RoundedCornerShape(28.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = horizontalPad, vertical = verticalPad),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = primary,
            fontSize = if (emphasis) 15.sp else 13.sp,
            fontWeight = FontWeight.ExtraBold,
            color = textColor
        )
        if (secondary != null) {
            Text(
                text = secondary,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = textColor.copy(alpha = 0.6f)
            )
        }
    }
}
