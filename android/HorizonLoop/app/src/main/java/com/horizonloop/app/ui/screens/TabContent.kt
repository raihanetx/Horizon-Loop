package com.horizonloop.app.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.horizonloop.app.data.ActiveTab
import com.horizonloop.app.data.Dialogue
import com.horizonloop.app.data.Loop
import com.horizonloop.app.data.Note
import com.horizonloop.app.ui.components.CleanTab
import com.horizonloop.app.ui.components.DialogueTab
import com.horizonloop.app.ui.components.LoopsTab
import com.horizonloop.app.ui.components.NotesTab
import com.horizonloop.app.ui.components.SpeedTab

@Composable
fun TabContent(
    activeTab: ActiveTab,
    isAudioMode: Boolean,
    isPlaying: Boolean,
    isTranslating: Boolean,
    translationProgress: String,
    currentDialogue: Dialogue?,
    notes: List<Note>,
    loops: List<Loop>,
    dialogues: List<Dialogue>,
    currentSpeed: Float,
    speeds: List<Float>,
    selectedDialogueIds: Set<Int>,
    onAddNote: (String) -> Unit,
    onDeleteNote: (Int) -> Unit,
    onAddLoop: (String, String, String, Int) -> Unit,
    onDeleteLoop: (Int) -> Unit,
    onPlayLoop: (Loop) -> Unit,
    onSpeedChange: (Int) -> Unit,
    onDialogueSelect: (Dialogue) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        when (activeTab) {
            ActiveTab.CLEAN -> CleanTab(
                dialogue = currentDialogue,
                isAudioMode = isAudioMode,
                isPlaying = isPlaying,
                isTranslating = isTranslating,
                translationProgress = translationProgress,
                onSpeedDecrease = { onSpeedChange((speeds.indexOf(currentSpeed) - 1).coerceAtLeast(0)) },
                onSpeedIncrease = { onSpeedChange((speeds.indexOf(currentSpeed) + 1).coerceAtMost(speeds.size - 1)) }
            )
            ActiveTab.SAVE -> DialogueTab(
                dialogues = dialogues,
                playingDialogueId = currentDialogue?.id,
                selectedDialogueIds = selectedDialogueIds,
                onDialogueClick = onDialogueSelect
            )
            ActiveTab.SPEED -> SpeedTab(currentSpeed = currentSpeed, speeds = speeds, onSpeedChange = onSpeedChange)
            ActiveTab.LOOP -> LoopsTab(loops = loops, onAddLoop = onAddLoop, onDeleteLoop = onDeleteLoop, onPlayLoop = onPlayLoop)
            ActiveTab.NOTES -> NotesTab(notes = notes, onAddNote = onAddNote, onDeleteNote = onDeleteNote)
        }
    }
}