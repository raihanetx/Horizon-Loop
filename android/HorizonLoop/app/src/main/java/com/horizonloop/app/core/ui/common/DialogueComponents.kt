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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.horizonloop.app.core.data.formatTimeRange
import com.horizonloop.app.core.domain.model.Dialogue
import com.horizonloop.app.core.ui.theme.Accent
import com.horizonloop.app.core.ui.theme.Dark
import com.horizonloop.app.core.ui.theme.Deep
import com.horizonloop.app.core.ui.theme.Mid
import com.horizonloop.app.core.ui.theme.Surface

@Composable
fun DialogueCard(
    dialogue: Dialogue,
    isPlaying: Boolean = false,
    isSelected: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Subtle pulse for the time prefix when playing
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 0.55f,
        animationSpec = infiniteRepeatable(animation = tween(900), repeatMode = RepeatMode.Reverse),
        label = "pulseAnim"
    )

    // Same color combination as the rest of the app:
    // Surface card on Deep background, off-white text, amber on click/select
    val textColor = if (isPlaying || isSelected) Accent else Dark
    val timeColor = if (isPlaying || isSelected) Accent.copy(alpha = pulseAlpha) else Mid

    val englishWeight = if (isPlaying || isSelected) FontWeight.SemiBold else FontWeight.Medium

    // First line: "[0:45-56] how are you? are you good ?"
    val timeText = formatTimeRange(dialogue.startTime, dialogue.endTime)
    val firstLine = buildAnnotatedString {
        withStyle(
            SpanStyle(
                color = timeColor,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
        ) {
            append("[$timeText] ")
        }
        withStyle(
            SpanStyle(
                color = textColor,
                fontSize = 15.sp,
                fontWeight = englishWeight
            )
        ) {
            append(dialogue.english)
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Surface)
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp, horizontal = 18.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // First line: [time] English
        Text(
            text = firstLine,
            textAlign = TextAlign.Center,
            maxLines = 4,
            overflow = TextOverflow.Ellipsis
        )
        // Second line: Bangla — same size as English
        Text(
            text = dialogue.bangla,
            fontSize = 15.sp,
            fontWeight = if (isPlaying || isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = textColor.copy(alpha = if (isPlaying || isSelected) 1f else 0.75f),
            textAlign = TextAlign.Center,
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
                color = Mid.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }
    } else {
        LazyColumn(
            modifier = modifier
                .fillMaxWidth()
                .background(Deep)
                .padding(bottom = 32.dp),
            contentPadding = PaddingValues(top = 6.dp)
        ) {
            items(dialogues.size) { index ->
                val dialogue = dialogues[index]
                DialogueCard(
                    dialogue = dialogue,
                    isPlaying = dialogue.id == playingDialogueId,
                    isSelected = dialogue.id in selectedDialogueIds,
                    onClick = { onDialogueClick(context, dialogue) }
                )
                if (index < dialogues.lastIndex) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(separatorColorOf(index, dialogues, playingDialogueId, selectedDialogueIds))
                    )
                }
            }
        }
    }
}

private fun separatorColorOf(
    index: Int,
    dialogues: List<Dialogue>,
    playingId: Int?,
    selected: Set<Int>
): Color {
    val current = dialogues[index]
    val next = dialogues.getOrNull(index + 1) ?: return Mid.copy(alpha = 0.3f)
    val active = current.id == playingId || current.id in selected ||
        next.id == playingId || next.id in selected
    return if (active) Accent.copy(alpha = 0.4f) else Mid.copy(alpha = 0.3f)
}
