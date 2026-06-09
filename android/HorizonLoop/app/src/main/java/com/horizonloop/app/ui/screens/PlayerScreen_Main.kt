package com.horizonloop.app.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.horizonloop.app.data.ActiveTab
import com.horizonloop.app.data.Dialogue
import com.horizonloop.app.data.Loop
import com.horizonloop.app.data.Note
import com.horizonloop.app.data.TranslationStep
import com.horizonloop.app.ui.components.*
import com.horizonloop.app.ui.theme.*

/**
 * PlayerScreen_Main.kt - Main player screen composable
 */

@Composable
fun PlayerScreen(
    context: Context, title: String, activeTab: ActiveTab, isPlaying: Boolean, isAudioMode: Boolean,
    isTranslating: Boolean, isPlaybackEnded: Boolean, translationProgress: String,
    currentTime: String, totalTime: String, progress: Float, currentSpeed: Float, speeds: List<Float>,
    activeLoopId: Int?, notes: List<Note>, loops: List<Loop>, dialogues: List<Dialogue>,
    currentDialogue: Dialogue?, showCapsuleMenu: Boolean, selectedDialogueIds: Set<Int>,
    showTranslationDebug: Boolean, steps: List<TranslationStep>, log: List<String>,
    onBack: () -> Unit, onMenuClick: () -> Unit, onTabClick: (ActiveTab) -> Unit,
    onAudioModeToggle: () -> Unit, onTranslate: (Context) -> Unit, onPlayPause: () -> Unit,
    onRewind: () -> Unit, onForward: () -> Unit, onAddNote: (String) -> Unit, onDeleteNote: (Int) -> Unit,
    onAddLoop: (String, String, String, Int) -> Unit, onDeleteLoop: (Int) -> Unit, onPlayLoop: (Loop) -> Unit,
    onSpeedChange: (Int) -> Unit, onDialogueSelect: (Dialogue) -> Unit, onDismissCapsule: () -> Unit,
    onDismissTranslationDebug: () -> Unit, modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize().background(Deep)) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.fillMaxWidth().background(Muted).padding(horizontal = 4.dp, vertical = 4.dp), contentAlignment = Alignment.Center) {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back", tint = Mid) }
                    Text(title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Dark, maxLines = 1,
                        overflow = TextOverflow.Ellipsis, textAlign = TextAlign.Center, modifier = Modifier.weight(1f))
                    IconButton(onClick = onMenuClick) { Icon(Icons.Default.MoreVert, "Menu", tint = Mid) }
                }
            }
            TabContent(activeTab, isAudioMode, isPlaying, isTranslating, isPlaybackEnded, translationProgress,
                currentDialogue, notes, loops, dialogues, currentSpeed, speeds, selectedDialogueIds,
                onAddNote, onDeleteNote, onAddLoop, onDeleteLoop, onPlayLoop, onSpeedChange, onDialogueSelect)
            if (activeTab != ActiveTab.NOTES && activeTab != ActiveTab.LOOP) {
                AudioControls(title, isPlaying, currentTime, totalTime, progress, currentSpeed, activeTab.value,
                    activeLoopId, false, onPlayPause, onRewind, onForward, { }, Modifier.fillMaxWidth())
            }
        }
        if (showCapsuleMenu) {
            Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)).clickable { onDismissCapsule() })
            CapsuleMenu(activeTab.value, isAudioMode,
                { tabStr -> onTabClick(ActiveTab.entries.find { it.value == tabStr } ?: ActiveTab.CLEAN) },
                onAudioModeToggle, onTranslate, onDismissCapsule, context, Modifier.align(Alignment.BottomCenter))
        }
        TranslationDebugPanel(context, steps, log, showTranslationDebug, onDismissTranslationDebug, Modifier.align(Alignment.BottomCenter))
    }
}
