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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.horizonloop.app.data.Dialogue
import com.horizonloop.app.ui.theme.Dark
import com.horizonloop.app.ui.theme.Deep
import com.horizonloop.app.ui.theme.Mid

/**
 * DialogueComponents_Card.kt
 * Card components for dialogue display
 */

@Composable
fun DialogueCard(dialogue: Dialogue, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp).clip(RoundedCornerShape(8.dp))
        .background(if (isSelected) Dark else Deep).clickable { onClick() }.padding(12.dp),
        verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(1f)) {
            Text(dialogue.english, fontSize = 14.sp, fontWeight = FontWeight.Normal, color = if (isSelected) Deep else Mid, maxLines = 2)
            if (dialogue.bangla.isNotBlank()) {
                Text(dialogue.bangla, fontSize = 12.sp, color = Mid, maxLines = 1, modifier = Modifier.padding(top = 2.dp))
            }
        }
        Text(dialogue.time, fontSize = 12.sp, color = Mid)
    }
}
