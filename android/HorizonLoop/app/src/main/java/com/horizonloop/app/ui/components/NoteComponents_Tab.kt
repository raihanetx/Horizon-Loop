package com.horizonloop.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.horizonloop.app.data.Note
import com.horizonloop.app.ui.theme.AppIcons
import com.horizonloop.app.ui.theme.Dark
import com.horizonloop.app.ui.theme.Mid
import com.horizonloop.app.ui.theme.Muted

/**
 * NoteComponents_Tab.kt
 * Notes tab composable
 */

@Composable
fun NotesTab(
    notes: List<Note>,
    onAddNote: (String) -> Unit,
    onDeleteNote: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }
    var selectedNote by remember { mutableStateOf<Note?>(null) }

    Box(modifier = modifier.fillMaxSize()) {
        if (notes.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                Text("No notes yet.", fontSize = 12.sp, color = Mid)
            }
        } else {
            Column(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                notes.forEach { note -> NoteCard(note = note, onClick = { selectedNote = note }) }
            }
        }

        FloatingActionButton(
            onClick = { showDialog = true },
            modifier = Modifier.align(Alignment.BottomEnd).padding(bottom = 34.dp, end = 16.dp).size(48.dp),
            shape = CircleShape,
            containerColor = Muted,
            contentColor = Dark
        ) {
            Icon(AppIcons.Add, contentDescription = "Add Note", modifier = Modifier.size(20.dp))
        }
    }

    if (selectedNote != null) {
        NoteDetailDialog(note = selectedNote!!, onDismiss = { selectedNote = null }, onDelete = { id -> onDeleteNote(id); selectedNote = null })
    }

    if (showDialog) {
        AddNoteDialog(onDismiss = { showDialog = false }, onSave = { text -> onAddNote(text); showDialog = false })
    }
}
