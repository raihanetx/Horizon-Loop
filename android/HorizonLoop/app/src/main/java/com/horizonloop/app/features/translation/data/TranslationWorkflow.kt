package com.horizonloop.app.features.translation.data

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.horizonloop.app.core.data.GroqClient
import com.horizonloop.app.core.data.VerboseWhisperResponse
import com.horizonloop.app.core.data.WhisperWord
import com.horizonloop.app.core.data.createDialoguesFromTranscript
import com.horizonloop.app.core.domain.model.TranslationStep
import com.horizonloop.app.core.domain.model.StepStatus
import com.horizonloop.app.core.domain.model.Dialogue
import com.horizonloop.app.features.media.data.AudioExtractor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

internal class TranslationWorkflow(
    private val context: Context,
    private val audioFilePath: String,
    private val apiKey: String,
    private val transcriptionModel: String,
    private val translationModel: String,
    private val onStepUpdate: (List<TranslationStep>) -> Unit,
    private val onLogUpdate: (List<String>) -> Unit,
    private val onProgressUpdate: (String) -> Unit,
    private val onErrorUpdate: (String?) -> Unit,
    private val onDialoguesUpdate: (List<Dialogue>) -> Unit,
    private val onTranslatingUpdate: (Boolean) -> Unit,
    private val onShowDebugToggle: (Boolean) -> Unit,
    private val onProgress: (Float) -> Unit = {}
) {
    private val authHeader get() = "Bearer $apiKey"
    private val fullTranscript = StringBuilder()
    private val allWords = mutableListOf<WhisperWord>()
    private var chunkOffsetSeconds = 0.0
    private var banglaResult = ""

    fun run(viewModelScope: kotlinx.coroutines.CoroutineScope) {
        viewModelScope.launch(Dispatchers.IO) {
            onTranslatingUpdate(true)
            onProgressUpdate("Starting...")
            try {
                initDebugState()
                if (!checkApiKey()) return@launch
                if (!extractAudio()) return@launch
                transcribeAudio()
            } catch (e: Exception) {
                onLogUpdate(listOf("FATAL ERROR: ${e.message}"))
                onErrorUpdate("Translation failed: ${e.message}")
            }
            onTranslatingUpdate(false)
        }
    }

    private fun baseSteps(vararg updates: Pair<Int, Triple<StepStatus?, String?, String?>>) = listOf(
        TranslationStep(1, "Checking permissions", StepStatus.PENDING, icon = "🔐"),
        TranslationStep(2, "Reading media file", StepStatus.PENDING, icon = "📁"),
        TranslationStep(3, "Extracting audio track", StepStatus.PENDING, icon = "🎵"),
        TranslationStep(4, "Sending to Whisper API", StepStatus.PENDING, icon = "🎤"),
        TranslationStep(5, "Receiving transcript", StepStatus.PENDING, icon = "📝"),
        TranslationStep(6, "Sending to LLM for translation", StepStatus.PENDING, icon = "🧠"),
        TranslationStep(7, "Creating subtitle entries", StepStatus.PENDING, icon = "✅")
    ).mapIndexed { i, step -> updates.find { it.first == i + 1 }?.second?.let { (s, d, ic) ->
        step.copy(status = s ?: step.status, detail = d ?: step.detail, icon = ic ?: step.icon)
    } ?: step }

    private fun initDebugState() {
        onStepUpdate(baseSteps())
        onLogUpdate(emptyList())
        onShowDebugToggle(true)
    }

    private suspend fun checkApiKey(): Boolean {
        onStepUpdate(baseSteps(1 to Triple(StepStatus.IN_PROGRESS, null, null)))
        onLogUpdate(listOf("STEP 1: Checking API key availability"))
        if (apiKey.isBlank()) {
            onLogUpdate(listOf("ERROR: No API key found in Settings"))
            onStepUpdate(baseSteps(1 to Triple(StepStatus.FAILED, "No API key in Settings", null)))
            onProgressUpdate("Error: No API key in settings"); onErrorUpdate("No API key in Settings."); onTranslatingUpdate(false); return false
        }
        onLogUpdate(listOf("✓ API key found: ${apiKey.take(10)}..."))
        onStepUpdate(baseSteps(1 to Triple(StepStatus.COMPLETED, "API key found", null))); return true
    }

    private suspend fun extractAudio(): Boolean {
        onStepUpdate(baseSteps(1 to Triple(StepStatus.COMPLETED, null, null), 3 to Triple(StepStatus.IN_PROGRESS, null, null)))
        onLogUpdate(listOf("STEP 3: Starting audio extraction")); onProgressUpdate("Extracting audio...")
        val audioPath = AudioExtractor.extractAudio(context, audioFilePath, context.cacheDir, onProgress = { onProgress(it.toFloat()) })
        if (audioPath == null) {
            onLogUpdate(listOf("ERROR: Audio extraction failed - returned null"))
            onStepUpdate(baseSteps(3 to Triple(StepStatus.FAILED, "Failed to extract audio", null)))
            onProgressUpdate("Error: Failed to extract audio"); onErrorUpdate("Failed to extract audio from video."); onTranslatingUpdate(false); return false
        }
        onLogUpdate(listOf("✓ Audio extracted successfully to: $audioPath"))
        onStepUpdate(baseSteps(3 to Triple(StepStatus.COMPLETED, "Audio saved to: ${File(audioPath).name}", null))); return true
    }

    private suspend fun transcribeAudio() {
        val audioPath = AudioExtractor.extractAudio(context, audioFilePath, context.cacheDir, onProgress = { onProgress(it.toFloat()) }) ?: return
        val audioChunks = AudioExtractor.splitAudio(audioPath, context.cacheDir)
        onStepUpdate(baseSteps(1 to Triple(StepStatus.COMPLETED, null, null), 3 to Triple(StepStatus.COMPLETED, null, null), 4 to Triple(StepStatus.IN_PROGRESS, "${audioChunks.size} chunk(s)", null)))
        onLogUpdate(listOf("STEP 4: Preparing audio for API upload", "Audio chunks to process: ${audioChunks.size}"))
        sendToWhisper(audioChunks)
        val transcript = fullTranscript.toString().trim()
        if (transcript.isBlank()) {
            onLogUpdate(listOf("ERROR: No transcript text received from Whisper"))
            onStepUpdate(baseSteps(5 to Triple(StepStatus.FAILED, "Empty transcript", null)))
            onProgressUpdate("Error: No transcript generated"); onErrorUpdate("No transcript generated."); onTranslatingUpdate(false); return
        }
        onLogUpdate(listOf("✓ Total transcript length: ${transcript.length} characters"))
        onStepUpdate(baseSteps(4 to Triple(StepStatus.COMPLETED, null, null), 5 to Triple(StepStatus.IN_PROGRESS, "${transcript.length} chars extracted", null)))
        sendToLLM(transcript)
        createSubtitles()
    }

    private suspend fun sendToWhisper(audioChunks: List<String>) {
        onLogUpdate(listOf("STEP 5: Sending to Groq Whisper API", "Endpoint: https://api.groq.com/openai/v1/audio/transcriptions", "Model: $transcriptionModel", "Format: verbose_json | Timestamp granularity: word | Temperature: 0"))
        for ((index, chunkPath) in audioChunks.withIndex()) {
            if (audioChunks.size > 1) onLogUpdate(listOf("Processing chunk ${index + 1}/${audioChunks.size}"))
            val chunkFile = File(chunkPath)
            onLogUpdate(listOf("Uploading: ${chunkFile.name} (${chunkFile.length() / 1024} KB)"))
            val mimeType = if (chunkPath.endsWith(".m4a")) "audio/mp4" else "audio/mpeg"

            val response = GroqClient.apiService.transcribeAudio(
                authHeader,
                MultipartBody.Part.createFormData("file", chunkFile.name, chunkFile.asRequestBody(mimeType.toMediaTypeOrNull())),
                transcriptionModel.toRequestBody("text/plain".toMediaTypeOrNull()),
                "verbose_json".toRequestBody("text/plain".toMediaTypeOrNull()),
                "word".toRequestBody("text/plain".toMediaTypeOrNull()),
                "0".toRequestBody("text/plain".toMediaTypeOrNull())
            )

            if (response.isSuccessful) {
                val body = response.body()
                fullTranscript.append(body?.text ?: "").append(" ")
                val words = body?.words
                if (words != null) {
                    val shiftedWords = words.map { it.copy(start = it.start + chunkOffsetSeconds, end = it.end + chunkOffsetSeconds) }
                    allWords.addAll(shiftedWords)
                    onLogUpdate(listOf("✓ Whisper response: ${words.size} words with timestamps"))
                    // Track chunk offset for timestamp continuity: use last word's end time
                    val actualEnd = words.lastOrNull()?.end ?: 0.0
                    if (actualEnd > 0) chunkOffsetSeconds = actualEnd
                } else {
                    onLogUpdate(listOf("✓ Whisper response received (no word timestamps)"))
                }
            } else {
                onLogUpdate(listOf("ERROR: Whisper API error: ${response.code()} - ${response.message()}"))
            }
            chunkFile.delete()
        }
        audioChunks.firstOrNull()?.let { File(it).takeIf { f -> f.exists() }?.delete() }
        onLogUpdate(listOf("✓ Transcript + ${allWords.size} word-timestamps collected"))
    }

    private suspend fun sendToLLM(transcript: String) {
        onStepUpdate(baseSteps(5 to Triple(StepStatus.COMPLETED, transcript.take(50) + "...", null), 6 to Triple(StepStatus.IN_PROGRESS, "Processing transcript...", null)))
        onLogUpdate(listOf("STEP 6: Transcript received", "Preview: \"${transcript.take(100)}...\""))
        onLogUpdate(listOf("STEP 7: Sending to Groq LLM", "Model: $translationModel"))
        val request = com.horizonloop.app.core.data.ChatRequest(model = translationModel, messages = listOf(com.horizonloop.app.core.data.ChatMessage("system", "You are a translator. Translate the following English text to Bangla (Bengali). Only output the translation, nothing else."), com.horizonloop.app.core.data.ChatMessage("user", transcript)))
        val response = GroqClient.apiService.translateText(authHeader, request)
        banglaResult = if (response.isSuccessful) response.body()?.choices?.firstOrNull()?.message?.content?.trim() ?: "" else ""
        onLogUpdate(listOf(if (banglaResult.isNotBlank()) "✓ LLM: ${banglaResult.take(50)}..." else "ERROR: LLM API error ${response.code()}"))
        onStepUpdate(baseSteps(6 to Triple(StepStatus.COMPLETED, banglaResult.take(50) + "...", null)))
    }

    private suspend fun createSubtitles() {
        onStepUpdate(baseSteps(6 to Triple(StepStatus.COMPLETED, null, null), 7 to Triple(StepStatus.IN_PROGRESS, null, null)))
        onLogUpdate(listOf("STEP 8: Creating dialogue entries from transcript"))
        val dialogues = createDialoguesFromTranscript(fullTranscript.toString().trim(), banglaResult, allWords.toList())
        onDialoguesUpdate(dialogues); onErrorUpdate(null)
        onStepUpdate(baseSteps(7 to Triple(StepStatus.COMPLETED, "${dialogues.size} entries created", null)))
        onProgressUpdate("Done! ${dialogues.size} subtitles created")
        onLogUpdate(listOf("✓ TRANSLATION COMPLETE - ${dialogues.size} subtitles ready"))
        delay(5000); onShowDebugToggle(false)
    }
}