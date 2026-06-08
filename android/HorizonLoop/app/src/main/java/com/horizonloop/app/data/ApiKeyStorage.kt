package com.horizonloop.app.data

import android.content.Context
import android.content.SharedPreferences

object ApiKeyStorage {
    
    private const val PREFS_NAME = "horizonloop_prefs"
    private const val KEY_API_KEY = "groq_api_key"
    private const val KEY_STT_ENGINE = "stt_engine"  // Speech-to-text model (Whisper)
    private const val KEY_LLM_ENGINE = "llm_engine"  // LLM model for translation
    
    // Groq Whisper/STT model options for audio transcription
    val STT_MODELS = listOf(
        "whisper-1"
    )
    
    // Groq LLM model options for translation
    val LLM_MODELS = listOf(
        "llama-3.3-70b-versatile",
        "llama-3.1-8b-instant",
        "mixtral-8x7b-32768",
        "gemma2-9b-it"
    )
    
    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
    
    fun saveApiKey(context: Context, apiKey: String) {
        getPrefs(context).edit().putString(KEY_API_KEY, apiKey).apply()
    }
    
    fun getApiKey(context: Context): String {
        return getPrefs(context).getString(KEY_API_KEY, "") ?: ""
    }
    
    fun saveSttEngine(context: Context, engine: String) {
        getPrefs(context).edit().putString(KEY_STT_ENGINE, engine).apply()
    }
    
    fun getSttEngine(context: Context): String {
        return getPrefs(context).getString(KEY_STT_ENGINE, "whisper-1") ?: "whisper-1"
    }
    
    fun saveLlmEngine(context: Context, engine: String) {
        getPrefs(context).edit().putString(KEY_LLM_ENGINE, engine).apply()
    }
    
    fun getLlmEngine(context: Context): String {
        return getPrefs(context).getString(KEY_LLM_ENGINE, "llama-3.3-70b-versatile") ?: "llama-3.3-70b-versatile"
    }
    
    fun hasApiKey(context: Context): Boolean {
        return getApiKey(context).isNotBlank()
    }
}
