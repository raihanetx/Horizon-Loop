package com.horizonloop.app.data

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.*
import java.io.File

/**
 * AudioPlayer - Handles actual audio/video playback using Android MediaPlayer
 * Supports both file paths and content:// URIs for scoped storage compatibility
 */
class AudioPlayer(private val context: Context) {
    
    private var mediaPlayer: MediaPlayer? = null
    private var playbackJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    
    // Callbacks for playback state updates
    var onProgressUpdate: ((currentMs: Long, totalMs: Long) -> Unit)? = null
    var onPlaybackStateChanged: ((isPlaying: Boolean) -> Unit)? = null
    var onCompletion: (() -> Unit)? = null
    var onError: ((String) -> Unit)? = null
    
    private var isPrepared = false
    private var currentSource: String = ""
    
    /**
     * Load and prepare audio from file path or content URI
     */
    fun load(source: String): Boolean {
        Log.d("AudioPlayer", "Loading audio: $source")
        
        // Release any existing player
        release()
        
        try {
            mediaPlayer = MediaPlayer().apply {
                setOnPreparedListener { mp ->
                    isPrepared = true
                    Log.d("AudioPlayer", "MediaPlayer prepared, duration: ${mp.duration}ms")
                }
                
                setOnCompletionListener {
                    Log.d("AudioPlayer", "Playback completed")
                    onCompletion?.invoke()
                }
                
                setOnErrorListener { _, what, extra ->
                    Log.e("AudioPlayer", "MediaPlayer error: what=$what, extra=$extra")
                    onError?.invoke("Playback error: $what")
                    true
                }
                
                setOnInfoListener { _, what, extra ->
                    Log.d("AudioPlayer", "MediaPlayer info: what=$what, extra=$extra")
                    false
                }
            }
            
            currentSource = source
            
            if (source.startsWith("content://")) {
                // Scoped storage - use content URI
                val uri = Uri.parse(source)
                val pfd = context.contentResolver.openFileDescriptor(uri, "r")
                if (pfd != null) {
                    mediaPlayer?.setDataSource(pfd.fileDescriptor)
                    pfd.close()
                } else {
                    onError?.invoke("Cannot open file descriptor")
                    return false
                }
            } else if (source.startsWith("file://")) {
                // File URI
                val file = File(source.removePrefix("file://"))
                mediaPlayer?.setDataSource(file.absolutePath)
            } else if (File(source).exists()) {
                // Direct file path
                mediaPlayer?.setDataSource(source)
            } else {
                onError?.invoke("File not found: $source")
                return false
            }
            
            mediaPlayer?.prepareAsync()
            return true
            
        } catch (e: Exception) {
            Log.e("AudioPlayer", "Error loading audio: ${e.message}")
            onError?.invoke("Failed to load: ${e.message}")
            return false
        }
    }
    
    /**
     * Start or resume playback
     */
    fun play(): Boolean {
        return try {
            if (!isPrepared) {
                Log.w("AudioPlayer", "Cannot play - not prepared yet")
                return false
            }
            mediaPlayer?.start()
            startProgressTracking()
            onPlaybackStateChanged?.invoke(true)
            Log.d("AudioPlayer", "Playback started")
            true
        } catch (e: Exception) {
            Log.e("AudioPlayer", "Error starting playback: ${e.message}")
            onError?.invoke("Playback error: ${e.message}")
            false
        }
    }
    
    /**
     * Pause playback
     */
    fun pause() {
        try {
            mediaPlayer?.pause()
            stopProgressTracking()
            onPlaybackStateChanged?.invoke(false)
            Log.d("AudioPlayer", "Playback paused")
        } catch (e: Exception) {
            Log.e("AudioPlayer", "Error pausing: ${e.message}")
        }
    }
    
    /**
     * Toggle play/pause
     */
    fun togglePlayPause() {
        if (mediaPlayer?.isPlaying == true) {
            pause()
        } else {
            play()
        }
    }
    
    /**
     * Seek to position in milliseconds
     */
    fun seekTo(positionMs: Long) {
        try {
            mediaPlayer?.seekTo(positionMs.toInt())
            Log.d("AudioPlayer", "Seeked to $positionMs ms")
        } catch (e: Exception) {
            Log.e("AudioPlayer", "Error seeking: ${e.message}")
        }
    }
    
    /**
     * Seek to position by percentage (0.0 to 1.0)
     */
    fun seekToPercent(percent: Float) {
        val duration = mediaPlayer?.duration ?: 0
        if (duration > 0) {
            val position = (percent * duration).toLong()
            seekTo(position)
        }
    }
    
    /**
     * Get current playback position in milliseconds
     */
    fun getCurrentPosition(): Long {
        return try {
            mediaPlayer?.currentPosition?.toLong() ?: 0L
        } catch (e: Exception) {
            0L
        }
    }
    
    /**
     * Get total duration in milliseconds
     */
    fun getDuration(): Long {
        return try {
            mediaPlayer?.duration?.toLong() ?: 0L
        } catch (e: Exception) {
            0L
        }
    }
    
    /**
     * Check if currently playing
     */
    fun isPlaying(): Boolean {
        return try {
            mediaPlayer?.isPlaying == true
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Set playback speed (1.0 = normal)
     */
    fun setSpeed(speed: Float) {
        try {
            // Note: MediaPlayer.setPlaybackParams only works on API 23+
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                mediaPlayer?.let { mp ->
                    val wasPlaying = mp.isPlaying
                    mp.playbackParams = mp.playbackParams.setSpeed(speed)
                    if (!wasPlaying) {
                        mp.pause()
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("AudioPlayer", "Error setting speed: ${e.message}")
        }
    }
    
    /**
     * Rewind by 5 seconds
     */
    fun rewind() {
        val newPos = (getCurrentPosition() - 5000).coerceAtLeast(0)
        seekTo(newPos)
    }
    
    /**
     * Forward by 5 seconds
     */
    fun forward() {
        val newPos = (getCurrentPosition() + 5000).coerceAtMost(getDuration())
        seekTo(newPos)
    }
    
    private fun startProgressTracking() {
        stopProgressTracking()
        playbackJob = scope.launch {
            while (isActive) {
                if (mediaPlayer?.isPlaying == true) {
                    val current = getCurrentPosition()
                    val total = getDuration()
                    onProgressUpdate?.invoke(current, total)
                }
                delay(100) // Update every 100ms
            }
        }
    }
    
    private fun stopProgressTracking() {
        playbackJob?.cancel()
        playbackJob = null
    }
    
    /**
     * Release all resources
     */
    fun release() {
        stopProgressTracking()
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        } catch (e: Exception) {
            Log.e("AudioPlayer", "Error releasing: ${e.message}")
        }
        mediaPlayer = null
        isPrepared = false
        currentSource = ""
    }
    
    fun destroy() {
        release()
        scope.cancel()
    }
}