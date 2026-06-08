package com.horizonloop.app.data

import android.content.Context
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMuxer
import android.net.Uri
import android.util.Log
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

/**
 * Extracts audio track from video files using MediaMuxer
 * Produces proper M4A/AAC output that Groq API can process
 */
object AudioExtractor {
    
    private const val TAG = "AudioExtractor"
    
    /**
     * Extract audio from video file using MediaMuxer
     * Supports both content:// URIs (scoped storage) and file paths
     */
    fun extractAudio(
        context: Context,
        videoPath: String,
        outputDir: File,
        onProgress: (Double) -> Unit
    ): String? {
        val extractor = MediaExtractor()
        
        try {
            val tag = "AudioExtractor"
            Log.d(tag, "extractAudio: $videoPath")
            
            // Set up data source
            if (videoPath.startsWith("content://")) {
                val uri = Uri.parse(videoPath)
                val pfd = context.contentResolver.openFileDescriptor(uri, "r")
                if (pfd == null) {
                    Log.e(tag, "Failed to open content URI")
                    return null
                }
                extractor.setDataSource(pfd.fileDescriptor)
                pfd.close()
            } else {
                extractor.setDataSource(videoPath)
            }
            
            // Find audio track
            var audioTrackIndex = -1
            var audioFormat: MediaFormat? = null
            
            for (i in 0 until extractor.trackCount) {
                val format = extractor.getTrackFormat(i)
                val mime = format.getString(MediaFormat.KEY_MIME) ?: ""
                if (mime.startsWith("audio/")) {
                    audioTrackIndex = i
                    audioFormat = format
                    Log.d(tag, "Found audio track at index $i")
                    break
                }
            }
            
            if (audioTrackIndex < 0 || audioFormat == null) {
                Log.e(tag, "No audio track found")
                return null
            }
            
            val durationUs = if (audioFormat.containsKey(MediaFormat.KEY_DURATION)) {
                audioFormat.getLong(MediaFormat.KEY_DURATION)
            } else {
                0L
            }
            
            extractor.selectTrack(audioTrackIndex)
            
            // Create output file
            val outputFile = File(outputDir, "audio_${System.currentTimeMillis()}.m4a")
            Log.d(tag, "Output: ${outputFile.absolutePath}")
            
            // Use MediaMuxer to write audio track
            val muxer = MediaMuxer(outputFile.absolutePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
            val muxerTrackIndex = muxer.addTrack(audioFormat)
            muxer.start()
            
            val bufferSize = 1024 * 1024
            val buffer = java.nio.ByteBuffer.allocate(bufferSize)
            val bufferInfo = android.media.MediaCodec.BufferInfo()
            
            var totalBytesWritten = 0L
            var samplesWritten = 0L
            
            while (true) {
                buffer.clear()  // Reset buffer position before each read
                val sampleSize = extractor.readSampleData(buffer, 0)
                if (sampleSize < 0) {
                    break
                }
                
                bufferInfo.offset = 0
                bufferInfo.size = sampleSize
                bufferInfo.presentationTimeUs = extractor.sampleTime
                bufferInfo.flags = 0
                
                muxer.writeSampleData(muxerTrackIndex, buffer, bufferInfo)
                totalBytesWritten += sampleSize
                samplesWritten++
                
                // Update progress
                if (durationUs > 0) {
                    val progress = extractor.sampleTime.toDouble() / durationUs.toDouble()
                    onProgress(progress.coerceIn(0.0, 1.0))
                }
                
                extractor.advance()
            }
            
            onProgress(1.0)
            
            muxer.stop()
            muxer.release()
            extractor.release()
            
            Log.d(tag, "Extracted $totalBytesWritten bytes, $samplesWritten samples")
            
            return if (outputFile.exists() && outputFile.length() > 0) {
                outputFile.absolutePath
            } else {
                null
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error: ${e.message}")
            extractor.release()
            return null
        }
    }
    
    /**
     * Split audio file into chunks for API upload
     */
    fun splitAudio(audioPath: String, outputDir: File): List<String> {
        val chunks = mutableListOf<String>()
        
        try {
            val file = File(audioPath)
            if (!file.exists()) {
                Log.e(TAG, "File not found: $audioPath")
                return chunks
            }
            
            val fileSize = file.length()
            val maxChunkSize = 20L * 1024 * 1024 // 20MB
            
            if (fileSize <= maxChunkSize) {
                // No split needed
                chunks.add(audioPath)
                return chunks
            }
            
            // Split into chunks
            val inputStream = FileInputStream(file)
            var chunkIndex = 0
            var bytesRead = 0L
            
            while (bytesRead < fileSize) {
                val remaining = fileSize - bytesRead
                val chunkSize = minOf(remaining, maxChunkSize)
                
                val chunkName = "chunk_" + chunkIndex + "_" + System.currentTimeMillis() + ".m4a"
                val chunkFile = File(outputDir, chunkName)
                val outputStream = FileOutputStream(chunkFile)
                
                val buffer = ByteArray(chunkSize.toInt())
                val read = inputStream.read(buffer)
                if (read > 0) {
                    outputStream.write(buffer, 0, read)
                    chunks.add(chunkFile.absolutePath)
                    chunkIndex++
                    bytesRead += read
                }
                
                outputStream.close()
            }
            
            inputStream.close()
            Log.d(TAG, "Split into ${chunks.size} chunks")
            
        } catch (e: Exception) {
            Log.e(TAG, "Split error: ${e.message}")
        }
        
        return chunks
    }
    
    /**
     * Get duration of audio file
     */
    fun getDuration(context: Context, audioPath: String): Double {
        val extractor = MediaExtractor()
        
        try {
            if (audioPath.startsWith("content://")) {
                val uri = Uri.parse(audioPath)
                val pfd = context.contentResolver.openFileDescriptor(uri, "r")
                if (pfd != null) {
                    extractor.setDataSource(pfd.fileDescriptor)
                    pfd.close()
                }
            } else {
                extractor.setDataSource(audioPath)
            }
            
            for (i in 0 until extractor.trackCount) {
                val format = extractor.getTrackFormat(i)
                val mime = format.getString(MediaFormat.KEY_MIME) ?: ""
                if (mime.startsWith("audio/")) {
                    if (format.containsKey(MediaFormat.KEY_DURATION)) {
                        return format.getLong(MediaFormat.KEY_DURATION) / 1000000.0
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "getDuration error: ${e.message}")
        } finally {
            extractor.release()
        }
        
        return 0.0
    }
}