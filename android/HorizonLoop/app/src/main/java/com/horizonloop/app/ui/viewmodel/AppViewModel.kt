package com.horizonloop.app.ui.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.horizonloop.app.data.*
import kotlinx.coroutines.launch

/**
 * AppViewModel.kt - Main ViewModel for the app
 */

class AppViewModel : ViewModel() {
    var audioFiles by mutableStateOf<List<Audio>>(emptyList())
    var filteredAudioFiles by mutableStateOf<List<Audio>>(emptyList())
    var currentAudioTitle by mutableStateOf("")
    var currentAudioFilePath by mutableStateOf("")
    var searchQuery by mutableStateOf("")
    var currentFilter by mutableStateOf(FilterType.ALL)
    var showHomeView by mutableStateOf(true)
    var showCapsuleMenu by mutableStateOf(false)
    var showSettingsDialog by mutableStateOf(false)
    var showTranslationDebug by mutableStateOf(false)
    var isPlaying by mutableStateOf(false)
    var audioMode by mutableStateOf(false)
    var isTranslating by mutableStateOf(false)
    var isPlaybackEnded by mutableStateOf(false)
    var currentPlaybackTime by mutableStateOf(0.0)
    var totalDuration by mutableStateOf(0.0)
    var currentSpeedIndex by mutableStateOf(2)
    var activeTab by mutableStateOf(ActiveTab.CLEAN)
    var activeLoopId by mutableStateOf<Int?>(null)
    var previewEndTime by mutableStateOf<Double?>(null)
    var notes by mutableStateOf<List<Note>>(emptyList())
    var loops by mutableStateOf<List<Loop>>(emptyList())
    var translatedDialogues by mutableStateOf<List<Dialogue>>(emptyList())
    var translationProgress by mutableStateOf("")
    var translationLog by mutableStateOf<List<String>>(emptyList())
    var translationSteps by mutableStateOf<List<TranslationStep>>(emptyList())
    var selectedDialogueIds by mutableStateOf<Set<Int>>(emptySet())
    var currentDialogue by mutableStateOf<Dialogue?>(null)

    val speeds = listOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f)
    val currentSpeed get() = speeds[currentSpeedIndex]
    val audio: Audio? get() = audioFiles.find { it.title == currentAudioTitle }

    // Context stored for playback
    private var playbackContext: Context? = null

    fun loadAudioFiles(context: Context) = viewModelScope.launch {
        playbackContext = context
        translationProgress = "Scanning for audio files..."
        val files = VideoScanner_Main.scanVideos(context)
        audioFiles = files
        filterAudioFiles()
        translationProgress = ""
    }

    fun filterAudioFiles() {
        var result = audioFiles
        if (searchQuery.isNotBlank()) result = result.filter { it.title.contains(searchQuery, ignoreCase = true) }
        filteredAudioFiles = when (currentFilter) {
            FilterType.SIZE_DESC -> result.sortedByDescending { it.size.toLongOrNull() ?: 0 }
            FilterType.SIZE_ASC -> result.sortedBy { it.size.toLongOrNull() ?: 0 }
            FilterType.SUBTITLE_YES -> result.filter { it.subtitle }
            FilterType.SUBTITLE_NO -> result.filter { !it.subtitle }
            FilterType.PINNED -> result.filter { it.pin }
            else -> result
        }
    }

    fun selectAudio(audio: Audio) {
        currentAudioTitle = audio.title
        currentAudioFilePath = audio.filePath
        showHomeView = false
        playbackContext?.let { ctx ->
            MediaPlaybackManager.prepare(ctx, audio.filePath,
                onProgress = { pos, dur -> currentPlaybackTime = pos; totalDuration = dur },
                onStateChanged = { playing -> isPlaying = playing },
                onPlaybackError = { }
            )
        }
        isPlaying = true
    }

    fun togglePlayPause() { if (MediaPlaybackManager.isPlaying) MediaPlaybackManager.pause() else MediaPlaybackManager.start(); isPlaying = MediaPlaybackManager.isPlaying }
    fun rewind() = MediaPlaybackManager.seekBackward(10)
    fun forward() = MediaPlaybackManager.seekForward(10)
    fun seekTo(position: Double) { MediaPlaybackManager.seekTo((position * 1000).toInt()); currentPlaybackTime = position }
    fun setSpeed(index: Int) { currentSpeedIndex = index; MediaPlaybackManager.setSpeed(speeds[index]) }

    fun updatePlaybackState() {
        if (MediaPlaybackManager.isPlaying) {
            val pos = MediaPlaybackManager.currentPosition
            val dur = MediaPlaybackManager.duration
            currentPlaybackTime = pos
            totalDuration = dur
            isPlaying = true
        }
    }

    fun formatTime(seconds: Double): String {
        val mins = (seconds / 60).toInt()
        val secs = (seconds % 60).toInt()
        return "%02d:%02d".format(mins, secs)
    }

    fun getProgressPercent(): Float {
        return if (totalDuration > 0) (currentPlaybackTime / totalDuration).toFloat() else 0f
    }

    fun addNote(text: String) {
        val id = (notes.maxOfOrNull { it.id } ?: 0) + 1
        val date = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault()).format(java.util.Date())
        notes = notes + Note(id = id, text = text, date = date)
    }
    fun deleteNote(id: Int) { notes = notes.filter { it.id != id } }

    fun addLoop(title: String, startTime: String, endTime: String, color: Int) {
        val id = (loops.maxOfOrNull { it.id } ?: 0) + 1
        loops = loops + Loop(id = id, name = title, start = startTime, end = endTime, count = color)
    }
    fun deleteLoop(id: Int) { loops = loops.filter { it.id != id } }
    fun playLoop(loop: Loop) { previewEndTime = loop.end.toDoubleOrNull(); seekTo(loop.start.toDoubleOrNull() ?: 0.0); isPlaying = true }

    fun translateAudio(context: Context) = viewModelScope.launch {
        isTranslating = true
        translationSteps = listOf(TranslationStep(1, "Starting", StepStatus.IN_PROGRESS))
        translationLog = emptyList()
        AppViewModel_Translation.executeTranslation(context, currentAudioFilePath,
            onProgress = { translationProgress = it },
            onLog = { translationLog = translationLog + it },
            onComplete = { d -> translatedDialogues = d; isTranslating = false },
            onError = { err -> translationSteps = translationSteps + TranslationStep(99, "Error: $err", StepStatus.FAILED) })
    }

    fun toggleAudioMode() { audioMode = !audioMode }
    fun selectDialogue(dialogue: Dialogue) { selectedDialogueIds = selectedDialogueIds + dialogue.id; currentDialogue = dialogue }
    fun clearDialogueSelection() { selectedDialogueIds = emptySet(); currentDialogue = null }
    fun goHome() { showHomeView = true }
    fun toggleShowCapsuleMenu() { showCapsuleMenu = !showCapsuleMenu }
    fun hideCapsuleMenu() { showCapsuleMenu = false }
}
