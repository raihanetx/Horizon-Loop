package com.horizonloop.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.horizonloop.app.data.Dialogue
import com.horizonloop.app.ui.theme.Deep

/**
 * CleanComponents_Main.kt
 * Main composables for clean tab
 */

@Composable
fun CleanTab(
    isAudioMode: Boolean, isPlaying: Boolean, isPlaybackEnded: Boolean, dialogues: List<Dialogue>,
    selectedDialogueIds: Set<Int>, onDialogueSelect: (Dialogue) -> Unit, modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize().background(Deep)) {
        if (dialogues.isEmpty()) {
            EmptyDialogueMessage(Modifier.fillMaxSize())
        } else {
            dialogues.forEach { dialogue ->
                DialogueDisplay(dialogue, selectedDialogueIds.contains(dialogue.id), { onDialogueSelect(dialogue) })
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}
