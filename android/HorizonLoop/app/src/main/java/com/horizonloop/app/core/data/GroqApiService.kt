package com.horizonloop.app.core.data

import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

data class WhisperResponse(
    val text: String
)

/** Word-level timestamp from verbose_json Whisper response */
data class WhisperWord(
    val word: String,
    val start: Double,
    val end: Double,
    val probability: Double
)

/** Verbose JSON response from Whisper API with word-level timestamps */
data class VerboseWhisperResponse(
    val text: String,
    val words: List<WhisperWord>?,
    val language: String?
)

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

data class GroqModel(
    @SerializedName("object") val objectType: String,
    val id: String,
    val created: Long,
    val owned_by: String
)

data class GroqModelsResponse(
    @SerializedName("object") val objectType: String,
    val data: List<GroqModel>
)

interface GroqApiService {

    @Multipart
    @POST("https://api.groq.com/openai/v1/audio/transcriptions")
    suspend fun transcribeAudio(
        @Header("Authorization") authorization: String,
        @Part file: MultipartBody.Part,
        @Part("model") model: RequestBody,
        @Part("response_format") responseFormat: RequestBody,
        @Part("timestamp_granularities[]") timestampGranularities: RequestBody,
        @Part("temperature") temperature: RequestBody
    ): Response<VerboseWhisperResponse>

    @POST("https://api.groq.com/openai/v1/chat/completions")
    suspend fun translateText(
        @Header("Authorization") authorization: String,
        @Body request: ChatRequest
    ): Response<ChatResponse>

    @GET("https://api.groq.com/openai/v1/models")
    suspend fun listModels(
        @Header("Authorization") authorization: String
    ): Response<GroqModelsResponse>
}