package com.horizonloop.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import com.horizonloop.app.ui.theme.AppIcons
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import com.horizonloop.app.data.Note
import com.horizonloop.app.ui.theme.Dark
import com.horizonloop.app.ui.theme.Deep
import com.horizonloop.app.ui.theme.Mid
import com.horizonloop.app.ui.theme.Muted
import com.horizonloop.app.ui.theme.Surface
import com.horizonloop.app.ui.components.NoteDetailDialog
import com.horizonloop.app.ui.components.NoteAddDialog

@Composable
fun NoteCard(
    note: Note,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Muted.copy(alpha = 0.04f))
            .clickable(onClick = onClick)
            .padding(18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = AppIcons.Note,
            contentDescription = null,
            tint = Mid,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            val shortText = if (note.text.length > 40) note.text.take(40) + "..." else note.text
            Text(
                text = shortText,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Dark
            )
            Spacer(modifier = Modifier.height(4.dp))
            val words = note.text.trim().split("\\s+".toRegex()).filter { it.isNotBlank() }.size
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(note.date, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Mid)
                Spacer(modifier = Modifier.width(8.dp))
                Box(modifier = Modifier.width(1.dp).height(10.dp).background(Dark.copy(alpha = 0.3f)))
                Spacer(modifier = Modifier.width(8.dp))
                Text("$words words", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Mid)
                Spacer(modifier = Modifier.width(8.dp))
                Box(modifier = Modifier.width(1.dp).height(10.dp).background(Dark.copy(alpha = 0.3f)))
                Spacer(modifier = Modifier.width(8.dp))
                Text("${note.text.length} chars", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Mid)
            }
        }
    }
}

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
            Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                notes.forEach { note -> NoteCard(note = note, onClick = { selectedNote = note }) }
            }
        }
        FloatingActionButton(
            onClick = { showDialog = true },
            modifier = Modifier.align(Alignment.BottomEnd).padding(bottom = 34.dp, end = 16.dp).size(48.dp),
            shape = androidx.compose.foundation.shape.CircleShape,
            containerColor = Muted,
            contentColor = Dark
        ) { Icon(AppIcons.Add, contentDescription = "Add Note", modifier = Modifier.size(20.dp)) }
    }

    if (selectedNote != null) {
        NoteDetailDialog(note = selectedNote!!, onDelete = { onDeleteNote(selectedNote!!.id); selectedNote = null }, onDismiss = { selectedNote = null })
    }
    if (showDialog) {
        NoteAddDialog(onAdd = { onAddNote(it); showDialog = false }, onDismiss = { showDialog = false })
    }
}
