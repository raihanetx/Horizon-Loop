package com.horizonloop.app.features.media.data

object VideoScannerHelpers {
    fun formatDuration(durationMs: Long): String {
        val totalSeconds = durationMs / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%d:%02d", minutes, seconds)
    }

    fun estimateBitrate(sizeMB: Double, durationSec: Double): String {
        if (durationSec <= 0) return "Unknown"
        val bitrateKbps = (sizeMB * 8 * 1024 / durationSec).toInt()
        return when {
            bitrateKbps < 128 -> "128 kbps"
            bitrateKbps < 192 -> "192 kbps"
            bitrateKbps < 256 -> "256 kbps"
            else -> "320 kbps"
        }
    }
}