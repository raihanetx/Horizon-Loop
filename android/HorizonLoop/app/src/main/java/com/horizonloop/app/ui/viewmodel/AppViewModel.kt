package com.horizonloop.app.ui.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.horizonloop.app.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

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
    var currentAudioFilePath by mutableStateOf("")
    var searchQuery by mutableStateOf("")
    var currentFilter by mutableStateOf<FilterType>(FilterType.ALL)
    var showHomeView by mutableStateOf(true)
    var showCapsuleMenu by mutableStateOf(false)
    var translatedDialogues by mutableStateOf<List<Dialogue>>(emptyList())
    var isTranslating by mutableStateOf(false)
    var translationProgress by mutableStateOf("")
    var selectedDialogueIds by mutableStateOf<Set<Int>>(emptySet())

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
        currentAudioFilePath = audio.filePath
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

    /**
     * Start the full translation workflow:
     * 1. Extract audio from video file
     * 2. Send to Whisper for transcription
     * 3. Send to LLM for translation to Bangla
     */
    fun startTranslation(context: Context) {
        if (currentAudioFilePath.isBlank()) {
            // No file path - use mock data for demo
            useMockTranslation()
            return
        }
        
        viewModelScope.launch {
            isTranslating = true
            translationProgress = "Starting..."
            
            try {
                // Step 1: Get API key
                val apiKey = ApiKeyStorage.getApiKey(context)
                if (apiKey.isBlank()) {
                    translationProgress = "Error: No API key in settings"
                    isTranslating = false
                    return@launch
                }
                
                val authHeader = "Bearer $apiKey"
                
                // Step 2: Extract audio
                translationProgress = "Extracting audio..."
                val cacheDir = context.cacheDir
                val audioPath = AudioExtractor.extractAudio(
                    context = context,
                    videoPath = currentAudioFilePath,
                    outputDir = cacheDir,
                    onProgress = { progress ->
                        translationProgress = "Extracting audio... ${(progress * 100).toInt()}%"
                    }
                )
                
                if (audioPath == null) {
                    translationProgress = "Error: Failed to extract audio"
                    isTranslating = false
                    return@launch
                }
                
                // Step 3: Split if needed (25MB limit)
                translationProgress = "Preparing audio..."
                val audioChunks = AudioExtractor.splitAudio(audioPath, cacheDir)
                
                // Step 4: Transcribe with Whisper
                translationProgress = "Transcribing with Whisper..."
                val fullTranscript = StringBuilder()
                
                for ((index, chunkPath) in audioChunks.withIndex()) {
                    if (audioChunks.size > 1) {
                        translationProgress = "Transcribing chunk ${index + 1}/${audioChunks.size}..."
                    }
                    
                    val chunkFile = File(chunkPath)
                    val mimeType = if (chunkPath.endsWith(".m4a")) "audio/mp4" else "audio/mpeg"
                    val requestFile = chunkFile.asRequestBody(mimeType.toMediaTypeOrNull())
                    val body = MultipartBody.Part.createFormData("file", chunkFile.name, requestFile)
                    val modelBody = "whisper-1".toRequestBody("text/plain".toMediaTypeOrNull())
                    
                    val response = GroqClient.apiService.transcribeAudio(authHeader, body, modelBody)
                    
                    if (response.isSuccessful) {
                        val text = response.body()?.text ?: ""
                        fullTranscript.append(text).append(" ")
                    } else {
                        android.util.Log.e("Translation", "Whisper error: ${response.code()} - ${response.message()}")
                    }
                    
                    // Clean up chunk file
                    chunkFile.delete()
                }
                
                val transcript = fullTranscript.toString().trim()
                if (transcript.isBlank()) {
                    translationProgress = "Error: No transcript generated"
                    isTranslating = false
                    return@launch
                }
                
                // Step 5: Translate with LLM
                translationProgress = "Translating to Bangla..."
                val chatRequest = ChatRequest(
                    model = "llama-3.3-70b-versatile",
                    messages = listOf(
                        ChatMessage("system", "You are a translator. Translate the following English text to Bangla (Bengali). Only output the translation, nothing else."),
                        ChatMessage("user", transcript)
                    )
                )
                
                val translateResponse = GroqClient.apiService.translateText(authHeader, chatRequest)
                
                var banglaTranslation = ""
                if (translateResponse.isSuccessful) {
                    banglaTranslation = translateResponse.body()?.choices?.firstOrNull()?.message?.content?.trim() ?: ""
                } else {
                    android.util.Log.e("Translation", "LLM error: ${translateResponse.code()} - ${translateResponse.message()}")
                }
                
                // Clean up extracted audio
                File(audioPath).delete()
                
                // Step 6: Create dialogues from transcript
                translationProgress = "Creating subtitles..."
                val newDialogues = createDialoguesFromTranscript(transcript, banglaTranslation)
                translatedDialogues = newDialogues
                
                translationProgress = "Done!"
                
            } catch (e: Exception) {
                android.util.Log.e("Translation", "Error: ${e.message}")
                translationProgress = "Error: ${e.message}"
            }
            
            isTranslating = false
        }
    }
    
    /**
     * Create dialogue entries from transcript by splitting into sentences
     */
    private fun createDialoguesFromTranscript(english: String, bangla: String): List<Dialogue> {
        // Split text by whitespace and filter for sentences with punctuation
        val englishParts = english.split(" ").filter { it.isNotBlank() }
        val englishSentences = mutableListOf<String>()
        var currentSentence = StringBuilder()
        
        for (word in englishParts) {
            currentSentence.append(word).append(" ")
            if (word.endsWith(".") || word.endsWith("?") || word.endsWith("!")) {
                englishSentences.add(currentSentence.toString().trim())
                currentSentence = StringBuilder()
            }
        }
        if (currentSentence.isNotBlank()) {
            englishSentences.add(currentSentence.toString().trim())
        }
        
        // Split bangla similarly
        val banglaParts = bangla.split(" ").filter { it.isNotBlank() }
        val banglaSentences = mutableListOf<String>()
        var currentBangla = StringBuilder()
        
        for (word in banglaParts) {
            currentBangla.append(word).append(" ")
            if (word.endsWith(".") || word.endsWith("?") || word.endsWith("!") || word.contains("।")) {
                banglaSentences.add(currentBangla.toString().trim())
                currentBangla = StringBuilder()
            }
        }
        if (currentBangla.isNotBlank()) {
            banglaSentences.add(currentBangla.toString().trim())
        }
        
        // Create dialogues with timestamps
        var timeOffset = 0.0
        return englishSentences.mapIndexed { index, eng ->
            val time = formatTime(timeOffset)
            val bang = if (index < banglaSentences.size) banglaSentences[index] else ""
            timeOffset += 3.0
            Dialogue(
                id = index + 1,
                time = time,
                english = eng,
                bangla = bang
            )
        }
    }
    
    /**
     * Use mock translation for demo purposes (when no file path available)
     */
    private fun useMockTranslation() {
        viewModelScope.launch {
            isTranslating = true
            translationProgress = "Processing..."
            
            // Simulate processing time
            kotlinx.coroutines.delay(2000)
            
            translatedDialogues = dialogues.take(10)
            translationProgress = "Done!"
            isTranslating = false
        }
    }

    fun selectDialogue(dialogue: Dialogue) {
        selectedDialogueIds = if (dialogue.id in selectedDialogueIds) {
            selectedDialogueIds - dialogue.id
        } else {
            selectedDialogueIds + dialogue.id
        }
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