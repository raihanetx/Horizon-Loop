package com.horizonloop.app.core.ui.common

import android.content.Context
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.horizonloop.app.core.data.formatTimestamp
import com.horizonloop.app.core.domain.model.Dialogue
import com.horizonloop.app.core.ui.theme.Accent
import com.horizonloop.app.core.ui.theme.Dark
import com.horizonloop.app.core.ui.theme.Deep
import com.horizonloop.app.core.ui.theme.Mid

@Composable
fun DialogueCard(
    dialogue: Dialogue,
    isPlaying: Boolean = false,
    isSelected: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Subtle pulse for the time line when playing
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 0.55f,
        animationSpec = infiniteRepeatable(animation = tween(900), repeatMode = RepeatMode.Reverse),
        label = "pulseAnim"
    )

    // White text on click/playing, muted gray when idle — no card background, no side bars
    val bodyColor = when {
        isPlaying -> Color.White
        isSelected -> Color.White.copy(alpha = 0.92f)
        else -> Dark.copy(alpha = 0.78f)
    }
    val timeColor = when {
        isPlaying -> Accent.copy(alpha = pulseAlpha)
        isSelected -> Color.White.copy(alpha = 0.7f)
        else -> Mid
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp, horizontal = 0.dp)
    ) {
        // Time on the first line
        Text(
            text = "${formatTimestamp(dialogue.startTime)} → ${formatTimestamp(dialogue.endTime)}",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = timeColor
        )
        Spacer(modifier = Modifier.height(8.dp))
        // English text
        Text(
            text = dialogue.english,
            fontSize = 16.sp,
            fontWeight = if (isPlaying || isSelected) FontWeight.SemiBold else FontWeight.Medium,
            color = bodyColor,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(5.dp))
        // Bangla text — same size as English
        Text(
            text = dialogue.bangla,
            fontSize = 16.sp,
            fontWeight = if (isPlaying || isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = bodyColor.copy(alpha = if (isPlaying || isSelected) 1f else 0.82f),
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun DialogueTab(
    context: Context,
    dialogues: List<Dialogue>,
    playingDialogueId: Int? = null,
    selectedDialogueIds: Set<Int> = emptySet(),
    onDialogueClick: (Context, Dialogue) -> Unit,
    modifier: Modifier = Modifier
) {
    if (dialogues.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .background(Deep)
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "No dialogues yet — tap Translate to generate",
                fontSize = 13.sp,
                color = Mid.copy(alpha = 0.5f),
                textAlign = TextAlign.Center
            )
        }
    } else {
        LazyColumn(
            modifier = modifier
                .fillMaxWidth()
                .background(Deep)
                .padding(horizontal = 18.dp)
                .padding(bottom = 32.dp),
            contentPadding = PaddingValues(top = 14.dp)
        ) {
            items(dialogues.size) { index ->
                val dialogue = dialogues[index]
                DialogueCard(
                    dialogue = dialogue,
                    isPlaying = dialogue.id == playingDialogueId,
                    isSelected = dialogue.id in selectedDialogueIds,
                    onClick = { onDialogueClick(context, dialogue) }
                )
            }
        }
    }
}
