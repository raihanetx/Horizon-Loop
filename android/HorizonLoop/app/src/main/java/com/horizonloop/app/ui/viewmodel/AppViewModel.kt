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
import java.text.SimpleDateFormat
import java.util.Date

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
    
    var scannedAudioFiles by mutableStateOf<List<Audio>>(emptyList())
    var isScanning by mutableStateOf(false)
    var scanError by mutableStateOf<String?>(null)
    
    val speeds = listOf(0.5f, 0.75f, 1f, 1.25f, 1.5f, 2f)
    var currentSpeedIndex by mutableStateOf(2)
    
    var showTranslationDebug by mutableStateOf(false)
    var translationSteps by mutableStateOf<List<TranslationStep>>(emptyList())
    var translationLog by mutableStateOf<List<String>>(emptyList())

    private fun logTranslation(message: String) {
        val timestamp = SimpleDateFormat("HH:mm:ss.SSS").format(Date())
        val logEntry = "[$timestamp] $message"
        translationLog = translationLog + logEntry
        android.util.Log.d("TranslationDebug", logEntry)
    }

    private fun addStep(step: TranslationStep) {
        translationSteps = translationSteps + step
    }

    private fun updateLastStep(status: StepStatus, detail: String? = null) {
        if (translationSteps.isNotEmpty()) {
            val lastStep = translationSteps.last()
            translationSteps = translationSteps.dropLast(1) + lastStep.copy(status = status, detail = detail)
        }
    }

    val filteredAudioFiles: List<Audio>
        get() {
            var result = scannedAudioFiles
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
    
    fun scanDeviceMedia(context: Context) {
        viewModelScope.launch {
            isScanning = true
            scanError = null
            try {
                val files = VideoScanner.scanAllMedia(context)
                scannedAudioFiles = files
            } catch (e: Exception) {
                android.util.Log.e("AppViewModel", "Error scanning media: ${e.message}")
                scanError = "Failed to scan: ${e.message}"
            }
            isScanning = false
        }
    }
    
    fun hasMediaFiles(): Boolean = scannedAudioFiles.isNotEmpty()

    fun toggleShowCapsuleMenu() { showCapsuleMenu = !showCapsuleMenu }
    fun hideCapsuleMenu() { showCapsuleMenu = false }

    fun openPlayer(audio: Audio, context: Context) {
        showHomeView = false
        currentAudioTitle = audio.title
        currentAudioFilePath = if (audio.contentUri.isNotBlank()) audio.contentUri else audio.filePath
        totalDuration = audio.durationSec
        currentPlaybackTime = 0.0
        activeTab = ActiveTab.CLEAN
        translatedDialogues = emptyList()
        translationSteps = emptyList()
        translationLog = emptyList()
        
        MediaPlaybackManager.prepare(
            context = context,
            filePath = currentAudioFilePath,
            onProgress = { pos, dur ->
                currentPlaybackTime = pos
                totalDuration = dur
            },
            onStateChanged = { playing ->
                isPlaying = playing
            },
            onPlaybackError = { error ->
                android.util.Log.e("AppViewModel", "Playback error: $error")
            }
        )
    }

    fun goHome() {
        showHomeView = true
        MediaPlaybackManager.release()
        isPlaying = false
        previewEndTime = null
        if (audioMode) {
            audioMode = false
        }
    }

    fun togglePlay() {
        MediaPlaybackManager.togglePlayPause()
        isPlaying = MediaPlaybackManager.isPlaying
    }

    fun rewind() {
        MediaPlaybackManager.rewind(5.0)
        currentPlaybackTime = MediaPlaybackManager.currentPosition
    }

    fun forward() {
        MediaPlaybackManager.forward(5.0)
        currentPlaybackTime = MediaPlaybackManager.currentPosition
    }

    fun seekTo(percent: Float) {
        val seconds = (percent * totalDuration).coerceIn(0.0, totalDuration)
        MediaPlaybackManager.seekTo(seconds)
        currentPlaybackTime = seconds
    }

    fun cycleSpeed() {
        currentSpeedIndex = (currentSpeedIndex + 1) % speeds.size
        setSpeed(currentSpeedIndex)
    }

    fun setSpeed(index: Int) {
        currentSpeedIndex = index.coerceIn(0, speeds.size - 1)
        MediaPlaybackManager.setSpeed(speeds[currentSpeedIndex])
    }

    fun toggleAudioMode() {
        audioMode = !audioMode
        MediaPlaybackManager.setAudioMode(audioMode)
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
            MediaPlaybackManager.seekTo(startSec)
            MediaPlaybackManager.play()
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

    fun startTranslation(context: Context) {
        val sttModel = ApiKeyStorage.getSttEngine(context)
        val llmModel = ApiKeyStorage.getLlmEngine(context)

        translationSteps = listOf(
            TranslationStep(1, "Checking permissions", StepStatus.PENDING, icon = "🔐"),
            TranslationStep(2, "Reading media file", StepStatus.PENDING, icon = "📁"),
            TranslationStep(3, "Extracting audio track", StepStatus.PENDING, icon = "🎵"),
            TranslationStep(4, "Sending to Whisper API", StepStatus.PENDING, icon = "🎤"),
            TranslationStep(5, "Receiving transcript", StepStatus.PENDING, icon = "📝"),
            TranslationStep(6, "Sending to LLM for translation", StepStatus.PENDING, icon = "🧠"),
            TranslationStep(7, "Creating subtitle entries", StepStatus.PENDING, icon = "✅")
        )
        translationLog = emptyList()
        showTranslationDebug = true
        
        if (currentAudioFilePath.isBlank()) {
            translationProgress = "Error: No media file selected"
            return
        }
        
        viewModelScope.launch {
            isTranslating = true
            translationProgress = "Starting..."
            
            try {
                updateLastStep(StepStatus.IN_PROGRESS, "Checking API key...")
                logTranslation("STEP 1: Checking API key availability")
                
                val apiKey = ApiKeyStorage.getApiKey(context)
                if (apiKey.isBlank()) {
                    logTranslation("ERROR: No API key found in Settings")
                    updateLastStep(StepStatus.FAILED, "No API key in Settings")
                    translationProgress = "Error: No API key in settings"
                    isTranslating = false
                    return@launch
                }
                logTranslation("✓ API key found: ${apiKey.take(10)}...")
                updateLastStep(StepStatus.COMPLETED, "API key found")
                
                val authHeader = "Bearer $apiKey"
                
                addStep(TranslationStep(8, "Reading media metadata", StepStatus.IN_PROGRESS, icon = "📋"))
                logTranslation("STEP 2: Reading media file information")
                logTranslation("File path/URI: $currentAudioFilePath")
                updateLastStep(StepStatus.COMPLETED, "File: $currentAudioTitle")
                
                updateLastStep(StepStatus.IN_PROGRESS, "Extracting audio from video...")
                logTranslation("STEP 3: Starting audio extraction")
                
                translationProgress = "Extracting audio..."
                val cacheDir = context.cacheDir
                val audioPath = AudioExtractor.extractAudio(
                    context = context,
                    videoPath = currentAudioFilePath,
                    outputDir = cacheDir,
                    onProgress = { progress ->
                        val pct = "${(progress * 100).toInt()}%"
                        translationProgress = "Extracting audio... $pct"
                        updateLastStep(StepStatus.IN_PROGRESS, "Extracting... $pct")
                    }
                )
                
                if (audioPath == null) {
                    logTranslation("ERROR: Audio extraction failed")
                    updateLastStep(StepStatus.FAILED, "Failed to extract audio")
                    translationProgress = "Error: Failed to extract audio"
                    isTranslating = false
                    return@launch
                }
                logTranslation("✓ Audio extracted to: $audioPath")
                updateLastStep(StepStatus.COMPLETED, "Audio saved to: ${File(audioPath).name}")
                
                addStep(TranslationStep(9, "Preparing audio for upload", StepStatus.IN_PROGRESS, icon = "📦"))
                logTranslation("STEP 4: Preparing audio for API upload")
                val audioChunks = AudioExtractor.splitAudio(audioPath, cacheDir)
                logTranslation("Audio chunks to process: ${audioChunks.size}")
                updateLastStep(StepStatus.COMPLETED, "${audioChunks.size} chunk(s)")
                
                updateLastStep(StepStatus.IN_PROGRESS, "Sending audio to Whisper API...")
                logTranslation("STEP 5: Sending to Groq Whisper API")
                logTranslation("Model: $sttModel")
                
                val fullTranscript = StringBuilder()
                
                for ((index, chunkPath) in audioChunks.withIndex()) {
                    if (audioChunks.size > 1) {
                        logTranslation("Processing chunk ${index + 1}/${audioChunks.size}")
                    }
                    
                    val chunkFile = File(chunkPath)
                    logTranslation("Uploading: ${chunkFile.name} (${chunkFile.length() / 1024} KB)")
                    
                    val mimeType = if (chunkPath.endsWith(".m4a")) "audio/m4a" else "audio/mpeg"
                    val requestFile = chunkFile.asRequestBody(mimeType.toMediaTypeOrNull())
                    val body = MultipartBody.Part.createFormData("file", chunkFile.name, requestFile)
                    val modelBody = sttModel.toRequestBody("text/plain".toMediaTypeOrNull())
                    
                    val response = GroqClient.apiService.transcribeAudio(authHeader, body, modelBody)
                    
                    if (response.isSuccessful) {
                        val text = response.body()?.text ?: ""
                        logTranslation("✓ Whisper response: ${text.take(50)}...")
                        fullTranscript.append(text).append(" ")
                    } else {
                        logTranslation("ERROR: Whisper API error: ${response.code()}")
                    }
                    
                    chunkFile.delete()
                }
                
                val transcript = fullTranscript.toString().trim()
                if (transcript.isBlank()) {
                    logTranslation("ERROR: No transcript received")
                    updateLastStep(StepStatus.FAILED, "Empty transcript")
                    translationProgress = "Error: No transcript generated"
                    isTranslating = false
                    return@launch
                }
                logTranslation("✓ Total transcript: ${transcript.length} chars")
                updateLastStep(StepStatus.COMPLETED, "${transcript.length} chars")
                
                updateLastStep(StepStatus.IN_PROGRESS, "Processing transcript...")
                logTranslation("STEP 6: Processing transcript from Whisper")
                updateLastStep(StepStatus.COMPLETED, transcript.take(50) + "...")
                
                updateLastStep(StepStatus.IN_PROGRESS, "Sending to LLM for Bangla translation...")
                logTranslation("STEP 7: Sending to Groq LLM for translation")
                logTranslation("Model: $llmModel")
                
                val chatRequest = ChatRequest(
                    model = llmModel,
                    messages = listOf(
                        ChatMessage("system", "You are a translator. Translate the following English text to Bangla (Bengali). Only output the translation, nothing else."),
                        ChatMessage("user", transcript)
                    )
                )
                
                val translateResponse = GroqClient.apiService.translateText(authHeader, chatRequest)
                
                var banglaTranslation = ""
                if (translateResponse.isSuccessful) {
                    banglaTranslation = translateResponse.body()?.choices?.firstOrNull()?.message?.content?.trim() ?: ""
                    logTranslation("✓ LLM translation received")
                } else {
                    logTranslation("ERROR: LLM API error: ${translateResponse.code()}")
                }
                updateLastStep(StepStatus.COMPLETED, banglaTranslation.take(50) + "...")
                
                File(audioPath).delete()
                logTranslation("Cleaned up temp audio file")
                
                updateLastStep(StepStatus.IN_PROGRESS, "Creating subtitle entries...")
                logTranslation("STEP 8: Creating dialogue entries")
                val newDialogues = createDialoguesFromTranscript(transcript, banglaTranslation)
                logTranslation("Created ${newDialogues.size} entries")
                
                translatedDialogues = newDialogues
                updateLastStep(StepStatus.COMPLETED, "${newDialogues.size} entries created")
                
                translationProgress = "Done! ${newDialogues.size} subtitles created"
                logTranslation("✓ TRANSLATION COMPLETE")
                
                kotlinx.coroutines.delay(5000)
                showTranslationDebug = false
                
            } catch (e: Exception) {
                logTranslation("FATAL ERROR: ${e.message}")
                translationProgress = "Error: ${e.message}"
            }
            
            isTranslating = false
        }
    }
    
    private fun createDialoguesFromTranscript(english: String, bangla: String): List<Dialogue> {
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

    fun selectDialogue(dialogue: Dialogue) {
        selectedDialogueIds = if (dialogue.id in selectedDialogueIds) {
            selectedDialogueIds - dialogue.id
        } else {
            selectedDialogueIds + dialogue.id
        }
    }

    fun getCurrentDialogue(): Dialogue? {
        if (translatedDialogues.isEmpty()) return null
        val activeList = translatedDialogues
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
        if (MediaPlaybackManager.isPlaying) {
            currentPlaybackTime = MediaPlaybackManager.currentPosition
            totalDuration = MediaPlaybackManager.duration
        }
    }
}
