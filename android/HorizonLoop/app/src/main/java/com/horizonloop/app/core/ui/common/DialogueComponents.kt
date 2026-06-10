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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.horizonloop.app.core.data.formatTimestamp
import com.horizonloop.app.core.domain.model.Dialogue
import com.horizonloop.app.core.ui.theme.AppIcons
import com.horizonloop.app.core.ui.theme.Accent
import com.horizonloop.app.core.ui.theme.Dark
import com.horizonloop.app.core.ui.theme.Deep
import com.horizonloop.app.core.ui.theme.Mid
import com.horizonloop.app.core.ui.theme.Muted
import com.horizonloop.app.core.ui.theme.Surface

@Composable
fun DialogueCard(
    dialogue: Dialogue,
    isPlaying: Boolean = false,
    isSelected: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 0.4f,
        animationSpec = infiniteRepeatable(animation = tween(800), repeatMode = RepeatMode.Reverse),
        label = "pulseAnim"
    )
    // White text when playing or selected, gray when neither
    val textColor = when {
        isPlaying -> Color.White
        isSelected -> Color.White.copy(alpha = 0.85f)
        else -> Mid
    }
    // Left divider bar — accent when playing, subtle gray otherwise
    val dividerColor = if (isPlaying) Accent else Dark.copy(alpha = 0.25f)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left accent divider bar
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(48.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(dividerColor)
                .then(if (isPlaying) Modifier.alpha(pulseAlpha) else Modifier)
        )
        Spacer(modifier = Modifier.width(18.dp))
        // Text block — centered horizontally within the card
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Time + English on the same line
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "[${formatTimestamp(dialogue.startTime)} → ${formatTimestamp(dialogue.endTime)}]",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = textColor.copy(alpha = if (isPlaying) 0.7f else 0.55f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = dialogue.english,
                    fontSize = 16.sp,
                    fontWeight = if (isPlaying) FontWeight.Bold else FontWeight.Normal,
                    color = textColor,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
            }
            Spacer(modifier = Modifier.height(5.dp))
            // Bangla text on next line
            Text(
                text = dialogue.bangla,
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal,
                color = textColor.copy(alpha = if (isPlaying) 0.8f else 0.45f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
        // Small playing indicator dot on right when playing
        if (isPlaying) {
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(Accent)
                    .alpha(pulseAlpha)
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
        Box(modifier = modifier.fillMaxWidth().background(Surface).padding(32.dp), contentAlignment = Alignment.Center) {
            Text("No dialogues yet — tap Translate to generate", fontSize = 13.sp, color = Mid.copy(alpha = 0.5f), textAlign = TextAlign.Center)
        }
    } else {
        LazyColumn(
            modifier = modifier.fillMaxWidth().background(Deep).padding(horizontal = 16.dp).padding(bottom = 32.dp),
            contentPadding = PaddingValues(top = 16.dp)
        ) {
            items(dialogues.size) { index ->
                val dialogue = dialogues[index]
                Column {
                    DialogueCard(
                        dialogue = dialogue,
                        isPlaying = dialogue.id == playingDialogueId,
                        isSelected = dialogue.id in selectedDialogueIds,
                        onClick = { onDialogueClick(context, dialogue) }
                    )
                    // Thin divider between items (not after last item)
                    if (index < dialogues.lastIndex) {
                        Spacer(modifier = Modifier.height(0.dp))
                        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Dark.copy(alpha = 0.08f)))
                    }
                }
            }
        }
    }
}