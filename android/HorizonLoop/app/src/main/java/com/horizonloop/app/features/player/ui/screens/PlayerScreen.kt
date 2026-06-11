package com.horizonloop.app.features.player.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import android.content.Context
import com.horizonloop.app.core.domain.model.ActiveTab
import com.horizonloop.app.core.domain.model.Dialogue
import com.horizonloop.app.core.domain.model.Loop
import com.horizonloop.app.core.domain.model.Note
import com.horizonloop.app.core.domain.model.TranslationStep
import com.horizonloop.app.core.ui.theme.Deep
import com.horizonloop.app.features.player.ui.components.AudioControls
import com.horizonloop.app.features.player.ui.components.CapsuleMenu
import com.horizonloop.app.features.player.ui.components.CleanTab
import com.horizonloop.app.features.translation.ui.components.TranslationDebugPanel
import com.horizonloop.app.core.ui.common.DialogueTab
import com.horizonloop.app.core.ui.common.NotesTab
import com.horizonloop.app.core.ui.common.SpeedTab
import com.horizonloop.app.features.loops.ui.LoopsTab

@Composable
fun PlayerScreen(
    context: Context,
    title: String,
    activeTab: ActiveTab,
    isPlaying: Boolean,
    isAudioMode: Boolean,
    isTranslating: Boolean,
    translationProgress: String,
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
    selectedDialogueIds: Set<Int>,
    showTranslationDebug: Boolean,
    translationSteps: List<TranslationStep>,
    translationLog: List<String>,
    translationError: String?,
    onBack: () -> Unit,
    onMenuClick: () -> Unit,
    onTabClick: (ActiveTab) -> Unit,
    onAudioModeToggle: () -> Unit,
    onTranslate: (Context) -> Unit,
    onPlayPause: () -> Unit,
    onRewind: () -> Unit,
    onForward: () -> Unit,
    onSeek: (Float) -> Unit,
    onAddNote: (String) -> Unit,
    onDeleteNote: (Int) -> Unit,
    onAddLoop: (String, String, String, Int) -> Unit,
    onDeleteLoop: (Int) -> Unit,
    onPlayLoop: (Loop) -> Unit,
    onSpeedChange: (Int) -> Unit,
    onDialogueSelect: (Context, Dialogue) -> Unit,
    onDismissCapsule: () -> Unit,
    onDismissTranslationDebug: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize().background(Deep)) {
        Column(modifier = Modifier.fillMaxSize()) {
            PlayerHeader(title = title, onBack = onBack, onMenuClick = onMenuClick)
            TabContent(
                context = context,
                activeTab = activeTab,
                isAudioMode = isAudioMode,
                isPlaying = isPlaying,
                isTranslating = isTranslating,
                translationProgress = translationProgress,
                currentDialogue = currentDialogue,
                notes = notes,
                loops = loops,
                dialogues = dialogues,
                currentSpeed = currentSpeed,
                speeds = speeds,
                selectedDialogueIds = selectedDialogueIds,
                onAddNote = onAddNote,
                onDeleteNote = onDeleteNote,
                onAddLoop = onAddLoop,
                onDeleteLoop = onDeleteLoop,
                onPlayLoop = onPlayLoop,
                onSpeedChange = onSpeedChange,
                onDialogueSelect = onDialogueSelect,
                modifier = Modifier.weight(1f)
            )
            // Audio controls are always visible on CLEAN, SAVE, and SPEED tabs
            // (only hidden on NOTES and LOOP tabs where they don't apply).
            val showAudioControls = activeTab != ActiveTab.NOTES && activeTab != ActiveTab.LOOP
            if (showAudioControls) {
                AudioControls(
                    title = title,
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
                    onSeek = onSeek,
                    modifier = Modifier.fillMaxWidth()
                )
            }
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
                context = context,
                hasTranslation = dialogues.isNotEmpty(),
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
        TranslationDebugPanel(
            steps = translationSteps,
            log = translationLog,
            isVisible = showTranslationDebug,
            onDismiss = onDismissTranslationDebug,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}