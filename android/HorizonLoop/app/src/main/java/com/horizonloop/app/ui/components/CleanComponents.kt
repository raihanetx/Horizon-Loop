package com.horizonloop.app.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.horizonloop.app.data.Dialogue
import com.horizonloop.app.ui.theme.AppIcons
import com.horizonloop.app.ui.theme.Dark
import com.horizonloop.app.ui.theme.Mid
import com.horizonloop.app.ui.theme.White15
import kotlinx.coroutines.delay

@Composable
fun CleanTab(
    dialogue: Dialogue?,
    isAudioMode: Boolean,
    isPlaying: Boolean,
    isTranslating: Boolean,
    isPlaybackEnded: Boolean = false,
    translationProgress: String = "",
    onSpeedDecrease: () -> Unit,
    onSpeedIncrease: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.4f,
        animationSpec = infiniteRepeatable(animation = tween(1000), repeatMode = RepeatMode.Reverse),
        label = "pulseAlpha"
    )
    
    // Long press speed control with continuous repeat
    var isLeftHeld by remember { mutableStateOf(false) }
    var isRightHeld by remember { mutableStateOf(false) }
    
    // Separate LaunchedEffects for concurrent left/right speed control
    LaunchedEffect(isLeftHeld) {
        if (isLeftHeld) {
            while (isLeftHeld) {
                onSpeedDecrease()
                delay(200) // Repeat every 200ms while held
            }
        }
    }
    
    LaunchedEffect(isRightHeld) {
        if (isRightHeld) {
            while (isRightHeld) {
                onSpeedIncrease()
                delay(200) // Repeat every 200ms while held
            }
        }
    }
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                val longPressTimeout = 400L // 400ms before action triggers
                
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    val isLeftSide = down.position.x < size.width / 2
                    
                    // Track press start time
                    val downTime = System.currentTimeMillis()
                    
                    // Wait for long press or finger release
                    var triggered = false
                    while (true) {
                        val change = awaitPointerEvent().changes.firstOrNull() ?: break
                        val elapsed = System.currentTimeMillis() - downTime
                        
                        if (!change.pressed) {
                            // Finger released - if we already triggered, reset
                            if (triggered) {
                                if (isLeftSide) isLeftHeld = false else isRightHeld = false
                            }
                            break
                        }
                        
                        if (!triggered && elapsed >= longPressTimeout) {
                            // Long press threshold reached - trigger!
                            triggered = true
                            if (isLeftSide) isLeftHeld = true else isRightHeld = true
                        }
                    }
                }
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Spacer to push content to exact vertical center
            Spacer(modifier = Modifier.weight(1f))

            // Show audio icon ONLY when:
            // - Audio mode is ON (pure listening mode)
            // NEVER show icon when paused or in normal mode
            val showAudioIcon = isAudioMode
            
            // Show subtitle when:
            // - Audio mode is OFF (normal mode with subtitles), AND
            // - Dialogue is available
            // Note: Shows even when paused - user can see the last subtitle
            val showSubtitle = !isAudioMode && dialogue != null && !isPlaybackEnded
            
            // Show translation loading animation
            if (isTranslating) {
                Box(
                    modifier = Modifier.size(64.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Pulsing translation icon
                    Icon(
                        imageVector = AppIcons.Translate,
                        contentDescription = null,
                        tint = Mid,
                        modifier = Modifier
                            .size(48.dp)
                            .alpha(alpha)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = if (translationProgress.isNotBlank()) translationProgress else "Generating translations...",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Mid,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                // Animated dots
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    repeat(3) { i ->
                        val dotAlpha by infiniteTransition.animateFloat(
                            initialValue = 0.3f,
                            targetValue = 1f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(600, delayMillis = i * 200),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "dot$i"
                        )
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .alpha(dotAlpha)
                                .background(Mid, shape = androidx.compose.foundation.shape.CircleShape)
                        )
                    }
                }
            }
            
            if (showAudioIcon && !isTranslating) {
                Box(
                    modifier = Modifier.size(64.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = AppIcons.AudioMode,
                        contentDescription = null,
                        tint = White15,
                        modifier = Modifier
                            .size(64.dp)
                            .alpha(if (isPlaying) alpha else 1f)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // Dialogue content - only show when audio mode is OFF and playing
            if (showSubtitle && !isTranslating) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "[${dialogue!!.time}]  ${dialogue.english}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Dark,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = dialogue.bangla,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        color = Mid,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            

        }
    }
}