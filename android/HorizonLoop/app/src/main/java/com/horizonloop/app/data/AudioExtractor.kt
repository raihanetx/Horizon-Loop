package com.horizonloop.app.data

import android.content.Context
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMuxer
import android.net.Uri
import android.util.Log
import java.io.File

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
    
    fun splitAudio(audioPath: String, outputDir: File): List<String> =
        AudioExtractorHelpers.splitAudio(audioPath, outputDir)

    fun getDuration(context: Context, audioPath: String): Double =
        AudioExtractorHelpers.getDuration(context, audioPath)
}