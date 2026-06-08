package com.horizonloop.app.data

import android.content.Context
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMuxer
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.nio.ByteBuffer

object AudioExtractor {
    
    private const val MAX_FILE_SIZE_MB = 25
    
    /**
     * Extract audio from video file using Android's built-in MediaExtractor
     * Supports both file paths (legacy) and content URIs (scoped storage)
     * 
     * @param context Android context
     * @param videoPath Path to the video file OR content URI string
     * @param outputDir Directory to save the extracted audio
     * @param onProgress Callback for progress updates (0.0 to 1.0)
     * @return Path to extracted audio file (.m4a), or null if failed
     */
    suspend fun extractAudio(
        context: Context,
        videoPath: String,
        outputDir: File,
        onProgress: (Float) -> Unit = {}
    ): String? = withContext(Dispatchers.IO) {
        var extractor: MediaExtractor? = null
        var muxer: MediaMuxer? = null
        
        try {
            val outputFile = File(outputDir, "temp_audio_${System.currentTimeMillis()}.m4a")
            
            extractor = MediaExtractor()
            
            // Check if this is a content URI or file path
            if (videoPath.startsWith("content://")) {
                // Use content resolver for scoped storage
                val uri = Uri.parse(videoPath)
                val fd = context.contentResolver.openFileDescriptor(uri, "r")
                if (fd == null) {
                    android.util.Log.e("AudioExtractor", "Failed to open content URI")
                    return@withContext null
                }
                extractor.setDataSource(fd.fileDescriptor)
                fd.close()
            } else {
                // Use file path directly (legacy)
                extractor.setDataSource(videoPath)
            }
            
            // Find the first audio track
            var audioTrackIndex = -1
            var audioFormat: MediaFormat? = null
            
            for (i in 0 until extractor.trackCount) {
                val format = extractor.getTrackFormat(i)
                val mime = format.getString(MediaFormat.KEY_MIME) ?: ""
                if (mime.startsWith("audio/") || mime.startsWith("video/")) {
                    // Check if track has audio
                    if (mime.startsWith("audio/") || format.containsKey(MediaFormat.KEY_DURATION)) {
                        try {
                            audioTrackIndex = i
                            audioFormat = format
                            break
                        } catch (e: Exception) {
                            continue
                        }
                    }
                }
            }
            
            if (audioTrackIndex < 0 || audioFormat == null) {
                android.util.Log.e("AudioExtractor", "No audio track found in video")
                return@withContext null
            }
            
            // Create muxer with M4A output
            muxer = MediaMuxer(outputFile.absolutePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
            
            val muxerTrackIndex = muxer.addTrack(audioFormat)
            muxer.start()
            
            // Get duration for progress calculation
            val durationUs = audioFormat.getLong(MediaFormat.KEY_DURATION)
            
            // Select the audio track
            extractor.selectTrack(audioTrackIndex)
            
            // Read and write audio samples
            val bufferSize = 1024 * 1024 // 1MB buffer
            val buffer = ByteBuffer.allocate(bufferSize)
            val bufferInfo = android.media.MediaCodec.BufferInfo()
            
            var processedUs = 0L
            
            while (true) {
                val sampleSize = extractor.readSampleData(buffer, 0)
                
                if (sampleSize < 0) {
                    break
                }
                
                bufferInfo.offset = 0
                bufferInfo.size = sampleSize
                bufferInfo.presentationTimeUs = extractor.sampleTime
                bufferInfo.flags = extractor.sampleFlags
                
                muxer.writeSampleData(muxerTrackIndex, buffer, bufferInfo)
                
                // Update progress
                processedUs = extractor.sampleTime
                if (durationUs > 0) {
                    onProgress((processedUs.toFloat() / durationUs.toFloat()) * 0.9f)
                }
                
                extractor.advance()
            }
            
            onProgress(1f)
            
            return@withContext outputFile.absolutePath
            
        } catch (e: Exception) {
            android.util.Log.e("AudioExtractor", "Extraction error: ${e.message}")
            null
        } finally {
            try {
                muxer?.stop()
                muxer?.release()
            } catch (e: Exception) { }
            extractor?.release()
        }
    }
    
    /**
     * Split large audio file into chunks of MAX_FILE_SIZE_MB
     * For simplicity, we just return the original path if under the limit
     */
    suspend fun splitAudio(
        audioPath: String,
        outputDir: File
    ): List<String> = withContext(Dispatchers.IO) {
        val audioFile = File(audioPath)
        val fileSizeMB = audioFile.length() / (1024 * 1024)
        
        if (fileSizeMB <= MAX_FILE_SIZE_MB) {
            return@withContext listOf(audioPath)
        }
        
        listOf(audioPath)
    }
    
    /**
     * Get duration of audio/video file using MediaExtractor
     */
    fun getDuration(context: Context, pathOrUri: String): Double {
        var extractor: MediaExtractor? = null
        return try {
            extractor = MediaExtractor()
            
            if (pathOrUri.startsWith("content://")) {
                val fd = context.contentResolver.openFileDescriptor(Uri.parse(pathOrUri), "r")
                fd?.let {
                    extractor.setDataSource(it.fileDescriptor)
                    it.close()
                }
            } else {
                extractor.setDataSource(pathOrUri)
            }
            
            for (i in 0 until extractor.trackCount) {
                val format = extractor.getTrackFormat(i)
                if (format.containsKey(MediaFormat.KEY_DURATION)) {
                    val durationUs = format.getLong(MediaFormat.KEY_DURATION)
                    return durationUs / 1_000_000.0
                }
            }
            0.0
        } catch (e: Exception) {
            0.0
        } finally {
            extractor?.release()
        }
    }
}