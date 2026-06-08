package com.horizonloop.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
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
    
    private val requiredPermissions: Array<String>
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(
                Manifest.permission.READ_MEDIA_VIDEO,
                Manifest.permission.READ_MEDIA_AUDIO
            )
        } else {
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        permissionResult = permissions
    }
    
    // Use state to communicate permission result to composables
    private var permissionResult by mutableStateOf<Map<String, Boolean>?>(null)
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HorizonLoopTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = Deep) {
                    val viewModel: AppViewModel = viewModel()
                    var showSettings by remember { mutableStateOf(false) }

                    // Check permissions on first composition
                    LaunchedEffect(Unit) {
                        val missingPermissions = requiredPermissions.filter {
                            ContextCompat.checkSelfPermission(this@MainActivity, it) != PackageManager.PERMISSION_GRANTED
                        }
                        if (missingPermissions.isEmpty()) {
                            // All permissions granted, scan immediately
                            viewModel.scanDeviceMedia(this@MainActivity)
                        } else {
                            // Request permissions
                            permissionLauncher.launch(missingPermissions.toTypedArray())
                        }
                    }
                    
                    // Handle permission result
                    LaunchedEffect(permissionResult) {
                        permissionResult?.let { results ->
                            val allGranted = results.values.all { it }
                            if (allGranted) {
                                viewModel.scanDeviceMedia(this@MainActivity)
                            } else {
                                viewModel.scanError = "Permission denied. Grant access in Settings to scan media."
                            }
                        }
                    }
                    
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
                            onSettingsClick = { showSettings = true },
                            isScanning = viewModel.isScanning,
                            scanError = viewModel.scanError
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
                            showTranslationDebug = viewModel.showTranslationDebug,
                            translationSteps = viewModel.translationSteps,
                            translationLog = viewModel.translationLog,
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
                            onDismissCapsule = { viewModel.hideCapsuleMenu() },
                            onDismissTranslationDebug = { viewModel.showTranslationDebug = false }
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