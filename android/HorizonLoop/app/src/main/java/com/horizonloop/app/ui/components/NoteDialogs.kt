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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.horizonloop.app.data.Note
import com.horizonloop.app.ui.theme.Dark
import com.horizonloop.app.ui.theme.Deep
import com.horizonloop.app.ui.theme.Mid
import com.horizonloop.app.ui.theme.Muted
import com.horizonloop.app.ui.theme.Surface

@Composable
fun NoteDetailDialog(
    note: Note,
    onDelete: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Surface)
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Note", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Dark)
                    Text("🗑️", fontSize = 16.sp, modifier = Modifier.clickable { onDelete(note.id); onDismiss() })
                }
                Text(note.text, fontSize = 14.sp, color = Dark, fontWeight = FontWeight.Normal, lineHeight = 20.sp)
                Text(
                    "${note.date} | ${note.text.trim().split("\\s+".toRegex()).filter { it.isNotBlank() }.size} words",
                    fontSize = 11.sp, color = Mid
                )
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                    TextButton(onClick = onDismiss) { Text("Close", color = Mid, fontSize = 13.sp, fontWeight = FontWeight.SemiBold) }
                }
            }
        }
    }
}

@Composable
fun NoteAddDialog(
    onAdd: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var noteText by remember { mutableStateOf("") }
    var previewNote by remember { mutableStateOf("") }
    var showPreview by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = { onDismiss(); noteText = ""; previewNote = ""; showPreview = false }) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Surface)
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Add Note", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Dark)

                if (showPreview && previewNote.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Muted.copy(alpha = 0.15f))
                            .padding(16.dp)
                    ) {
                        Text(previewNote, fontSize = 13.sp, color = Dark.copy(alpha = 0.7f), fontWeight = FontWeight.Normal)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = {
                        if (noteText.isNotBlank()) { previewNote = noteText; showPreview = true }
                    }) { Text("Preview", color = Mid, fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }
                    TextButton(onClick = { onDismiss(); noteText = ""; previewNote = ""; showPreview = false }) {
                        Text("Cancel", color = Mid, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Button(
                        onClick = {
                            if (noteText.isNotBlank()) {
                                onAdd(noteText.trim())
                                noteText = ""; previewNote = ""; showPreview = false; onDismiss()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Mid, contentColor = Deep),
                        shape = RoundedCornerShape(12.dp)
                    ) { Text("Save", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }
                }
            }
        }
    }
}