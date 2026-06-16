package com.horizonloop.app.features.player.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.horizonloop.app.core.domain.model.Dialogue
import com.horizonloop.app.core.ui.theme.AppIcons
import com.horizonloop.app.core.ui.theme.Dark
import com.horizonloop.app.core.ui.theme.Mid
import com.horizonloop.app.core.ui.theme.White15

@Composable
fun CleanTab(
    dialogue: Dialogue?,
    isAudioMode: Boolean,
    isPlaying: Boolean,
    isTranslating: Boolean,
    translationProgress: String = "",

    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 0.4f,
        animationSpec = infiniteRepeatable(animation = tween(1000), repeatMode = RepeatMode.Reverse),
        label = "pulseAlpha"
    )
    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.weight(1f))
            val showAudioIcon = isAudioMode
            val showSubtitle = !isAudioMode && dialogue != null
            if (isTranslating) {
                Box(modifier = Modifier.size(64.dp), contentAlignment = Alignment.Center) {
                    Icon(AppIcons.Translate, contentDescription = null, tint = Mid, modifier = Modifier.size(48.dp).alpha(alpha))
                }
                Spacer(modifier = Modifier.height(20.dp))
                Text(if (translationProgress.isNotBlank()) translationProgress else "Generating translations...", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Mid, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    repeat(3) { i ->
                        val dotAlpha by infiniteTransition.animateFloat(
                            initialValue = 0.3f, targetValue = 1f,
                            animationSpec = infiniteRepeatable(animation = tween(600, delayMillis = i * 200), repeatMode = RepeatMode.Reverse),
                            label = "dot$i"
                        )
                        Box(modifier = Modifier.size(10.dp).alpha(dotAlpha).background(Mid, shape = androidx.compose.foundation.shape.CircleShape))
                    }
                }
            }
            if (showAudioIcon && !isTranslating) {
                Box(modifier = Modifier.size(64.dp), contentAlignment = Alignment.Center) {
                    Icon(AppIcons.AudioMode, contentDescription = null, tint = White15, modifier = Modifier.size(56.dp).alpha(if (isPlaying) alpha else 1f))
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
            if (showSubtitle && !isTranslating) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                    Text("[${dialogue!!.time}]  ${dialogue.english}", fontSize = 20.sp, fontWeight = FontWeight.Medium, color = Dark, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(dialogue.bangla, fontSize = 16.sp, fontWeight = FontWeight.Normal, color = Mid, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp))
                }
            }
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}