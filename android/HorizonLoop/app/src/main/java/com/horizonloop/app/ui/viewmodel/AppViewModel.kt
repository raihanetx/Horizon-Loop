package com.horizonloop.app.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.horizonloop.app.data.ActiveTab
import com.horizonloop.app.data.Audio
import com.horizonloop.app.data.Dialogue
import com.horizonloop.app.data.FilterType
import com.horizonloop.app.data.Loop
import com.horizonloop.app.data.Note
import com.horizonloop.app.data.audioFiles
import com.horizonloop.app.data.dialogues

class AppViewModel : ViewModel() {
    var activeTab by mutableStateOf(ActiveTab.CLEAN)
    var notes by mutableStateOf<List<Note>>(emptyList())
    var loops by mutableStateOf<List<Loop>>(emptyList())
    var isPlaying by mutableStateOf(false)
    var currentPlaybackTime by mutableStateOf(0.0)
    var totalDuration by mutableStateOf(0.0)
    var activeLoopId by mutableStateOf<Int?>(null)
    var audioMode by mutableStateOf(false)
    var previewEndTime by mutableStateOf<Double?>(null)
    var currentAudioTitle by mutableStateOf("")
    var searchQuery by mutableStateOf("")
    var currentFilter by mutableStateOf<FilterType>(FilterType.ALL)
    var showHomeView by mutableStateOf(true)
    var showCapsuleMenu by mutableStateOf(false)
    var translatedDialogues by mutableStateOf<List<Dialogue>>(emptyList())

    val speeds = listOf(0.5f, 0.75f, 1f, 1.25f, 1.5f, 2f)
    var currentSpeedIndex by mutableStateOf(2)

    val filteredAudioFiles: List<Audio>
        get() {
            var result = audioFiles
            if (searchQuery.isNotBlank()) {
                result = result.filter { it.title.contains(searchQuery, ignoreCase = true) }
            }
            when (currentFilter) {
                FilterType.SIZE_DESC -> result = result.sortedByDescending { it.size.replace(" MB", "").toFloat() }
                FilterType.SIZE_ASC -> result = result.sortedBy { it.size.replace(" MB", "").toFloat() }
                FilterType.SUBTITLE_YES -> result = result.filter { it.subtitle }
                FilterType.SUBTITLE_NO -> result = result.filter { !it.subtitle }
                FilterType.PINNED -> result = result.filter { it.pin }
                FilterType.ALL -> {}
            }
            return result
        }

    fun toggleShowCapsuleMenu() { showCapsuleMenu = !showCapsuleMenu }
    fun hideCapsuleMenu() { showCapsuleMenu = false }

    fun openPlayer(audio: Audio) {
        showHomeView = false
        currentAudioTitle = audio.title
        totalDuration = audio.durationSec
        currentPlaybackTime = 0.0
        activeTab = ActiveTab.CLEAN
    }

    fun goHome() {
        showHomeView = true
        isPlaying = false
        previewEndTime = null
        if (audioMode) {
            audioMode = false
        }
    }

    fun togglePlay() {
        isPlaying = !isPlaying
    }

    fun rewind() {
        currentPlaybackTime = (currentPlaybackTime - 5).coerceAtLeast(0.0)
    }

    fun forward() {
        currentPlaybackTime = (currentPlaybackTime + 5).coerceAtMost(totalDuration)
    }

    fun seekTo(percent: Float) {
        currentPlaybackTime = (percent * totalDuration).coerceIn(0.0, totalDuration)
    }

    fun cycleSpeed() {
        currentSpeedIndex = (currentSpeedIndex + 1) % speeds.size
    }

    fun setSpeed(index: Int) {
        currentSpeedIndex = index.coerceIn(0, speeds.size - 1)
    }

    fun toggleAudioMode() {
        audioMode = !audioMode
    }

    fun addNote(text: String) {
        val note = Note(
            id = System.currentTimeMillis().toInt(),
            text = text,
            date = java.text.SimpleDateFormat("d/M/yyyy", java.util.Locale.getDefault()).format(java.util.Date())
        )
        notes = listOf(note) + notes
    }

    fun deleteNote(id: Int) {
        notes = notes.filter { it.id != id }
    }

    fun addLoop(name: String, start: String, end: String, count: Int) {
        val loop = Loop(
            id = System.currentTimeMillis().toInt(),
            name = name,
            start = start,
            end = end,
            count = count
        )
        loops = listOf(loop) + loops
    }

    fun deleteLoop(id: Int) {
        if (activeLoopId == id) activeLoopId = null
        loops = loops.filter { it.id != id }
    }

    fun playLoop(loop: Loop) {
        val startSec = parseTimeToSeconds(loop.start)
        val endSec = parseTimeToSeconds(loop.end)
        if (!startSec.isNaN() && !endSec.isNaN() && startSec < endSec) {
            activeLoopId = loop.id
            previewEndTime = endSec
            currentPlaybackTime = startSec
            isPlaying = true
            activeTab = ActiveTab.CLEAN
        }
    }

    private fun parseTimeToSeconds(timeStr: String): Double {
        return try {
            val parts = timeStr.split(":")
            if (parts.size == 2) {
                parts[0].toDouble() * 60 + parts[1].toDouble()
            } else {
                timeStr.toDoubleOrNull() ?: Double.NaN
            }
        } catch (e: Exception) { Double.NaN }
    }

    fun startTranslation() {
        translatedDialogues = dialogues.take(10)
    }

    fun getCurrentDialogue(): Dialogue? {
        val activeList = if (translatedDialogues.isNotEmpty()) translatedDialogues else dialogues
        if (activeList.isEmpty()) return null
        val firstSec = parseTimeToSeconds(activeList[0].time)
        if (currentPlaybackTime < firstSec) return activeList[0]
        var current: Dialogue? = null
        for (d in activeList) {
            val dSec = parseTimeToSeconds(d.time)
            if (dSec <= currentPlaybackTime) current = d
            else break
        }
        return current
    }

    fun getProgressPercent(): Float {
        return if (totalDuration > 0) (currentPlaybackTime / totalDuration).toFloat() else 0f
    }

    fun formatTime(secs: Double): String {
        val m = (secs / 60).toInt()
        val s = (secs % 60).toInt()
        return "$m:${if (s < 10) "0" else ""}$s"
    }

    fun updatePlayback() {
        if (isPlaying) {
            currentPlaybackTime += 0.1
            val previewEnd = previewEndTime
            if (previewEnd != null && currentPlaybackTime >= previewEnd) {
                isPlaying = false
                previewEndTime = null
            }
            if (currentPlaybackTime > totalDuration) {
                currentPlaybackTime = 0.0
            }
        }
    }
}