package com.horizonloop.app.data

import android.content.ContentUris
import android.content.Context
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * VideoScanner_Main.kt
 * Scans device for video files using MediaStore API
 */

object VideoScanner_Main {
    private const val TAG = "VideoScanner"

    suspend fun scanVideos(context: Context): List<Audio> = withContext(Dispatchers.IO) {
        val videos = mutableListOf<Audio>()
        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        }
        val projection = arrayOf(MediaStore.Video.Media._ID, MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.SIZE, MediaStore.Video.Media.DURATION, MediaStore.Video.Media.DATA)
        val sortOrder = "${MediaStore.Video.Media.DISPLAY_NAME} ASC"

        try {
            context.contentResolver.query(collection, projection, null, null, sortOrder)?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
                val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
                val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
                val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
                val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val name = cursor.getString(nameColumn) ?: "Unknown"
                    val size = cursor.getString(sizeColumn) ?: "0"
                    val duration = cursor.getLong(durationColumn)
                    val path = cursor.getString(dataColumn) ?: ""
                    val contentUri = ContentUris.withAppendedId(collection, id)
                    videos.add(Audio(id = id.toInt(), title = name, size = size, subtitle = false, pin = false,
                        bitrate = "", duration = "${duration / 1000}s", durationSec = duration / 1000.0,
                        filePath = path, contentUri = contentUri.toString()))
                }
            }
        } catch (e: Exception) { Log.e(TAG, "Error scanning videos", e) }
        videos
    }
}
