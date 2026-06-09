package com.horizonloop.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.horizonloop.app.data.Note
import com.horizonloop.app.ui.theme.Dark
import com.horizonloop.app.ui.theme.Mid
import com.horizonloop.app.ui.theme.Surface

/**
 * NoteComponents_Dialog_Detail.kt
 * Note detail dialog
 */

@Composable
fun NoteDetailDialog(note: Note, onDismiss: () -> Unit, onDelete: (Int) -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(Surface).padding(20.dp)) {
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Note", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Dark)
                    Text(text = "🗑️", fontSize = 16.sp, modifier = Modifier.clickable { onDelete(note.id); onDismiss() })
                }
                Text(text = note.text, fontSize = 14.sp, color = Dark, fontWeight = FontWeight.Normal, lineHeight = 20.sp)
                Text(text = "${note.date} | ${note.text.split("\\s+".toRegex()).filter { it.isNotBlank() }.size} words", fontSize = 11.sp, color = Mid)
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                    TextButton(onClick = onDismiss) { Text("Close", color = Mid, fontSize = 13.sp, fontWeight = FontWeight.SemiBold) }
                }
            }
        }
    }
}
