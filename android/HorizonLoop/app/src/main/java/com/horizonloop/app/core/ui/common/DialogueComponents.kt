package com.horizonloop.app.core.ui.common

import android.content.Context
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.horizonloop.app.core.data.formatTimeRange
import com.horizonloop.app.core.domain.model.Dialogue
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
    val isActive = isPlaying || isSelected
    // English + Bangla share the same color/size/weight. Default gray,
    // white on select/playing. Only the font weight flips.
    val textColor = if (isActive) Dark else Mid
    val textWeight = if (isActive) FontWeight.SemiBold else FontWeight.Medium

    val timeText = formatTimeRange(dialogue.startTime, dialogue.endTime)

    // Single AnnotatedString: "[time] english" — same color/size/weight
    // as the bangla line below for visual parity.
    val firstLine = buildAnnotatedString {
        append("[$timeText] ")
        append(dialogue.english)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = firstLine,
                color = textColor,
                fontSize = 16.sp,
                fontWeight = textWeight,
                textAlign = TextAlign.Center,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = dialogue.bangla,
                color = textColor,
                fontSize = 16.sp,
                fontWeight = textWeight,
                textAlign = TextAlign.Center,
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
                .padding(bottom = 12.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            itemsIndexed(dialogues) { _, dialogue ->
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
