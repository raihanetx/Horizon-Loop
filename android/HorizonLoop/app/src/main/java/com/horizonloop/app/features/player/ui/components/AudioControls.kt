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
 *   1. Title           — 15sp ExtraBold, primary text
 *   2. Mode/Loop/Speed — 11sp SemiBold, muted text, bullet-separated
 *   3. Progress        — 11sp SemiBold Monospace time labels
 *   4. Transport       — three identical outlined text buttons (−, Play, +)
 *
 * Spacing on a tight 8-point grid: 6 / 12 / 14 dp vertical gaps.
 * Card vertical padding: 16 dp.
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
            .padding(horizontal = 20.dp, vertical = 16.dp)
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

        Spacer(Modifier.height(6.dp))

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

        Spacer(Modifier.height(12.dp))

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

        Spacer(Modifier.height(14.dp))

        // ── 4. Transport (− | Play | +) ─────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TransportTextButton(primary = "−", onClick = onRewind)
            Spacer(Modifier.width(14.dp))
            TransportTextButton(primary = if (isPlaying) "Pause" else "Play", onClick = onPlayPause)
            Spacer(Modifier.width(14.dp))
            TransportTextButton(primary = "+", onClick = onForward)
        }
    }
}

/**
 * Uniform outlined text button. All three transport buttons (−, Play, +)
 * use the same shape, border, padding, and text size — only the glyph
 * and the label text differ.
 */
@Composable
private fun TransportTextButton(
    primary: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(28.dp))
            .background(androidx.compose.ui.graphics.Color.Transparent)
            .border(1.dp, Mid, RoundedCornerShape(28.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = primary,
            fontSize = 14.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Dark
        )
    }
}
