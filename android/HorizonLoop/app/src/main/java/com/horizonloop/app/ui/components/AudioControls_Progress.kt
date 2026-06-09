package com.horizonloop.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import com.horizonloop.app.ui.theme.Dark
import com.horizonloop.app.ui.theme.Muted

/**
 * AudioControls_Progress.kt
 * Progress bar with tap-to-seek functionality
 */

@Composable
fun ProgressBar(progress: Float, onSeek: (Float) -> Unit, modifier: Modifier = Modifier) {
    var barWidth by remember { mutableStateOf(0f) }
    Box(modifier = modifier.height(12.dp).clip(RoundedCornerShape(6.dp)).background(Muted).onSizeChanged { barWidth = it.width.toFloat() }.pointerInput(Unit) {
        detectTapGestures { offset ->
            if (barWidth > 0) {
                val newProgress = (offset.x / barWidth).coerceIn(0f, 1f)
                onSeek(newProgress)
            }
        }
    }) {
        Box(modifier = Modifier.fillMaxWidth(progress).height(12.dp).clip(RoundedCornerShape(6.dp)).background(Dark))
    }
}
