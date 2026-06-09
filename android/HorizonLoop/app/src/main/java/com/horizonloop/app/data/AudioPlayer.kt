package com.horizonloop.app.data

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import java.io.File

/**
 * AudioPlayer - Handles actual audio/video playback using Android MediaPlayer.
 * Supports both file paths and content:// URIs for scoped storage compatibility.
 */
class AudioPlayer(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null
    private val helpers: AudioPlayerHelpers by lazy { AudioPlayerHelpers({ mediaPlayer }) }

    var onProgressUpdate: ((currentMs: Long, totalMs: Long) -> Unit)? = null
        set(value) { helpers.onProgressUpdate = value }
    var onPlaybackStateChanged: ((isPlaying: Boolean) -> Unit)? = null
        set(value) { helpers.onPlaybackStateChanged = value }
    var onCompletion: (() -> Unit)? = null
    var onError: ((String) -> Unit)? = null

    private var isPrepared = false
    private var currentSource: String = ""

    fun load(source: String): Boolean {
        Log.d("AudioPlayer", "Loading audio: $source")
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
            }
            currentSource = source
            if (source.startsWith("content://")) {
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
                mediaPlayer?.setDataSource(File(source.removePrefix("file://")).absolutePath)
            } else if (File(source).exists()) {
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

    fun play(): Boolean {
        return try {
            if (!isPrepared) { Log.w("AudioPlayer", "Cannot play - not prepared yet"); return false }
            mediaPlayer?.start()
            helpers.startProgressTracking()
            onPlaybackStateChanged?.invoke(true)
            Log.d("AudioPlayer", "Playback started")
            true
        } catch (e: Exception) {
            Log.e("AudioPlayer", "Error starting playback: ${e.message}")
            onError?.invoke("Playback error: ${e.message}")
            false
        }
    }

    fun pause() {
        try {
            mediaPlayer?.pause()
            helpers.stopProgressTracking()
            onPlaybackStateChanged?.invoke(false)
        } catch (e: Exception) { Log.e("AudioPlayer", "Error pausing: ${e.message}") }
    }

    fun togglePlayPause() {
        if (mediaPlayer?.isPlaying == true) pause() else play()
    }

    fun seekTo(positionMs: Long) {
        try { mediaPlayer?.seekTo(positionMs.toInt()) } catch (e: Exception) { Log.e("AudioPlayer", "Error seeking: ${e.message}") }
    }

    fun seekToPercent(percent: Float) {
        val duration = try { mediaPlayer?.duration ?: 0 } catch (e: Exception) { 0 }
        if (duration > 0) seekTo((percent * duration).toLong())
    }

    fun getCurrentPosition(): Long = try { mediaPlayer?.currentPosition?.toLong() ?: 0L } catch (e: Exception) { 0L }
    fun getDuration(): Long = try { mediaPlayer?.duration?.toLong() ?: 0L } catch (e: Exception) { 0L }
    fun isPlaying(): Boolean = try { mediaPlayer?.isPlaying == true } catch (e: Exception) { false }

    fun setSpeed(speed: Float) { helpers.setSpeed(speed) }
    fun rewind() { helpers.rewind() }
    fun forward() { helpers.forward() }

    fun release() {
        helpers.stopProgressTracking()
        try { mediaPlayer?.stop(); mediaPlayer?.release() } catch (e: Exception) { Log.e("AudioPlayer", "Error releasing: ${e.message}") }
        mediaPlayer = null; isPrepared = false; currentSource = ""
    }

    fun destroy() { release(); helpers.destroy() }
}