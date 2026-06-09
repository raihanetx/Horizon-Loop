package com.horizonloop.app.utils

import java.text.SimpleDateFormat
import java.util.Date

/**
 * TimeUtils.kt
 * Utility functions for time formatting and parsing
 */
object TimeUtils {
    
    fun formatTime(secs: Double): String {
        val m = (secs / 60).toInt()
        val s = (secs % 60).toInt()
        return "$m:${if (s < 10) "0" else ""}$s"
    }
    
    fun parseTimeToSeconds(timeStr: String): Double {
        return try {
            val cleanTime = timeStr.replace(",", ".")
            val parts = cleanTime.split(":")
            when (parts.size) {
                3 -> {
                    val hours = parts[0].toDouble()
                    val minutes = parts[1].toDouble()
                    val seconds = parts[2].toDouble()
                    hours * 3600 + minutes * 60 + seconds
                }
                2 -> {
                    parts[0].toDouble() * 60 + parts[1].toDouble()
                }
                1 -> parts[0].toDouble()
                else -> Double.NaN
            }
        } catch (e: Exception) { Double.NaN }
    }
    
    fun getTimestamp(): String {
        return SimpleDateFormat("HH:mm:ss.SSS").format(Date())
    }
}
