package com.horizonloop.app

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.horizonloop.app.data.ApiKeyStorage
import com.horizonloop.app.ui.screens.HomeScreen
import com.horizonloop.app.ui.screens.PlayerScreen
import com.horizonloop.app.ui.screens.SettingsDialog
import kotlinx.coroutines.delay

/**
 * MainActivity_Content.kt
 * Main UI content and permission handling extracted from MainActivity
 */

@Composable
fun MainActivityContent(
    context: Context,
    viewModel: MainViewModel,
    showSettings: Boolean,
    onShowSettingsChange: (Boolean) -> Unit
) {
    var groqKey by remember { mutableStateOf("") }
    var whisperModel by remember { mutableStateOf("whisper-1") }

    LaunchedEffect(showSettings) {
        if (showSettings) {
            groqKey = ApiKeyStorage.getApiKey(context)
            whisperModel = ApiKeyStorage.getSttEngine(context)
        }
    }

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED ||
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_VIDEO) == PackageManager.PERMISSION_GRANTED) {
            viewModel.loadAudioFiles(context)
        }
    }

    LaunchedEffect(Unit) {
        while (true) { delay(100); viewModel.updatePlaybackState() }
    }

    if (viewModel.showHomeView) {
        HomeScreen(
            isLoading = false,
            audioFiles = viewModel.filteredAudioFiles,
            searchQuery = viewModel.searchQuery,
            onSearchChange = { viewModel.searchQuery = it },
            onAudioSelect = { viewModel.selectAudio(it) }
        )
    } else {
        PlayerScreen(
            context = context,
            title = viewModel.currentAudioTitle,
            activeTab = viewModel.activeTab,
            isPlaying = viewModel.isPlaying,
            isAudioMode = viewModel.audioMode,
            isTranslating = viewModel.isTranslating,
            isPlaybackEnded = viewModel.isPlaybackEnded,
            translationProgress = viewModel.translationProgress,
            currentTime = viewModel.formatTime(viewModel.currentPlaybackTime),
            totalTime = viewModel.formatTime(viewModel.totalDuration),
            progress = viewModel.getProgressPercent(),
            currentSpeed = viewModel.currentSpeed,
            speeds = viewModel.speeds,
            activeLoopId = viewModel.activeLoopId,
            notes = viewModel.notes,
            loops = viewModel.loops,
            dialogues = viewModel.translatedDialogues,
            currentDialogue = viewModel.currentDialogue,
            showCapsuleMenu = viewModel.showCapsuleMenu,
            selectedDialogueIds = viewModel.selectedDialogueIds,
            showTranslationDebug = viewModel.showTranslationDebug,
            steps = viewModel.translationSteps,
            log = viewModel.translationLog,
            onBack = { viewModel.goHome() },
            onMenuClick = { viewModel.toggleShowCapsuleMenu() },
            onTabClick = { viewModel.activeTab = it },
            onAudioModeToggle = { viewModel.toggleAudioMode() },
            onTranslate = { viewModel.translateAudio(it) },
            onPlayPause = { viewModel.togglePlayPause() },
            onRewind = { viewModel.rewind() },
            onForward = { viewModel.forward() },
            onAddNote = { viewModel.addNote(it) },
            onDeleteNote = { viewModel.deleteNote(it) },
            onAddLoop = { name, start, end, count -> viewModel.addLoop(name, start, end, count) },
            onDeleteLoop = { viewModel.deleteLoop(it) },
            onPlayLoop = { viewModel.playLoop(it) },
            onSpeedChange = { viewModel.setSpeed(it) },
            onDialogueSelect = { viewModel.selectDialogue(it) },
            onDismissCapsule = { viewModel.hideCapsuleMenu() },
            onDismissTranslationDebug = { viewModel.showTranslationDebug = false }
        )
    }

    if (showSettings) {
        SettingsDialog(
            onDismiss = { onShowSettingsChange(false) },
            onSave = { apiKey, model -> 
                ApiKeyStorage.saveApiKey(context, apiKey)
                ApiKeyStorage.saveSttEngine(context, model)
                onShowSettingsChange(false)
            }
        )
    }
}
