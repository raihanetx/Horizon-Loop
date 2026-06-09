package com.horizonloop.app.ui.viewmodel

import com.horizonloop.app.data.Loop
import com.horizonloop.app.data.Note
import com.horizonloop.app.data.TranslationStep
import com.horizonloop.app.data.StepStatus
import com.horizonloop.app.data.Dialogue
import java.text.SimpleDateFormat
import java.util.Date

/**
 * AppViewModel_Helpers.kt
 * Helper functions extracted from AppViewModel
 */

object AppViewModel_Helpers {
    fun parseTimeToSeconds(timeStr: String): Double {
        return try {
            val clean = timeStr.replace(",", ".")
            val parts = clean.split(":")
            when (parts.size) {
                3 -> parts[0].toDouble() * 3600 + parts[1].toDouble() * 60 + parts[2].toDouble()
                2 -> parts[0].toDouble() * 60 + parts[1].toDouble()
                1 -> parts[0].toDouble()
                else -> Double.NaN
            }
        } catch (e: Exception) { Double.NaN }
    }

    fun formatTimestamp(seconds: Double): String {
        val m = (seconds / 60).toInt()
        val s = (seconds % 60).toInt()
        return "$m:${if (s < 10) "0" else ""}$s"
    }

    fun formatTime(secs: Double): String {
        val m = (secs / 60).toInt()
        val s = (secs % 60).toInt()
        return "$m:${if (s < 10) "0" else ""}$s"
    }

    fun createTranslationSteps(): List<TranslationStep> = listOf(
        TranslationStep(1, "Checking permissions", StepStatus.PENDING, icon = "🔐"),
        TranslationStep(2, "Reading media file", StepStatus.PENDING, icon = "📁"),
        TranslationStep(3, "Extracting audio track", StepStatus.PENDING, icon = "🎵"),
        TranslationStep(4, "Sending to Whisper API", StepStatus.PENDING, icon = "🎤"),
        TranslationStep(5, "Receiving transcript", StepStatus.PENDING, icon = "📝"),
        TranslationStep(6, "Translating to Bangla", StepStatus.PENDING, icon = "🧠"),
        TranslationStep(7, "Creating subtitle entries", StepStatus.PENDING, icon = "✅")
    )

    fun createNote(text: String): Note = Note(
        id = System.currentTimeMillis().toInt(),
        text = text,
        date = SimpleDateFormat("d/M/yyyy", java.util.Locale.getDefault()).format(Date())
    )

    fun createLoop(name: String, start: String, end: String, count: Int): Loop = Loop(
        id = System.currentTimeMillis().toInt(),
        name = name,
        start = start,
        end = end,
        count = count
    )
}
