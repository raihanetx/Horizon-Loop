package com.horizonloop.app.data

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject

object DialogueStorage {
    
    private const val PREFS_NAME = "horizonloop_dialogues"
    private const val KEY_DIALOGUES = "translated_dialogues"
    
    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
    
    fun saveDialogues(context: Context, audioFilePath: String, dialogues: List<Dialogue>) {
        val jsonArray = JSONArray()
        dialogues.forEach { dialogue ->
            val obj = JSONObject().apply {
                put("id", dialogue.id)
                put("time", dialogue.time)
                put("english", dialogue.english)
                put("bangla", dialogue.bangla)
            }
            jsonArray.put(obj)
        }
        getPrefs(context).edit()
            .putString(audioFilePath, jsonArray.toString())
            .apply()
    }
    
    fun loadDialogues(context: Context, audioFilePath: String): List<Dialogue>? {
        val json = getPrefs(context).getString(audioFilePath, null) ?: return null
        return try {
            val jsonArray = JSONArray(json)
            val dialogues = mutableListOf<Dialogue>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                dialogues.add(Dialogue(
                    id = obj.getInt("id"),
                    time = obj.getString("time"),
                    english = obj.getString("english"),
                    bangla = obj.getString("bangla")
                ))
            }
            dialogues
        } catch (e: Exception) {
            null
        }
    }
    
    fun hasDialogues(context: Context, audioFilePath: String): Boolean {
        return getPrefs(context).contains(audioFilePath)
    }
}
