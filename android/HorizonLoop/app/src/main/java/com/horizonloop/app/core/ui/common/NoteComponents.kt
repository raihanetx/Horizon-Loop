package com.horizonloop.app.core.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import com.horizonloop.app.core.ui.theme.AppIcons
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.horizonloop.app.core.domain.model.Note
import com.horizonloop.app.core.ui.theme.Dark
import com.horizonloop.app.core.ui.theme.Mid
import com.horizonloop.app.core.ui.theme.Muted
import com.horizonloop.app.core.ui.theme.Surface

@Composable
fun NoteCard(
    note: Note,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Muted.copy(alpha = 0.04f))
            .clickable(onClick = onClick)
            .padding(24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = AppIcons.Note, contentDescription = null, tint = Mid, modifier = Modifier.size(28.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            val shortText = if (note.text.length > 50) note.text.take(50) + "..." else note.text
            Text(text = shortText, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Dark)
            Spacer(modifier = Modifier.height(6.dp))
            val words = note.text.trim().split("\\s+".toRegex()).filter { it.isNotBlank() }.size
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(note.date, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Mid)
                Spacer(modifier = Modifier.width(10.dp))
                Box(modifier = Modifier.width(1.dp).height(12.dp).background(Dark.copy(alpha = 0.3f)))
                Spacer(modifier = Modifier.width(10.dp))
                Text("$words words", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Mid)
                Spacer(modifier = Modifier.width(10.dp))
                Box(modifier = Modifier.width(1.dp).height(12.dp).background(Dark.copy(alpha = 0.3f)))
                Spacer(modifier = Modifier.width(10.dp))
                Text("${note.text.length} chars", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Mid)
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
            modifier = Modifier.align(Alignment.BottomEnd).padding(bottom = 40.dp, end = 20.dp).size(56.dp),
            shape = androidx.compose.foundation.shape.CircleShape,
            containerColor = Muted, contentColor = Dark
        ) { Icon(AppIcons.Add, contentDescription = "Add Note", modifier = Modifier.size(24.dp)) }
    }
    if (selectedNote != null) {
        NoteDialog(note = selectedNote!!, onDelete = { onDeleteNote(selectedNote!!.id); selectedNote = null }, onDismiss = { selectedNote = null })
    }
    if (showDialog) {
        NoteAddDialog(onAdd = { onAddNote(it); showDialog = false }, onDismiss = { showDialog = false })
    }
}