package com.horizonloop.app.core.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.horizonloop.app.core.domain.model.Note
import com.horizonloop.app.core.ui.theme.Dark
import com.horizonloop.app.core.ui.theme.Deep
import com.horizonloop.app.core.ui.theme.Mid
import com.horizonloop.app.core.ui.theme.Muted
import com.horizonloop.app.core.ui.theme.Surface

@Composable
fun NoteDialog(note: Note, onDelete: (Int) -> Unit, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp)).background(Surface).padding(22.dp)) {
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Note", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Dark)
                Text("🗑️", fontSize = 18.sp, modifier = Modifier.clickable { onDelete(note.id); onDismiss() })
                }
                Text(note.text, fontSize = 15.sp, color = Dark, fontWeight = FontWeight.Normal, lineHeight = 22.sp)
                Text("${note.date} | ${note.text.trim().split("\\s+".toRegex()).filter { it.isNotBlank() }.size} words", fontSize = 12.sp, color = Mid)
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                    TextButton(onClick = onDismiss) { Text("Close", color = Mid, fontSize = 13.sp, fontWeight = FontWeight.SemiBold) }
                }
            }
        }
    }
}

@Composable
fun NoteAddDialog(onAdd: (String) -> Unit, onDismiss: () -> Unit) {
    var noteText by remember { mutableStateOf("") }
    var previewNote by remember { mutableStateOf("") }
    var showPreview by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = { onDismiss(); noteText = ""; previewNote = ""; showPreview = false }) {
        Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp)).background(Surface).padding(22.dp)) {
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Add Note", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Dark)
                OutlinedTextField(
                    value = noteText,
                    onValueChange = { noteText = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Your note", fontSize = 13.sp, color = Mid) },
                    placeholder = { Text("Type something...", fontSize = 14.sp, color = Mid.copy(alpha = 0.6f)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Dark, unfocusedTextColor = Dark,
                        focusedBorderColor = Mid, unfocusedBorderColor = Muted.copy(alpha = 0.4f), cursorColor = Dark
                    ),
                    minLines = 3,
                    maxLines = 6
                )
                if (showPreview && previewNote.isNotBlank()) {
                    Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(Muted.copy(alpha = 0.15f)).padding(20.dp)) {
                        Text(previewNote, fontSize = 14.sp, color = Dark.copy(alpha = 0.7f), fontWeight = FontWeight.Normal)
                    }
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End), verticalAlignment = Alignment.CenterVertically) {
                    TextButton(onClick = { if (noteText.isNotBlank()) { previewNote = noteText; showPreview = true } }) { Text("Preview", color = Mid, fontSize = 13.sp, fontWeight = FontWeight.SemiBold) }
                    TextButton(onClick = { onDismiss(); noteText = ""; previewNote = ""; showPreview = false }) { Text("Cancel", color = Mid, fontSize = 13.sp, fontWeight = FontWeight.SemiBold) }
                    Button(onClick = { if (noteText.isNotBlank()) { onAdd(noteText.trim()); noteText = ""; previewNote = ""; showPreview = false; onDismiss() } }, colors = ButtonDefaults.buttonColors(containerColor = Mid, contentColor = Deep), shape = RoundedCornerShape(14.dp)) { Text("Save", fontSize = 13.sp, fontWeight = FontWeight.SemiBold) }
                }
            }
        }
    }
}