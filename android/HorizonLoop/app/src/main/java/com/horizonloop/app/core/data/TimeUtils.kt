package com.horizonloop.app.core.data

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

/**
 * Formats a time range as a compact "M:SS-SS" or "M:SS-M:SS" string.
 * When the end time is in the same minute as the start, the end is shown
 * as bare seconds (no leading zero, no minutes), producing a tidy
 * "[0:45-56]" style. When the end crosses a minute boundary, the full
 * "M:SS" form is used for the end.
 *
 * Returns "—" for NaN inputs.
 */
fun formatTimeRange(startSec: Double, endSec: Double): String {
    if (startSec.isNaN() || endSec.isNaN()) return "—"
    val startM = (startSec / 60).toInt()
    val startS = (startSec % 60).toInt()
    val startStr = "$startM:${if (startS < 10) "0" else ""}$startS"

    val endM = (endSec / 60).toInt()
    val endS = (endSec % 60).toInt()

    val endStr = if (endM == startM) {
        // Same minute — just bare seconds, no leading zero
        "$endS"
    } else {
        // Crosses a minute boundary — full M:SS
        "$endM:${if (endS < 10) "0" else ""}$endS"
    }
    return "$startStr-$endStr"
}