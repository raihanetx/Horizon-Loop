package com.horizonloop.app.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.horizonloop.app.data.ActiveTab
import com.horizonloop.app.data.Dialogue
import com.horizonloop.app.data.Loop
import com.horizonloop.app.data.Note
import com.horizonloop.app.ui.components.*

/**
 * PlayerScreen_TabContent.kt
 * Tab content composable for player screen
 */

@Composable
fun TabContent(
    activeTab: ActiveTab, isAudioMode: Boolean, isPlaying: Boolean, isTranslating: Boolean,
    isPlaybackEnded: Boolean, translationProgress: String, currentDialogue: Dialogue?,
    notes: List<Note>, loops: List<Loop>, dialogues: List<Dialogue>, currentSpeed: Float, speeds: List<Float>,
    selectedDialogueIds: Set<Int>, onAddNote: (String) -> Unit, onDeleteNote: (Int) -> Unit,
    onAddLoop: (String, String, String, Int) -> Unit, onDeleteLoop: (Int) -> Unit, onPlayLoop: (Loop) -> Unit,
    onSpeedChange: (Int) -> Unit, onDialogueSelect: (Dialogue) -> Unit, modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        when (activeTab) {
            ActiveTab.CLEAN -> CleanTab(isAudioMode, isPlaying, isPlaybackEnded, dialogues, selectedDialogueIds, onDialogueSelect)
            ActiveTab.NOTES -> NotesTab(notes, onAddNote, onDeleteNote, Modifier.fillMaxSize())
            ActiveTab.LOOP -> LoopsTab(loops, onAddLoop, onDeleteLoop, onPlayLoop, Modifier.fillMaxSize())
            ActiveTab.SPEED -> SpeedTab(currentSpeed, speeds, onSpeedChange, Modifier.fillMaxSize())
            ActiveTab.NOTES -> DialogueTab(dialogues, null, selectedDialogueIds, onDialogueSelect, Modifier.fillMaxSize())
            else -> CleanTab(isAudioMode, isPlaying, isPlaybackEnded, dialogues, selectedDialogueIds, onDialogueSelect)
        }
    }
}
