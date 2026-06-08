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
        "whisper-large-v3"
    )
    
    // Groq LLM model options for translation
    val LLM_MODELS = listOf(
        "openai/gpt-oss-120b"
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
        return getPrefs(context).getString(KEY_STT_ENGINE, "whisper-large-v3") ?: "whisper-large-v3"
    }
    
    fun saveLlmEngine(context: Context, engine: String) {
        getPrefs(context).edit().putString(KEY_LLM_ENGINE, engine).apply()
    }
    
    fun getLlmEngine(context: Context): String {
        return getPrefs(context).getString(KEY_LLM_ENGINE, "openai/gpt-oss-120b") ?: "openai/gpt-oss-120b"
    }
    
    fun hasApiKey(context: Context): Boolean {
        return getApiKey(context).isNotBlank()
    }
}
