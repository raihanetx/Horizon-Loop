package com.horizonloop.app.data

import com.google.gson.Gson
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

// Request/Response models for Whisper API
data class WhisperResponse(
    val text: String,
    val words: List<WhisperWord>? = null,  // Word-level timestamps when requested
    val segments: List<WhisperSegment>? = null  // Segment-level timestamps (dialogue boundaries!)
)

data class WhisperWord(
    val word: String,
    val start: Double,
    val end: Double
)

// Whisper segment with exact start/end times - these ARE the dialogue boundaries!
data class WhisperSegment(
    val id: Int,
    val start: Double,    // Exact start time in seconds
    val end: Double,      // Exact end time in seconds
    val text: String      // The text for this segment/dialogue
)

// Request/Response models for LLM Translation API
data class ChatMessage(
    val role: String,
    val content: String
)

data class ChatRequest(
    val model: String,
    val messages: List<ChatMessage>
)

data class ChatResponse(
    val choices: List<Choice>
)

data class Choice(
    val message: Message
)

data class Message(
    val content: String
)

// LLM translation response
data class LlmDialogue(
    val text: String,
    val bangla: String
)

interface GroqApiService {
    
    @Multipart
    @POST("https://api.groq.com/openai/v1/audio/transcriptions")
    suspend fun transcribeAudio(
        @Header("Authorization") authorization: String,
        @Part file: MultipartBody.Part,
        @Part("model") model: RequestBody,
        @Part("timestamp_granularities[]") timestampGranularities: RequestBody? = null,
        @Part("language") language: RequestBody? = null
    ): Response<WhisperResponse>
    
    @POST("https://api.groq.com/openai/v1/chat/completions")
    suspend fun translateText(
        @Header("Authorization") authorization: String,
        @Body request: ChatRequest
    ): Response<ChatResponse>
    
    @POST("https://api.groq.com/openai/v1/chat/completions")
    suspend fun chatCompletion(
        @Header("Authorization") authorization: String,
        @Body requestBody: RequestBody
    ): Response<ChatResponse>
}
