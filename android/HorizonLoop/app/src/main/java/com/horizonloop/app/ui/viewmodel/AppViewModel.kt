package com.horizonloop.app.ui.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.horizonloop.app.data.*
import kotlinx.coroutines.launch

private fun sortAudioFiles(files: List<Audio>, filter: FilterType): List<Audio> {
    var result = files
    if (result.isEmpty()) return result
    when (filter) {
        FilterType.SIZE_DESC -> result = result.sortedByDescending { it.size.replace(" MB", "").toFloat() }
        FilterType.SIZE_ASC -> result = result.sortedBy { it.size.replace(" MB", "").toFloat() }
        FilterType.SUBTITLE_YES -> result = result.filter { it.subtitle }
        FilterType.SUBTITLE_NO -> result = result.filter { !it.subtitle }
        FilterType.PINNED -> result = result.filter { it.pin }
        FilterType.ALL -> {}
    }
    return result
}

private fun buildAudioPlayer(ctx: Context, audio: Audio, onProg: (Double, Double) -> Unit, onState: (Boolean) -> Unit, onDone: () -> Unit, onErr: (String) -> Unit): AudioPlayer {
    val path = if (audio.contentUri.isNotBlank()) audio.contentUri else audio.filePath
    val player = AudioPlayer(ctx)
    player.onProgressUpdate = { ms, tot -> onProg(ms / 1000.0, tot / 1000.0) }
    player.onPlaybackStateChanged = onState
    player.onCompletion = onDone
    player.onError = onErr
    player.load(path)
    return player
}

class AppViewModel : ViewModel() {
    var activeTab by mutableStateOf(ActiveTab.CLEAN)
    var notes by mutableStateOf<List<Note>>(emptyList())
    var loops by mutableStateOf<List<Loop>>(emptyList())
    var isPlaying by mutableStateOf(false)
    var currentPlaybackTime by mutableStateOf(0.0)
    var totalDuration by mutableStateOf(0.0)
    private var audioPlayer: AudioPlayer? = null
    var activeLoopId by mutableStateOf<Int?>(null)
    var audioMode by mutableStateOf(false)
    var previewEndTime by mutableStateOf<Double?>(null)
    var currentAudioTitle by mutableStateOf("")
    var currentAudioFilePath by mutableStateOf("")
    var searchQuery by mutableStateOf("")
    var currentFilter by mutableStateOf<FilterType>(FilterType.ALL)
    var showHomeView by mutableStateOf(true)
    var showCapsuleMenu by mutableStateOf(false)
    var translatedDialogues by mutableStateOf<List<Dialogue>>(emptyList())
    var translationError by mutableStateOf<String?>(null)
    var isTranslating by mutableStateOf(false)
    var translationProgress by mutableStateOf("")
    var selectedDialogueIds by mutableStateOf<Set<Int>>(emptySet())
    var scannedAudioFiles by mutableStateOf<List<Audio>>(emptyList())
    var isScanning by mutableStateOf(false)
    var scanError by mutableStateOf<String?>(null)
    val speeds = listOf(0.5f, 0.75f, 1f, 1.25f, 1.5f, 2f)
    var currentSpeedIndex by mutableStateOf(2)
    var showTranslationDebug by mutableStateOf(false)
    var translationSteps by mutableStateOf<List<TranslationStep>>(emptyList())
    var translationLog by mutableStateOf<List<String>>(emptyList())

    val filteredAudioFiles: List<Audio>
        get() {
            val base = if (searchQuery.isNotBlank()) scannedAudioFiles.filter { it.title.contains(searchQuery, ignoreCase = true) } else scannedAudioFiles
            return sortAudioFiles(base, currentFilter)
        }

    fun scanDeviceMedia(context: Context) {
        viewModelScope.launch {
            isScanning = true; scanError = null
            try { scannedAudioFiles = VideoScanner.scanAllMedia(context) }
            catch (e: Exception) { scanError = "Failed to scan: ${e.message}" }
            isScanning = false
        }
    }

    fun hasMediaFiles(): Boolean = scannedAudioFiles.isNotEmpty()
    fun toggleShowCapsuleMenu() { showCapsuleMenu = !showCapsuleMenu }
    fun hideCapsuleMenu() { showCapsuleMenu = false }

