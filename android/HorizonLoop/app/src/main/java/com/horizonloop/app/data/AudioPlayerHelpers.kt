package com.horizonloop.app.data

import kotlinx.coroutines.*

/**
 * Playback progress tracking, speed control, and seek helpers.
 * Extracted from AudioPlayer to keep each file under 200 lines.
 * Uses a getter to access the MediaPlayer, so it works correctly even
 * when the player is assigned after these helpers are constructed.
 */
class AudioPlayerHelpers(private val getMediaPlayer: () -> android.media.MediaPlayer?) {
    private var playbackJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    var onProgressUpdate: ((currentMs: Long, totalMs: Long) -> Unit)? = null
    var onPlaybackStateChanged: ((isPlaying: Boolean) -> Unit)? = null

    fun startProgressTracking() {
        stopProgressTracking()
        playbackJob = scope.launch {
            while (isActive) {
                val mp = getMediaPlayer()
                if (mp?.isPlaying == true) {
                    val current = try { mp.currentPosition.toLong() } catch (e: Exception) { 0L }
                    val total = try { mp.duration.toLong() } catch (e: Exception) { 0L }
                    onProgressUpdate?.invoke(current, total)
                }
                delay(100)
            }
        }
    }

    fun stopProgressTracking() {
        playbackJob?.cancel()
        playbackJob = null
    }

    fun seekToPercent(percent: Float) {
        val mp = getMediaPlayer() ?: return
        val duration = try { mp.duration } catch (e: Exception) { 0 }
        if (duration > 0) {
            val position = (percent * duration).toLong()
            try { mp.seekTo(position.toInt()) } catch (e: Exception) { }
        }
    }

    fun rewind() {
        val mp = getMediaPlayer() ?: return
        val current = try { mp.currentPosition } catch (e: Exception) { 0 }
        val newPos = (current - 5000).coerceAtLeast(0)
        try { mp.seekTo(newPos) } catch (e: Exception) { }
    }

    fun forward() {
        val mp = getMediaPlayer() ?: return
        val duration = try { mp.duration } catch (e: Exception) { 0 }
        val current = try { mp.currentPosition } catch (e: Exception) { 0 }
        val newPos = (current + 5000).coerceAtMost(duration)
        try { mp.seekTo(newPos) } catch (e: Exception) { }
    }

    fun setSpeed(speed: Float) {
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                val mp = getMediaPlayer() ?: return
                val wasPlaying = try { mp.isPlaying } catch (e: Exception) { false }
                mp.playbackParams = mp.playbackParams.setSpeed(speed)
                if (!wasPlaying) mp.pause()
            }
        } catch (e: Exception) {
            android.util.Log.e("AudioPlayer", "Error setting speed: ${e.message}")
        }
    }

    fun destroy() {
        stopProgressTracking()
        scope.cancel()
    }
}