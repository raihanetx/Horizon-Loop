package com.horizonloop.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.horizonloop.app.data.ActiveTab
import com.horizonloop.app.data.Dialogue
import com.horizonloop.app.data.Loop
import com.horizonloop.app.data.Note
import com.horizonloop.app.ui.components.AudioControls
import com.horizonloop.app.ui.components.CapsuleMenu
import com.horizonloop.app.ui.components.CleanTab
import com.horizonloop.app.ui.components.DialogueTab
import com.horizonloop.app.ui.components.LoopsTab
import com.horizonloop.app.ui.components.NotesTab
import com.horizonloop.app.ui.components.SpeedTab
import com.horizonloop.app.ui.theme.Dark
import com.horizonloop.app.ui.theme.Deep
import com.horizonloop.app.ui.theme.Mid
import com.horizonloop.app.ui.theme.Muted

@Composable
fun PlayerScreen(
    title: String,
    activeTab: ActiveTab,
    isPlaying: Boolean,
    isAudioMode: Boolean,
    currentTime: String,
    totalTime: String,
    progress: Float,
    currentSpeed: Float,
    speeds: List<Float>,
    activeLoopId: Int?,
    notes: List<Note>,
    loops: List<Loop>,
    dialogues: List<Dialogue>,
    currentDialogue: Dialogue?,
    showCapsuleMenu: Boolean,
    onBack: () -> Unit,
    onMenuClick: () -> Unit,
    onTabClick: (ActiveTab) -> Unit,
    onAudioModeToggle: () -> Unit,
    onTranslate: () -> Unit,
    onPlayPause: () -> Unit,
    onRewind: () -> Unit,
    onForward: () -> Unit,
    onAddNote: (String) -> Unit,
    onAddLoop: (String, String, String, Int) -> Unit,
    onPlayLoop: (Loop) -> Unit,
    onSpeedChange: (Int) -> Unit,
    onDismissCapsule: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize().background(Deep)) {
        Column(modifier = Modifier.fillMaxSize()) {
            PlayerHeader(title = title, onBack = onBack, onMenuClick = onMenuClick)
            TabContent(
                activeTab = activeTab,
                isAudioMode = isAudioMode,
                isPlaying = isPlaying,
                currentDialogue = currentDialogue,
                notes = notes,
                loops = loops,
                dialogues = dialogues,
                currentSpeed = currentSpeed,
                speeds = speeds,
                onAddNote = onAddNote,
                onAddLoop = onAddLoop,
                onPlayLoop = onPlayLoop,
                onSpeedChange = onSpeedChange,
                modifier = Modifier.weight(1f)
            )
            AudioControls(
                isPlaying = isPlaying,
                currentTime = currentTime,
                totalTime = totalTime,
                progress = progress,
                currentSpeed = currentSpeed,
                activeTab = activeTab.value,
                activeLoopId = activeLoopId,
                onPlayPause = onPlayPause,
                onRewind = onRewind,
                onForward = onForward,
                onSeek = { },
                modifier = Modifier.fillMaxWidth()
            )
        }
        if (showCapsuleMenu) {
            Box(modifier = Modifier.fillMaxSize().background(androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.5f)).clickable { onDismissCapsule() })
            CapsuleMenu(
                activeTab = activeTab.value,
                audioMode = isAudioMode,
                onTabClick = { tabStr ->
                    val tab = ActiveTab.entries.find { it.value == tabStr } ?: ActiveTab.CLEAN
                    onTabClick(tab)
                },
                onAudioModeToggle = onAudioModeToggle,
                onTranslate = onTranslate,
                onDismiss = onDismissCapsule,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

@Composable
private fun PlayerHeader(title: String, onBack: () -> Unit, onMenuClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Mid)
        }
        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Dark,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = onMenuClick) {
            Icon(Icons.Default.MoreVert, contentDescription = "Menu", tint = Mid)
        }
    }
}

@Composable
private fun TabContent(
    activeTab: ActiveTab,
    isAudioMode: Boolean,
    isPlaying: Boolean,
    currentDialogue: Dialogue?,
    notes: List<Note>,
    loops: List<Loop>,
    dialogues: List<Dialogue>,
    currentSpeed: Float,
    speeds: List<Float>,
    onAddNote: (String) -> Unit,
    onAddLoop: (String, String, String, Int) -> Unit,
    onPlayLoop: (Loop) -> Unit,
    onSpeedChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        when (activeTab) {
            ActiveTab.CLEAN -> CleanTab(dialogue = currentDialogue, isAudioMode = isAudioMode, isPlaying = isPlaying)
            ActiveTab.SAVE -> DialogueTab(dialogues = dialogues, onDialogueClick = { })
            ActiveTab.SPEED -> SpeedTab(currentSpeed = currentSpeed, speeds = speeds, onSpeedChange = onSpeedChange)
            ActiveTab.LOOP -> LoopsTab(loops = loops, onAddLoop = onAddLoop, onPlayLoop = onPlayLoop)
            ActiveTab.NOTES -> NotesTab(notes = notes, onAddNote = onAddNote, onNoteClick = { })
        }
    }
}