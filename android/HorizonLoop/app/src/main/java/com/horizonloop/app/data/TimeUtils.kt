package com.horizonloop.app.data

/**
 * Parses a time string to seconds.
 * Supports "M:SS" format (e.g., "1:23" → 83 seconds) or plain seconds as string.
 */
fun parseTimeToSeconds(timeStr: String): Double {
    return try {
        val parts = timeStr.split(":")
        if (parts.size == 2) {
            parts[0].toDouble() * 60 + parts[1].toDouble()
        } else {
            timeStr.toDoubleOrNull() ?: Double.NaN
        }
    } catch (e: Exception) { Double.NaN }
}

/**
 * Formats seconds into "M:SS" string.
 */
fun formatTime(secs: Double): String {
    val m = (secs / 60).toInt()
    val s = (secs % 60).toInt()
    return "$m:${if (s < 10) "0" else ""}$s"
}

/**
 * Formats seconds into "M:SS" string, or "—" if NaN.
 */
fun formatTimestamp(seconds: Double): String {
    return if (seconds.isNaN()) "—" else {
        val m = (seconds / 60).toInt()
        val s = (seconds % 60).toInt()
        "$m:${if (s < 10) "0" else ""}$s"
    }
}

/**
 * Formats milliseconds into "M:SS" string.
 */
fun formatTimeFromMs(ms: Long): String {
    val secs = ms / 1000
    val m = (secs / 60).toInt()
    val s = (secs % 60).toInt()
    return "$m:${if (s < 10) "0" else ""}$s"
}