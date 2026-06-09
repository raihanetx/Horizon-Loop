package com.horizonloop.app.ui.viewmodel

import android.content.Context
import com.horizonloop.app.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

/**
 * AppViewModel_Translation.kt
 * Translation logic extracted from AppViewModel
 */

object AppViewModel_Translation {
    private val client = OkHttpClient.Builder().connectTimeout(60, TimeUnit.SECONDS).readTimeout(120, TimeUnit.SECONDS).writeTimeout(60, TimeUnit.SECONDS).build()

    suspend fun executeTranslation(
        context: Context, audioPath: String,
        onProgress: (String) -> Unit, onLog: (String) -> Unit, onComplete: (List<Dialogue>) -> Unit, onError: (String) -> Unit
    ) = withContext(Dispatchers.IO) {
        try {
            onProgress("Extracting audio..."); onLog("Starting audio extraction")
            val outputDir = File(context.cacheDir, "audio"); outputDir.mkdirs()
            val wavPath = AudioExtractor_Main.extractAudio(context, audioPath, outputDir) { p -> onProgress("Extracting: ${(p * 100).toInt()}%") }
            if (wavPath == null) { onError("Failed to extract audio"); return@withContext }
            onLog("Audio extracted to: $wavPath")
            
            val apiKey = ApiKeyStorage.getApiKey(context)
            if (apiKey.isBlank()) { onError("Please set Groq API key in settings"); return@withContext }
            
            onProgress("Transcribing..."); onLog("Calling Whisper API")
            val wavFile = File(wavPath)
            val requestBody = wavFile.asRequestBody("audio/wav".toMediaTypeOrNull())
            val filePart = MultipartBody.Part.createFormData("file", wavFile.name, requestBody)
            val modelPart = "whisper-1".toRequestBody("text/plain".toMediaTypeOrNull())
            
            val authHeader = "Bearer $apiKey"
            val request = Request.Builder().url("https://api.groq.com/openai/v1/audio/transcriptions")
                .addHeader("Authorization", authHeader).post(requestBody).build()
            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""
            
            val textPattern = Pattern.compile("\"text\":\"([^\"]+)\"")
            val textMatcher = textPattern.matcher(responseBody)
            val transcript = if (textMatcher.find()) textMatcher.group(1) ?: "" else ""
            
            if (transcript.isBlank()) { onError("Empty transcript"); return@withContext }
            onLog("Transcription complete: ${transcript.take(50)}...")
            
            val dialogues = transcript.split("\n").filter { it.isNotBlank() }.mapIndexed { index, text ->
                Dialogue(id = index + 1, time = "${index * 3}:00", english = text, bangla = "")
            }
            onComplete(dialogues)
        } catch (e: Exception) { onError(e.message ?: "Unknown error") }
    }
}
