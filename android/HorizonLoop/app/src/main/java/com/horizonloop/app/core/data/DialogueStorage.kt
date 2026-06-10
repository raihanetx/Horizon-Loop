package com.horizonloop.app.core.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.horizonloop.app.core.domain.model.Dialogue

object DialogueStorage {
    private const val PREFS_NAME = "horizonloop_dialogues"
    private const val KEY_PREFIX = "dialogues_"
    private val gson = Gson()

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    private fun makeKey(audioFilePath: String): String = KEY_PREFIX + audioFilePath.hashCode()

    fun saveDialogues(context: Context, audioFilePath: String, dialogues: List<Dialogue>) {
        if (dialogues.isEmpty()) return
        val json = gson.toJson(dialogues)
        getPrefs(context).edit().putString(makeKey(audioFilePath), json).apply()
    }

    fun loadDialogues(context: Context, audioFilePath: String): List<Dialogue> {
        val json = getPrefs(context).getString(makeKey(audioFilePath), null) ?: return emptyList()
        val type = object : TypeToken<List<Dialogue>>() {}.type
        return try { gson.fromJson(json, type) } catch (e: Exception) { emptyList() }
    }

    fun clearDialogues(context: Context, audioFilePath: String) {
        getPrefs(context).edit().remove(makeKey(audioFilePath)).apply()
    }
}