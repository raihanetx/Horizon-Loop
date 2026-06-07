package com.horizonloop.app.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.horizonloop.app.data.Dialogue
import com.horizonloop.app.ui.theme.Dark
import com.horizonloop.app.ui.theme.Mid
import com.horizonloop.app.ui.theme.White15

@Composable
fun CleanTab(
    dialogue: Dialogue?,
    isAudioMode: Boolean,
    isPlaying: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.4f,
        animationSpec = infiniteRepeatable(animation = tween(1000), repeatMode = RepeatMode.Reverse),
        label = "pulseAlpha"
    )

    Column(
        modifier = modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (isAudioMode || !isPlaying) {
            Box(
                modifier = Modifier.size(64.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.VolumeUp,
                    contentDescription = null,
                    tint = White15,
                    modifier = Modifier.size(64.dp).alpha(if (isPlaying) alpha else 1f)
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (dialogue != null && !isAudioMode) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "[${dialogue.time}] ${dialogue.english}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Dark,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = dialogue.bangla,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = Mid,
                    textAlign = TextAlign.Center
                )
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "Hold Left half to slow down • Hold Right to speed up",
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = White15.copy(alpha = 0.2f),
            letterSpacing = 1.sp
        )
    }
}