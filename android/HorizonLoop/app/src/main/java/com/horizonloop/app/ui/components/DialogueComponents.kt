package com.horizonloop.app.ui.components

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.horizonloop.app.data.Dialogue
import com.horizonloop.app.ui.theme.Dark
import com.horizonloop.app.ui.theme.Mid
import com.horizonloop.app.ui.theme.Muted

@Composable
fun DialogueCard(
    dialogue: Dialogue,
    isPlaying: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Muted.copy(alpha = 0.04f))
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row {
            Text(
                text = "[${dialogue.time}]",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Mid
            )
            Text(
                text = " ${dialogue.english}",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Dark
            )
        }
        Text(
            text = dialogue.bangla,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            color = Mid
        )
    }
}

@Composable
fun DialogueTab(
    dialogues: List<Dialogue>,
    playingDialogueId: Int? = null,
    onDialogueClick: (Dialogue) -> Unit,
    modifier: Modifier = Modifier
) {
    if (dialogues.isEmpty()) {
        Box(modifier = modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
            Text("No dialogues available", fontSize = 12.sp, color = Mid)
        }
    } else {
        Column(
            modifier = modifier.fillMaxWidth().padding(12.dp).padding(bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            dialogues.forEach { dialogue ->
                DialogueCard(
                    dialogue = dialogue,
                    isPlaying = dialogue.id == playingDialogueId,
                    onClick = { onDialogueClick(dialogue) }
                )
            }
        }
    }
}