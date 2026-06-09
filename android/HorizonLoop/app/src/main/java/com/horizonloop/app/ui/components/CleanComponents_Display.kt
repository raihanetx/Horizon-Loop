package com.horizonloop.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
 * CleanComponents_Display.kt
 * Display components for clean tab
 */

@Composable
fun DialogueDisplay(dialogue: Dialogue, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp).clip(RoundedCornerShape(8.dp))
        .background(if (isSelected) Dark else Deep).clickable { onClick() }.padding(16.dp)) {
        Column {
            Text(dialogue.english, fontSize = 16.sp, color = if (isSelected) Deep else Mid)
            if (dialogue.bangla.isNotBlank()) {
                Text(dialogue.bangla, fontSize = 14.sp, color = Mid, modifier = Modifier.padding(top = 4.dp))
            }
        }
        Text(dialogue.time, fontSize = 12.sp, color = Mid, modifier = Modifier.align(Alignment.TopEnd))
    }
}

@Composable
fun EmptyDialogueMessage(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("No dialogues yet. Tap 'Translate' to generate translations.", fontSize = 16.sp, color = Mid)
    }
}
