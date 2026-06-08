package com.horizonloop.app.data

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.util.Log
import kotlinx.coroutines.*
import java.io.File

/**
 * Manages audio/video playback using Android MediaPlayer
 * Supports both file paths and content URIs (for scoped storage)
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
    
    val isPlaying: Boolean
        get() = mediaPlayer?.isPlaying == true
    
    val duration: Double
        get() = mediaPlayer?.duration?.toDouble()?.div(1000) ?: 0.0
    
    val currentPosition: Double
        get() = mediaPlayer?.currentPosition?.toDouble()?.div(1000) ?: 0.0
    
    /**
     * Initialize playback with the given file path or content URI
     */
    fun prepare(
        context: Context,
        filePath: String,
        onProgress: (Double, Double) -> Unit,
        onStateChanged: (Boolean) -> Unit,
        onPlaybackError: (String) -> Unit
    ) {
        release()
        
        onProgressUpdate = onProgress
        onPlaybackStateChanged = onStateChanged
        onError = onPlaybackError
        
        try {
            mediaPlayer = MediaPlayer().apply {
                if (filePath.startsWith("content://")) {
                    // Scoped storage - use content URI
                    val uri = Uri.parse(filePath)
                    setDataSource(context, uri)
                    Log.d(TAG, "Using content URI: $filePath")
                } else {
                    // Direct file path
                    setDataSource(filePath)
                    Log.d(TAG, "Using file path: $filePath")
                }
                
                setOnPreparedListener { mp ->
                    Log.d(TAG, "Media prepared, duration: ${mp.duration}ms")
                    // Set playback speed
                    setPlaybackParams(playbackParams.setSpeed(currentSpeed))
                }
                
                setOnCompletionListener {
                    Log.d(TAG, "Playback completed")
                    onStateChanged(false)
                }
                
                setOnErrorListener { _, what, extra ->
                    Log.e(TAG, "Playback error: what=$what, extra=$extra")
                    val errorMsg = when (what) {
                        MediaPlayer.MEDIA_ERROR_UNKNOWN -> "Unknown playback error"
                        MediaPlayer.MEDIA_ERROR_SERVER_DIED -> "Media server died"
                        else -> "Playback error occurred"
                    }
                    onPlaybackError(errorMsg)
                    true
                }
                
                prepareAsync()
            }
            
            // Wait for async preparation
            playbackJob = CoroutineScope(Dispatchers.Main).launch {
                delay(500) // Give time for async prepare
                try {
                    mediaPlayer?.start()
                    onStateChanged(true)
                    startProgressTracking()
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to start: ${e.message}")
                    onPlaybackError("Failed to start playback: ${e.message}")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Prepare error: ${e.message}")
            onPlaybackError("Failed to prepare media: ${e.message}")
        }
    }
    
    private fun startProgressTracking() {
        playbackJob?.cancel()
        playbackJob = CoroutineScope(Dispatchers.Main).launch {
            while (isActive && mediaPlayer?.isPlaying == true) {
                val pos = currentPosition
                val dur = duration
                onProgressUpdate?.invoke(pos, dur)
                delay(100) // Update every 100ms
            }
        }
    }
    
    fun play() {
        try {
            mediaPlayer?.start()
            onPlaybackStateChanged?.invoke(true)
            startProgressTracking()
        } catch (e: Exception) {
            Log.e(TAG, "Play error: ${e.message}")
        }
    }
    
    fun pause() {
        try {
            mediaPlayer?.pause()
            onPlaybackStateChanged?.invoke(false)
            playbackJob?.cancel()
        } catch (e: Exception) {
            Log.e(TAG, "Pause error: ${e.message}")
        }
    }
    
    fun togglePlayPause() {
        if (isPlaying) pause() else play()
    }
    
    fun seekTo(seconds: Double) {
        try {
            mediaPlayer?.seekTo((seconds * 1000).toInt())
            onProgressUpdate?.invoke(currentPosition, duration)
        } catch (e: Exception) {
            Log.e(TAG, "Seek error: ${e.message}")
        }
    }
    
    fun setSpeed(speed: Float) {
        currentSpeed = speed
        try {
            mediaPlayer?.let { mp ->
                if (mp.isPlaying) {
                    mp.playbackParams = mp.playbackParams.setSpeed(speed)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Speed error: ${e.message}")
        }
    }
    
    fun setAudioMode(enabled: Boolean) {
        isAudioMode = enabled
        // In audio mode, we still play but UI shows different content
    }
    
    fun rewind(seconds: Double = 5.0) {
        seekTo((currentPosition - seconds).coerceAtLeast(0.0))
    }
    
    fun forward(seconds: Double = 5.0) {
        seekTo((currentPosition + seconds).coerceAtMost(duration))
    }
    
    fun release() {
        playbackJob?.cancel()
        playbackJob = null
        try {
            mediaPlayer?.release()
        } catch (e: Exception) {
            Log.e(TAG, "Release error: ${e.message}")
        }
        mediaPlayer = null
    }
}
