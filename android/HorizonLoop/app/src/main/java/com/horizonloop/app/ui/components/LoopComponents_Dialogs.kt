package com.horizonloop.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.horizonloop.app.data.Dialogue
import com.horizonloop.app.ui.theme.Dark
import com.horizonloop.app.ui.theme.Deep
import com.horizonloop.app.ui.theme.Mid

/**
 * LoopComponents_Dialogs.kt
 * Dialogue-related loop components
 */

@Composable
fun DialogueLoopList(dialogues: List<Dialogue>, selectedDialogueIds: Set<Int>, onDialogueSelect: (Dialogue) -> Unit, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier.fillMaxSize().background(Deep)) {
        items(dialogues, key = { it.id }) { dialogue ->
            DialogueLoopItem(dialogue, selectedDialogueIds.contains(dialogue.id), { onDialogueSelect(dialogue) })
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

@Composable
private fun DialogueLoopItem(dialogue: Dialogue, isSelected: Boolean, onClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp).clip(RoundedCornerShape(8.dp))
        .background(if (isSelected) Dark else Deep).clickable { onClick() }.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(dialogue.english, fontSize = 14.sp, color = if (isSelected) Deep else Mid, modifier = Modifier.weight(1f))
        Text(dialogue.time, fontSize = 12.sp, color = Mid)
    }
}
