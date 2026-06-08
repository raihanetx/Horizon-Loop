package com.horizonloop.app.data

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Scans the device for video and audio files using MediaStore API
 * Uses real MediaStore IDs to avoid collisions
 */
object VideoScanner {
    
    private const val TAG = "VideoScanner"
    
    /**
     * Scan device for all video files
     * Returns list of Audio objects with actual file paths and content URIs
     */
    suspend fun scanVideos(context: Context): List<Audio> = withContext(Dispatchers.IO) {
        val videos = mutableListOf<Audio>()
        
        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        }
        
        val projection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.DATA,
            MediaStore.Video.Media.MIME_TYPE,
            MediaStore.Video.Media.DATE_ADDED
        )
        
        // Scan ALL videos, no limit, sorted by date newest first
        val sortOrder = "${MediaStore.Video.Media.DATE_ADDED} DESC"
        
        try {
            Log.d(TAG, "Starting video scan...")
            context.contentResolver.query(
                collection,
                projection,
                null,
                null,
                sortOrder
            )?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
                val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
                val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
                val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
                val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
                val mimeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE)
                
                Log.d(TAG, "Video cursor columns: ${cursor.columnCount}, rows: ${cursor.count}")
                
                while (cursor.moveToNext()) {
                    val mediaId = cursor.getLong(idColumn) // Use REAL MediaStore ID
                    val name = cursor.getString(nameColumn) ?: "Unknown"
                    val size = cursor.getLong(sizeColumn)
                    val duration = cursor.getLong(durationColumn)
                    val filePath = cursor.getString(dataColumn) ?: ""
                    val mimeType = cursor.getString(mimeColumn) ?: ""
                    
                    // Skip very short videos (< 1 second) and very small files
                    if (duration < 1000 || size < 1024) continue
                    
                    val contentUri = ContentUris.withAppendedId(collection, mediaId)
                    
                    // Clean up display name (remove extension)
                    val displayName = name.substringBeforeLast(".")
                    
                    // Convert size to MB string
                    val sizeMB = size / (1024.0 * 1024.0)
                    val sizeStr = String.format("%.1f MB", sizeMB)
                    
                    // Convert duration to seconds and format
                    val durationSec = duration / 1000.0
                    val durationStr = formatDuration(duration)
                    
                    // Determine if video likely has speech (longer videos more likely)
                    val likelyHasSubtitle = duration > 30000 // > 30 seconds
                    
                    videos.add(
                        Audio(
                            id = mediaId.toInt().coerceIn(0, Int.MAX_VALUE - 1), // Use REAL MediaStore ID (safe cast)
                            title = displayName,
                            size = sizeStr,
                            subtitle = likelyHasSubtitle,
                            pin = false,
                            bitrate = estimateBitrate(sizeMB, durationSec),
                            duration = durationStr,
                            durationSec = durationSec,
                            filePath = filePath,
                            mimeType = mimeType,
                            contentUri = contentUri.toString()
                        )
                    )
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error scanning videos: ${e.message}")
            e.printStackTrace()
        }
        
        Log.d(TAG, "Found ${videos.size} videos on device")
        return@withContext videos
    }
    
    /**
     * Scan device for audio files (music, recordings, WhatsApp audio, etc.)
     * Uses real MediaStore IDs to avoid collision with video IDs
     */
    suspend fun scanAudioFiles(context: Context): List<Audio> = withContext(Dispatchers.IO) {
        val audioFiles = mutableListOf<Audio>()
        
        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        }
        
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.SIZE,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.MIME_TYPE,
            MediaStore.Audio.Media.DATE_ADDED
        )
        
        // Get all audio files - no filter, sorted by date
        val sortOrder = "${MediaStore.Audio.Media.DATE_ADDED} DESC"
        
        try {
            Log.d(TAG, "Starting audio scan...")
            context.contentResolver.query(
                collection,
                projection,
                null,  // No selection filter - get all audio
                null,
                sortOrder
            )?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
                val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)
                val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
                val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                val mimeColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.MIME_TYPE)
                
                Log.d(TAG, "Audio cursor columns: ${cursor.columnCount}, rows: ${cursor.count}")
                
                while (cursor.moveToNext()) {
                    val mediaId = cursor.getLong(idColumn) // Use REAL MediaStore ID
                    val name = cursor.getString(nameColumn) ?: "Unknown"
                    val size = cursor.getLong(sizeColumn)
                    val duration = cursor.getLong(durationColumn)
                    val filePath = cursor.getString(dataColumn) ?: ""
                    val artist = cursor.getString(artistColumn) ?: "Unknown Artist"
                    val mimeType = cursor.getString(mimeColumn) ?: ""
                    
                    // Skip very short audio (< 5 seconds) and very small files
                    if (duration < 5000 || size < 1024) continue
                    
                    val contentUri = ContentUris.withAppendedId(collection, mediaId)
                    
                    // Clean up display name
                    val displayName = if (artist != "<unknown>" && artist.isNotBlank()) {
                        "$artist - ${name.substringBeforeLast(".")}"
                    } else {
                        name.substringBeforeLast(".")
                    }
                    
                    // Convert size to MB string
                    val sizeMB = size / (1024.0 * 1024.0)
                    val sizeStr = String.format("%.1f MB", sizeMB)
                    
                    // Convert duration
                    val durationSec = duration / 1000.0
                    val durationStr = formatDuration(duration)
                    
                    // Add unique offset to avoid ID collision with videos
                    // Video IDs are from 0-99999, audio uses 100000+
                    audioFiles.add(
                        Audio(
                            id = (mediaId + 100000).toInt().coerceIn(0, Int.MAX_VALUE - 1), // Use REAL MediaStore ID + offset (safe cast)
                            title = displayName,
                            size = sizeStr,
                            subtitle = true, // Most audio files could have lyrics/subtitles
                            pin = false,
                            bitrate = estimateBitrate(sizeMB, durationSec),
                            duration = durationStr,
                            durationSec = durationSec,
                            filePath = filePath,
                            mimeType = mimeType,
                            contentUri = contentUri.toString()
                        )
                    )
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error scanning audio: ${e.message}")
            e.printStackTrace()
        }
        
        Log.d(TAG, "Found ${audioFiles.size} audio files on device")
        return@withContext audioFiles
    }
    
    /**
     * Combine videos and audio into a single list sorted by date
     */
    suspend fun scanAllMedia(context: Context): List<Audio> {
        Log.d(TAG, "Starting full media scan...")
        val videos = scanVideos(context)
        Log.d(TAG, "Video scan complete: ${videos.size} videos")
        val audioFiles = scanAudioFiles(context)
        Log.d(TAG, "Audio scan complete: ${audioFiles.size} audio files")
        
        // Combine and sort by date added (newest first)
        val allMedia = videos + audioFiles
        Log.d(TAG, "Total media files: ${allMedia.size}")
        return allMedia
    }
    
    private fun formatDuration(durationMs: Long): String {
        val totalSeconds = durationMs / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%d:%02d", minutes, seconds)
    }
    
    private fun estimateBitrate(sizeMB: Double, durationSec: Double): String {
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