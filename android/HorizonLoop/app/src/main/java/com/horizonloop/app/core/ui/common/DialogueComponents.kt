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
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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

    // Card-less design: just text on the dark background, with left-aligned text
    val textColor = if (isPlaying || isSelected) Accent else Dark
    val timeColor = if (isPlaying || isSelected) Accent.copy(alpha = pulseAlpha) else Mid

    val englishWeight = if (isPlaying || isSelected) FontWeight.SemiBold else FontWeight.Medium

    // First line: "[0:45-56] how are you? are you good ?"
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

    // Card-less design with a 3dp left accent bar that lights up emerald
    // when this dialogue is playing or selected. The bar's space is always
    // reserved (transparent when inactive) so layout doesn't shift.
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Max)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(3.dp)
                .fillMaxHeight()
                .background(if (isPlaying || isSelected) Accent else Color.Transparent)
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 17.dp, end = 20.dp, top = 12.dp, bottom = 12.dp),
            horizontalAlignment = Alignment.Start
        ) {
        // First line: [time] English — left-aligned
        Text(
            text = firstLine,
            textAlign = TextAlign.Start,
            maxLines = 4,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(6.dp))
        // Second line: Bangla — left-aligned
        Text(
            text = dialogue.bangla,
            fontSize = 16.sp,
            fontWeight = if (isPlaying || isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = textColor.copy(alpha = if (isPlaying || isSelected) 1f else 0.75f),
            textAlign = TextAlign.Start,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )
        }
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
                .padding(bottom = 32.dp),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(18.dp)
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
