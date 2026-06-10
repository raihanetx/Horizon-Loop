package com.horizonloop.app.features.media.data

import android.content.ContentUris
import android.content.Context
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal object VideoScannerImpl {

    private const val TAG = "VideoScanner"

    suspend fun scanVideos(context: Context): List<com.horizonloop.app.core.domain.model.Audio> = withContext(Dispatchers.IO) {
        val videos = mutableListOf<com.horizonloop.app.core.domain.model.Audio>()
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
        val sortOrder = "${MediaStore.Video.Media.DATE_ADDED} DESC"
        try {
            Log.d(TAG, "Starting video scan...")
            context.contentResolver.query(collection, projection, null, null, sortOrder)?.use { cursor ->
                val idCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
                val nameCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
                val sizeCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
                val durCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
                val dataCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
                val mimeCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE)
                while (cursor.moveToNext()) {
                    val mediaId = cursor.getLong(idCol)
                    val name = cursor.getString(nameCol) ?: "Unknown"
                    val size = cursor.getLong(sizeCol)
                    val duration = cursor.getLong(durCol)
                    val filePath = cursor.getString(dataCol) ?: ""
                    val mimeType = cursor.getString(mimeCol) ?: ""
                    if (duration < 1000 || size < 1024) continue
                    val contentUri = ContentUris.withAppendedId(collection, mediaId)
                    val displayName = name.substringBeforeLast(".")
                    val sizeMB = size / (1024.0 * 1024.0)
                    val sizeStr = String.format("%.1f MB", sizeMB)
                    val durationSec = duration / 1000.0
                    val durationStr = VideoScannerHelpers.formatDuration(duration)
                    val likelyHasSubtitle = duration > 30000
                    videos.add(
                        com.horizonloop.app.core.domain.model.Audio(
                            id = mediaId.toInt().coerceIn(0, Int.MAX_VALUE - 1),
                            title = displayName,
                            size = sizeStr,
                            subtitle = likelyHasSubtitle,
                            pin = false,
                            bitrate = VideoScannerHelpers.estimateBitrate(sizeMB, durationSec),
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
        }
        return@withContext videos
    }

    suspend fun scanAudioFiles(context: Context): List<com.horizonloop.app.core.domain.model.Audio> = withContext(Dispatchers.IO) {
        val audioFiles = mutableListOf<com.horizonloop.app.core.domain.model.Audio>()
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
        val sortOrder = "${MediaStore.Audio.Media.DATE_ADDED} DESC"
        try {
            Log.d(TAG, "Starting audio scan...")
            context.contentResolver.query(collection, projection, null, null, sortOrder)?.use { cursor ->
                val idCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val nameCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
                val sizeCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)
                val durCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                val dataCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
                val artistCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                val mimeCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.MIME_TYPE)
                while (cursor.moveToNext()) {
                    val mediaId = cursor.getLong(idCol)
                    val name = cursor.getString(nameCol) ?: "Unknown"
                    val size = cursor.getLong(sizeCol)
                    val duration = cursor.getLong(durCol)
                    val filePath = cursor.getString(dataCol) ?: ""
                    val artist = cursor.getString(artistCol) ?: "Unknown Artist"
                    val mimeType = cursor.getString(mimeCol) ?: ""
                    if (duration < 5000 || size < 1024) continue
                    val contentUri = ContentUris.withAppendedId(collection, mediaId)
                    val displayName = if (artist != "<unknown>" && artist.isNotBlank()) "$artist - ${name.substringBeforeLast(".")}" else name.substringBeforeLast(".")
                    val sizeMB = size / (1024.0 * 1024.0)
                    val sizeStr = String.format("%.1f MB", sizeMB)
                    val durationSec = duration / 1000.0
                    val durationStr = VideoScannerHelpers.formatDuration(duration)
                    audioFiles.add(
                        com.horizonloop.app.core.domain.model.Audio(
                            id = (mediaId + 100000).toInt().coerceIn(0, Int.MAX_VALUE - 1),
                            title = displayName,
                            size = sizeStr,
                            subtitle = true,
                            pin = false,
                            bitrate = VideoScannerHelpers.estimateBitrate(sizeMB, durationSec),
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
        }
        return@withContext audioFiles
    }
}