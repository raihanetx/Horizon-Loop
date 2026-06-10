package com.horizonloop.app.core.data

import android.content.Context
import android.content.SharedPreferences

object ApiKeyStorage {

    private const val PREFS_NAME = "horizonloop_prefs"
    private const val KEY_API_KEY = "groq_api_key"
    private const val KEY_ENGINE = "translation_engine"
    private const val KEY_TRANSCRIPTION_MODEL = "transcription_model"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveApiKey(context: Context, apiKey: String) {
        getPrefs(context).edit().putString(KEY_API_KEY, apiKey).apply()
    }

    fun getApiKey(context: Context): String {
        return getPrefs(context).getString(KEY_API_KEY, "") ?: ""
    }

    fun saveEngine(context: Context, engine: String) {
        getPrefs(context).edit().putString(KEY_ENGINE, engine).apply()
    }

    fun getEngine(context: Context): String {
        return getPrefs(context).getString(KEY_ENGINE, "llama-3.3-70b-versatile") ?: "llama-3.3-70b-versatile"
    }

    fun saveTranscriptionModel(context: Context, model: String) {
        getPrefs(context).edit().putString(KEY_TRANSCRIPTION_MODEL, model).apply()
    }

    fun getTranscriptionModel(context: Context): String {
        return getPrefs(context).getString(KEY_TRANSCRIPTION_MODEL, "whisper-1") ?: "whisper-1"
    }

    fun hasApiKey(context: Context): Boolean {
        return getApiKey(context).isNotBlank()
    }
}