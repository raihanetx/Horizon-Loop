package com.horizonloop.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.horizonloop.app.data.Dialogue
import com.horizonloop.app.ui.theme.Mid
import com.horizonloop.app.ui.theme.Surface

/**
 * DialogueComponents_Tab.kt
 * DialogueTab composable for displaying list of dialogues
 */

@Composable
fun DialogueTab(
    dialogues: List<Dialogue>, playingDialogueId: Int? = null,
    selectedDialogueIds: Set<Int> = emptySet(), onDialogueClick: (Dialogue) -> Unit,
    modifier: Modifier = Modifier
) {
    if (dialogues.isEmpty()) {
        Box(modifier = modifier.fillMaxWidth().background(Surface).padding(32.dp), contentAlignment = Alignment.Center) {
            Text("No dialogues available", fontSize = 12.sp, color = Mid)
        }
    } else {
        LazyColumn(modifier = modifier.fillMaxWidth().background(Surface).padding(horizontal = 16.dp).padding(bottom = 20.dp)) {
            items(dialogues, key = { it.id }) { dialogue ->
                DialogueCard(dialogue = dialogue, isSelected = dialogue.id in selectedDialogueIds, onClick = { onDialogueClick(dialogue) })
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}