    fun openPlayer(context: Context, audio: Audio) {
        showHomeView = false
        currentAudioTitle = audio.title
        currentAudioFilePath = if (audio.contentUri.isNotBlank()) audio.contentUri else audio.filePath
        audioPlayer?.release()
        audioPlayer = buildAudioPlayer(context, audio,
            onProg = { cur, tot -> currentPlaybackTime = cur; totalDuration = tot },
            onState = { playing -> isPlaying = playing },
            onDone = { currentPlaybackTime = 0.0; isPlaying = false },
            onErr = { err -> android.util.Log.e("AppViewModel", "AudioPlayer error: $err") }
        )
        activeTab = ActiveTab.CLEAN
        translatedDialogues = emptyList(); translationSteps = emptyList(); translationLog = emptyList()
    }

    fun goHome() {
        showHomeView = true; isPlaying = false; previewEndTime = null
        audioPlayer?.pause()
        if (audioMode) audioMode = false
    }

    fun togglePlay() { audioPlayer?.togglePlayPause() }
    fun rewind() { audioPlayer?.rewind() }
    fun forward() { audioPlayer?.forward() }
    fun seekTo(percent: Float) { audioPlayer?.seekToPercent(percent) }
    fun cycleSpeed() { currentSpeedIndex = (currentSpeedIndex + 1) % speeds.size }
    fun setSpeed(index: Int) { currentSpeedIndex = index.coerceIn(0, speeds.size - 1); audioPlayer?.setSpeed(speeds[currentSpeedIndex]) }
    fun toggleAudioMode() { audioMode = !audioMode }

    fun addNote(text: String) {
        notes = listOf(Note(id = System.currentTimeMillis().toInt(), text = text,
            date = java.text.SimpleDateFormat("d/M/yyyy", java.util.Locale.getDefault()).format(java.util.Date()))) + notes
    }

    fun deleteNote(id: Int) { notes = notes.filter { it.id != id } }

    fun addLoop(name: String, start: String, end: String, count: Int) {
        loops = listOf(Loop(id = System.currentTimeMillis().toInt(), name = name, start = start, end = end, count = count)) + loops
    }

    fun deleteLoop(id: Int) { if (activeLoopId == id) activeLoopId = null; loops = loops.filter { it.id != id } }

    fun playLoop(context: Context, loop: Loop) {
        val s = parseTimeToSeconds(loop.start); val e = parseTimeToSeconds(loop.end)
        if (!s.isNaN() && !e.isNaN() && s < e) {
            activeLoopId = loop.id; previewEndTime = e
            audioPlayer?.seekTo((s * 1000).toLong()); audioPlayer?.play(); activeTab = ActiveTab.CLEAN
        }
    }

    fun startTranslation(context: Context) {
        translationError = null; translatedDialogues = emptyList()
        if (currentAudioFilePath.isBlank()) { translationError = "No media file selected"; translationProgress = "Error: No media file"; showTranslationDebug = true; return }
        val apiKey = ApiKeyStorage.getApiKey(context)
        TranslationWorkflow(
            context = context, audioFilePath = currentAudioFilePath, apiKey = apiKey,
            onStepUpdate = { translationSteps = it }, onLogUpdate = { translationLog = it },
            onProgressUpdate = { translationProgress = it }, onErrorUpdate = { translationError = it },
            onDialoguesUpdate = { translatedDialogues = it }, onTranslatingUpdate = { isTranslating = it },
            onShowDebugToggle = { showTranslationDebug = it }
        ).run(viewModelScope)
    }

    fun selectDialogue(dialogue: Dialogue) { selectedDialogueIds = if (dialogue.id in selectedDialogueIds) selectedDialogueIds - dialogue.id else selectedDialogueIds + dialogue.id }

    fun getCurrentDialogue(): Dialogue? {
        val list = translatedDialogues
        if (list.isEmpty()) return null
        val firstSec = parseTimeToSeconds(list[0].time)
        if (currentPlaybackTime < firstSec) return list[0]
        var current: Dialogue? = null
        for (d in list) { val s = parseTimeToSeconds(d.time); if (s <= currentPlaybackTime) current = d else break }
        return current
    }

    fun getProgressPercent(): Float = if (totalDuration > 0) (currentPlaybackTime / totalDuration).toFloat() else 0f

    override fun onCleared() { super.onCleared(); audioPlayer?.destroy() }
}