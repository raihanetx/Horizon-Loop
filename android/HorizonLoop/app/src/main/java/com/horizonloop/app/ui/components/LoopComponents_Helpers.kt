package com.horizonloop.app.ui.components

/**
 * LoopComponents_Helpers.kt
 * Helper functions for loop time parsing and formatting
 */

fun parseTimeToSeconds(timeStr: String): Double {
    return try {
        val parts = timeStr.split(":")
        if (parts.size == 2) parts[0].toDouble() * 60 + parts[1].toDouble()
        else timeStr.toDoubleOrNull() ?: Double.NaN
    } catch (e: Exception) { Double.NaN }
}

fun formatTimestamp(seconds: Double): String {
    return if (seconds.isNaN()) "—" else {
        val m = (seconds / 60).toInt()
        val s = (seconds % 60).toInt()
        "$m:${if (s < 10) "0" else ""}$s"
    }
}
