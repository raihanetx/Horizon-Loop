package com.horizonloop.app.core.ui.common

import android.content.Context
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.horizonloop.app.core.ui.theme.Muted

@Composable
fun DialogueCard(
    dialogue: Dialogue,
    displayNumber: Int,
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

    val isActive = isPlaying || isSelected
    val textColor = if (isActive) Accent else Dark
    val timeColor = when {
        isPlaying -> Accent.copy(alpha = pulseAlpha)
        isSelected -> Accent
        else -> Mid
    }
    val englishWeight = if (isActive) FontWeight.SemiBold else FontWeight.Medium

    val durationSec = (dialogue.endTime - dialogue.startTime).toInt().coerceAtLeast(0)
    val timeText = formatTimeRange(dialogue.startTime, dialogue.endTime)

    val firstLine = buildAnnotatedString {
        withStyle(
            SpanStyle(
                color = timeColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        ) {
            append("[$timeText] ")
        }
        withStyle(
            SpanStyle(
                color = textColor,
                fontSize = 16.sp,
                fontWeight = englishWeight
            )
        ) {
            append(dialogue.english)
        }
    }

    // Flat list item — not a separate card. No background, no always-on border.
    // When active (playing/selected), a 1dp Accent border with 14dp rounded
    // corners appears. Numbered circle on the left identifies order (1, 2, 3…).
    // Total seconds of the segment shown on the right.
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(
                width = 1.dp,
                color = if (isActive) Accent else Color.Transparent,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
            .clip(CircleShape)
                .background(if (isActive) Accent else Muted),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = displayNumber.toString(),
                color = if (isActive) Deep else Mid,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = firstLine,
                textAlign = TextAlign.Start,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = dialogue.bangla,
                fontSize = 16.sp,
                fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal,
                color = textColor.copy(alpha = if (isActive) 1f else 0.75f),
                textAlign = TextAlign.Start,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = "${durationSec}s",
            color = if (isActive) Accent else Mid,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
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
                fontSize = 14.sp,
                color = Mid.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }
    } else {
        LazyColumn(
            modifier = modifier
                .fillMaxWidth()
                .background(Deep)
                .padding(bottom = 12.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            itemsIndexed(dialogues) { index, dialogue ->
                DialogueCard(
                    dialogue = dialogue,
                    displayNumber = index + 1,
                    isPlaying = dialogue.id == playingDialogueId,
                    isSelected = dialogue.id in selectedDialogueIds,
                    onClick = { onDialogueClick(context, dialogue) }
                )
            }
        }
    }
}
