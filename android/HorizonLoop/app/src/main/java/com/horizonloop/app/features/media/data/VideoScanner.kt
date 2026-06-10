package com.horizonloop.app.features.media.data

import android.content.Context
import android.util.Log

/**
 * Scans the device for video and audio files using MediaStore API
 */
object VideoScanner {
    private const val TAG = "VideoScanner"

    suspend fun scanVideos(context: Context) = VideoScannerImpl.scanVideos(context)
    suspend fun scanAudioFiles(context: Context) = VideoScannerImpl.scanAudioFiles(context)

    suspend fun scanAllMedia(context: Context): List<com.horizonloop.app.core.domain.model.Audio> {
        Log.d(TAG, "Starting full media scan...")
        val videos = VideoScannerImpl.scanVideos(context)
        Log.d(TAG, "Video scan complete: ${videos.size} videos")
        val audioFiles = VideoScannerImpl.scanAudioFiles(context)
        Log.d(TAG, "Audio scan complete: ${audioFiles.size} audio files")
        val allMedia = videos + audioFiles
        Log.d(TAG, "Total media files: ${allMedia.size}")
        return allMedia
    }
}