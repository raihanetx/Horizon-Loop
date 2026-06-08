package com.horizonloop.app.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.horizonloop.app.data.Dialogue
import com.horizonloop.app.ui.theme.Dark
import com.horizonloop.app.ui.theme.Mid
import com.horizonloop.app.ui.theme.Muted
import com.horizonloop.app.ui.theme.Surface
import com.horizonloop.app.ui.theme.White20

@Composable
fun DialogueCard(
    dialogue: Dialogue,
    isPlaying: Boolean = false,
    isSelected: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulseBorder")
    val borderAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.5f,
        animationSpec = infiniteRepeatable(animation = tween(800), repeatMode = RepeatMode.Reverse),
        label = "borderPulse"
    )
    
    // Selection color - white thin border
    val selectionColor = Color.White
    
    // Border: selection takes priority (white), then playing pulse
    val borderColor = when {
        isSelected -> selectionColor
        isPlaying -> Dark.copy(alpha = borderAlpha)
        else -> Dark.copy(alpha = 0.15f)
    }
    
    val englishColor = if (isPlaying) Dark else Dark
    val banglaColor = if (isPlaying) Mid else Mid.copy(alpha = 0.7f)
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) White20 else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "[${dialogue.time}]",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Mid.copy(alpha = 0.6f),
                modifier = Modifier.align(Alignment.CenterVertically)
            )
            Text(
                text = " ${dialogue.english}",
                fontSize = 14.sp,
                fontWeight = if (isPlaying) FontWeight.Bold else FontWeight.SemiBold,
                color = englishColor,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }
        Text(
            text = dialogue.bangla,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            color = banglaColor
        )
    }
}

@Composable
fun DialogueTab(
    dialogues: List<Dialogue>,
    playingDialogueId: Int? = null,
    selectedDialogueIds: Set<Int> = emptySet(),
    onDialogueClick: (Dialogue) -> Unit,
    modifier: Modifier = Modifier
) {
    if (dialogues.isEmpty()) {
        Box(modifier = modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
            Text("No dialogues available", fontSize = 12.sp, color = Mid)
        }
    } else {
        LazyColumn(
            modifier = modifier.fillMaxWidth().padding(horizontal = 30.dp).padding(bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(top = 0.dp)
        ) {
            items(dialogues, key = { it.id }) { dialogue ->
                DialogueCard(
                    dialogue = dialogue,
                    isPlaying = dialogue.id == playingDialogueId,
                    isSelected = dialogue.id in selectedDialogueIds,
                    onClick = { onDialogueClick(dialogue) }
                )
            }
        }
    }
}