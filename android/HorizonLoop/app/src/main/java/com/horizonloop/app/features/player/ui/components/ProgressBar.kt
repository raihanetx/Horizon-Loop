package com.horizonloop.app.features.player.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import com.horizonloop.app.core.ui.theme.Dark
import com.horizonloop.app.core.ui.theme.Mid

@Composable
fun ProgressBar(
    progress: Float,
    onSeek: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var barWidth by remember { mutableStateOf(0f) }
    val trackColor = Mid.copy(alpha = 0.25f)
    val progressColor = Dark

    Box(
        modifier = modifier
            .height(48.dp)
            .clip(RoundedCornerShape(4.dp))
            .onSizeChanged { barWidth = it.width.toFloat() }
            .pointerInput(Unit) {
                awaitEachGesture {
                    val down = awaitFirstDown()
                    val startX = down.position.x
                    if (barWidth > 0f) {
                        val tapProgress = (startX / barWidth).coerceIn(0f, 1f)
                        onSeek(tapProgress)
                    }
                    var dragStarted = false
                    var lastSeekTime = 0L
                    while (true) {
                        val event = awaitPointerEvent()
                        val change = event.changes.find { it.id == down.id } ?: break
                        if (!change.pressed) {
                            break
                        }
                        val dx = change.position.x - startX
                        if (kotlin.math.abs(dx) >= 10f) {
                            if (!dragStarted) {
                                dragStarted = true
                                change.consume()
                            }
                            val now = System.currentTimeMillis()
                            if (now - lastSeekTime > 50 && barWidth > 0f) {
                                lastSeekTime = now
                                val newProgress = (change.position.x / barWidth).coerceIn(0f, 1f)
                                onSeek(newProgress)
                            }
                        }
                    }
                }
            }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(trackColor)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress.coerceIn(0f, 1f))
                        .height(6.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(progressColor)
                )
            }
        }
    }
}
