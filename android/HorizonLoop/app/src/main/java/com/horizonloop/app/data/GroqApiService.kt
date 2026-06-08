package com.horizonloop.app.data

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

// Request/Response models for Whisper API
data class WhisperResponse(
    val text: String
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

interface GroqApiService {
    
    @Multipart
    @POST("https://api.groq.com/openai/v1/audio/transcriptions")
    suspend fun transcribeAudio(
        @Header("Authorization") authorization: String,
        @Part file: MultipartBody.Part,
        @Part("model") model: RequestBody
    ): Response<WhisperResponse>
    
    @POST("https://api.groq.com/openai/v1/chat/completions")
    suspend fun translateText(
        @Header("Authorization") authorization: String,
        @Body request: ChatRequest
    ): Response<ChatResponse>
}