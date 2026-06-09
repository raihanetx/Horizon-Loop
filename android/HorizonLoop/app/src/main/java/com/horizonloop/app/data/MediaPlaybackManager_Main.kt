package com.horizonloop.app.data

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.*

/**
 * MediaPlaybackManager_Main.kt
 * Manages audio/video playback using Android MediaPlayer
 */

object MediaPlaybackManager {
    private const val TAG = "MediaPlaybackManager"
    private var mediaPlayer: MediaPlayer? = null
    private var playbackJob: Job? = null
    private var onProgressUpdate: ((Double, Double) -> Unit)? = null
    private var onPlaybackStateChanged: ((Boolean) -> Unit)? = null
    private var onError: ((String) -> Unit)? = null
    private var currentSpeed = 1.0f
    private var isAudioMode = false

    val isPlaying: Boolean get() = mediaPlayer?.isPlaying == true
    val duration: Double get() = mediaPlayer?.duration?.toDouble()?.div(1000) ?: 0.0
    val currentPosition: Double get() = mediaPlayer?.currentPosition?.toDouble()?.div(1000) ?: 0.0

    fun prepare(context: Context, filePath: String, onProgress: (Double, Double) -> Unit,
        onStateChanged: (Boolean) -> Unit, onPlaybackError: (String) -> Unit) {
        release(); onProgressUpdate = onProgress; onPlaybackStateChanged = onStateChanged; onError = onPlaybackError
        try {
            mediaPlayer = MediaPlayer().apply {
                if (filePath.startsWith("content://")) setDataSource(context, Uri.parse(filePath))
                else setDataSource(filePath)
                setOnPreparedListener { mp -> Log.d(TAG, "Media prepared, duration: ${mp.duration}ms"); setPlaybackParams(playbackParams.setSpeed(currentSpeed)) }
                setOnCompletionListener { Log.d(TAG, "Playback completed"); onStateChanged(false) }
                setOnErrorListener { _, what, extra -> Log.e(TAG, "Playback error: what=$what, extra=$extra"); val errMsg = when (what) { MediaPlayer.MEDIA_ERROR_UNKNOWN -> "Unknown playback error"; MediaPlayer.MEDIA_ERROR_SERVER_DIED -> "Media server died"; else -> "Unknown error ($what, $extra)" }; onPlaybackError(errMsg); true }
                prepareAsync()
                setOnPreparedListener { mp -> Log.d(TAG, "MediaPlayer ready"); setPlaybackParams(playbackParams.setSpeed(currentSpeed)); start() }
            }
        } catch (e: Exception) { Log.e(TAG, "Error preparing media: ${e.message}"); onPlaybackError("Failed to load media: ${e.message}") }
    }

    fun start() { mediaPlayer?.start() }
    fun pause() { mediaPlayer?.pause() }
    fun seekTo(positionMs: Int) { mediaPlayer?.seekTo(positionMs) }
    fun seekForward(seconds: Int = 5) { mediaPlayer?.let { it.seekTo((it.currentPosition + seconds * 1000).coerceAtMost(it.duration)) } }
    fun seekBackward(seconds: Int = 5) { mediaPlayer?.let { it.seekTo((it.currentPosition - seconds * 1000).coerceAtLeast(0)) } }

    fun setSpeed(speed: Float) { currentSpeed = speed; mediaPlayer?.let { it.setPlaybackParams(it.playbackParams.setSpeed(speed)) } }
    fun setAudioMode(enabled: Boolean) { isAudioMode = enabled }

    fun startProgressTracking(scope: CoroutineScope) {
        playbackJob?.cancel()
        playbackJob = scope.launch {
            while (isActive && mediaPlayer != null) {
                val pos = currentPosition; val dur = duration
                if (pos > 0 && dur > 0) onProgressUpdate?.invoke(pos, dur)
                delay(100)
            }
        }
    }

    fun release() { playbackJob?.cancel(); playbackJob = null; mediaPlayer?.release(); mediaPlayer = null }
}
