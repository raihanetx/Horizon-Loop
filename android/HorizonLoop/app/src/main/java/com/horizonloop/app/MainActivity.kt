package com.horizonloop.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.horizonloop.app.data.FilterType
import com.horizonloop.app.ui.screens.HomeScreen
import com.horizonloop.app.ui.screens.PlayerScreen
import com.horizonloop.app.ui.screens.SettingsDialog
import com.horizonloop.app.ui.theme.Deep
import com.horizonloop.app.ui.theme.HorizonLoopTheme
import com.horizonloop.app.ui.viewmodel.AppViewModel
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HorizonLoopTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = Deep) {
                    val viewModel: AppViewModel = viewModel()
                    var showSettings by remember { mutableStateOf(false) }

                    LaunchedEffect(Unit) {
                        while (true) {
                            delay(100)
                            viewModel.updatePlayback()
                        }
                    }

                    if (viewModel.showHomeView) {
                        HomeScreen(
                            audioFiles = viewModel.filteredAudioFiles,
                            searchQuery = viewModel.searchQuery,
                            currentFilter = viewModel.currentFilter,
                            onSearchChange = { viewModel.searchQuery = it },
                            onFilterChange = { viewModel.currentFilter = it },
                            onAudioClick = { viewModel.openPlayer(it) },
                            onSettingsClick = { showSettings = true }
                        )
                    } else {
                        PlayerScreen(
                            context = this@MainActivity,
                            title = viewModel.currentAudioTitle,
                            activeTab = viewModel.activeTab,
                            isPlaying = viewModel.isPlaying,
                            isAudioMode = viewModel.audioMode,
                            isTranslating = viewModel.isTranslating,
                            translationProgress = viewModel.translationProgress,
                            currentTime = viewModel.formatTime(viewModel.currentPlaybackTime),
                            totalTime = viewModel.formatTime(viewModel.totalDuration),
                            progress = viewModel.getProgressPercent(),
                            currentSpeed = viewModel.speeds[viewModel.currentSpeedIndex],
                            speeds = viewModel.speeds,
                            activeLoopId = viewModel.activeLoopId,
                            notes = viewModel.notes,
                            loops = viewModel.loops,
                            dialogues = viewModel.translatedDialogues.ifEmpty { com.horizonloop.app.data.dialogues },
                            currentDialogue = viewModel.getCurrentDialogue(),
                            showCapsuleMenu = viewModel.showCapsuleMenu,
                            selectedDialogueIds = viewModel.selectedDialogueIds,
                            onBack = { viewModel.goHome() },
                            onMenuClick = { viewModel.toggleShowCapsuleMenu() },
                            onTabClick = { viewModel.activeTab = it },
                            onAudioModeToggle = { viewModel.toggleAudioMode() },
                            onTranslate = { ctx -> viewModel.startTranslation(ctx) },
                            onPlayPause = { viewModel.togglePlay() },
                            onRewind = { viewModel.rewind() },
                            onForward = { viewModel.forward() },
                            onAddNote = { viewModel.addNote(it) },
                            onDeleteNote = { viewModel.deleteNote(it) },
                            onAddLoop = { name, start, end, count -> viewModel.addLoop(name, start, end, count) },
                            onDeleteLoop = { viewModel.deleteLoop(it) },
                            onPlayLoop = { viewModel.playLoop(it) },
                            onSpeedChange = { viewModel.setSpeed(it) },
                            onDialogueSelect = { viewModel.selectDialogue(it) },
                            onDismissCapsule = { viewModel.hideCapsuleMenu() }
                        )
                    }
                    if (showSettings) {
                        SettingsDialog(
                            apiKey = "",
                            selectedEngine = "gpt-4o-mini",
                            onApiKeyChange = { },
                            onEngineChange = { },
                            onDismiss = { showSettings = false }
                        )
                    }
                }
            }
        }
    }
}