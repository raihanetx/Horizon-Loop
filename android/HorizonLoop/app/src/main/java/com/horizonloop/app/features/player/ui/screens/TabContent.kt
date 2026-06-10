package com.horizonloop.app.features.player.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.horizonloop.app.core.domain.model.ActiveTab
import com.horizonloop.app.core.domain.model.Dialogue
import com.horizonloop.app.core.domain.model.Loop
import com.horizonloop.app.core.domain.model.Note
import com.horizonloop.app.features.player.ui.components.CleanTab
import com.horizonloop.app.core.ui.common.DialogueTab
import com.horizonloop.app.core.ui.common.SpeedTab
import com.horizonloop.app.features.loops.ui.LoopsTab
import com.horizonloop.app.core.ui.common.NotesTab

@Composable
fun TabContent(
    context: Context,
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
    onDialogueSelect: (android.content.Context, Dialogue) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        when (activeTab) {
            ActiveTab.CLEAN -> CleanTab(
                dialogue = currentDialogue,
                isAudioMode = isAudioMode,
                isPlaying = isPlaying,
                isTranslating = isTranslating,
                translationProgress = translationProgress
            )
            ActiveTab.SAVE -> DialogueTab(
                context = context,
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