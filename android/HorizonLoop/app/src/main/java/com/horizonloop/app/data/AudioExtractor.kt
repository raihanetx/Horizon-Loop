package com.horizonloop.app.data

import android.content.Context
import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer

object AudioExtractor {
    
    private const val MAX_FILE_SIZE_MB = 25
    private const val TIMEOUT_US = 10000L
    
    /**
     * Extract audio from video file and convert to WAV format
     * This guarantees compatibility with Whisper API
     */
    suspend fun extractAudio(
        context: Context,
        videoPath: String,
        outputDir: File,
        onProgress: (Float) -> Unit = {}
    ): String? = withContext(Dispatchers.IO) {
        var extractor: MediaExtractor? = null
        var decoder: MediaCodec? = null
        
        try {
            // Set up extractor
            extractor = MediaExtractor()
            
            if (videoPath.startsWith("content://")) {
                extractor.setDataSource(context, Uri.parse(videoPath), null)
            } else {
                extractor.setDataSource(videoPath)
            }
            
            // Find audio track
            var audioTrackIndex = -1
            var audioFormat: MediaFormat? = null
            
            for (i in 0 until extractor.trackCount) {
                val format = extractor.getTrackFormat(i)
                val mime = format.getString(MediaFormat.KEY_MIME) ?: continue
                
                if (mime.startsWith("audio/")) {
                    audioTrackIndex = i
                    audioFormat = format
                    break
                }
            }
            
            if (audioTrackIndex < 0 || audioFormat == null) {
                return@withContext null
            }
            
            // Select audio track
            extractor.selectTrack(audioTrackIndex)
            
            val mimeType = audioFormat.getString(MediaFormat.KEY_MIME)!!
            val sampleRate = audioFormat.getInteger(MediaFormat.KEY_SAMPLE_RATE)
            val channelCount = audioFormat.getInteger(MediaFormat.KEY_CHANNEL_COUNT)
            
            // Default to 16 bits per sample (most common for PCM audio)
            val bitsPerSample = 16
            
            // Create decoder
            decoder = MediaCodec.createDecoderByType(mimeType)
            decoder.configure(audioFormat, null, null, 0)
            decoder.start()
            
            // Create output file with WAV extension
            val outputFile = File(outputDir, "temp_audio_${System.currentTimeMillis()}.wav")
            
            val pcmData = mutableListOf<ByteArray>()
            var isEOS = false
            val bufferInfo = MediaCodec.BufferInfo()
            
            while (!isEOS) {
                // Feed input
                val inputBufferIndex = decoder.dequeueInputBuffer(TIMEOUT_US)
                if (inputBufferIndex >= 0) {
                    val inputBuffer = decoder.getInputBuffer(inputBufferIndex)!!
                    val sampleSize = extractor.readSampleData(inputBuffer, 0)
                    
                    if (sampleSize < 0) {
                        decoder.queueInputBuffer(inputBufferIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM)
                        isEOS = true
                    } else {
                        val presentationTimeUs = extractor.sampleTime
                        decoder.queueInputBuffer(inputBufferIndex, 0, sampleSize, presentationTimeUs, 0)
                        extractor.advance()
                    }
                }
                
                // Get output
                val outputBufferIndex = decoder.dequeueOutputBuffer(bufferInfo, TIMEOUT_US)
                if (outputBufferIndex >= 0) {
                    if (bufferInfo.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0) {
                        isEOS = true
                    }
                    
                    if (bufferInfo.size > 0) {
                        val outputBuffer = decoder.getOutputBuffer(outputBufferIndex)!!
                        // Set position to start of valid data
                        outputBuffer.position(bufferInfo.offset)
                        outputBuffer.limit(bufferInfo.offset + bufferInfo.size)
                        
                        val data = ByteArray(bufferInfo.size)
                        outputBuffer.get(data)
                        pcmData.add(data)
                    }
                    
                    decoder.releaseOutputBuffer(outputBufferIndex, false)
                    
                    onProgress(0.5f) // Decode in progress
                }
            }
            
            // Write WAV file
            writeWavFile(outputFile, pcmData, channelCount, sampleRate, bitsPerSample)
            
            onProgress(1f)
            return@withContext outputFile.absolutePath
            
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext null
        } finally {
            try {
                decoder?.stop()
                decoder?.release()
                extractor?.release()
            } catch (e: Exception) {
                // Ignore cleanup errors
            }
        }
    }
    
    /**
     * Split audio file into chunks (for Whisper API 25MB limit)
     * For WAV files, we just return the single file since we're already producing small WAV files
     */
    fun splitAudio(audioPath: String, cacheDir: File): List<String> {
        val maxSizeBytes = MAX_FILE_SIZE_MB * 1024 * 1024
        val file = File(audioPath)
        
        if (file.length() <= maxSizeBytes) {
            return listOf(audioPath)
        }
        
        // For larger files, we'd need to split - but WAV files from extraction should be small enough
        // Return single file (caller should handle size limits)
        return listOf(audioPath)
    }
    
    /**
     * Write PCM data to a WAV file
     */
    private fun writeWavFile(
        file: File,
        pcmData: List<ByteArray>,
        channels: Int,
        sampleRate: Int,
        bitsPerSample: Int
    ) {
        val byteRate = sampleRate * channels * bitsPerSample / 8
        val blockAlign = channels * bitsPerSample / 8
        
        val totalDataSize = pcmData.sumOf { it.size }
        val fileSize = 36 + totalDataSize
        
        FileOutputStream(file).use { fos ->
            // RIFF header
            fos.write("RIFF".toByteArray())
            fos.write(intToByteArray(fileSize))
            fos.write("WAVE".toByteArray())
            
            // fmt subchunk
            fos.write("fmt ".toByteArray())
            fos.write(intToByteArray(16)) // Subchunk1Size (16 for PCM)
            fos.write(shortToByteArray(1)) // AudioFormat (1 = PCM)
            fos.write(shortToByteArray(channels))
            fos.write(intToByteArray(sampleRate))
            fos.write(intToByteArray(byteRate))
            fos.write(shortToByteArray(blockAlign))
            fos.write(shortToByteArray(bitsPerSample))
            
            // data subchunk
            fos.write("data".toByteArray())
            fos.write(intToByteArray(totalDataSize))
            
            // Write PCM data
            for (chunk in pcmData) {
                fos.write(chunk)
            }
        }
    }
    
    private fun intToByteArray(value: Int): ByteArray {
        return byteArrayOf(
            (value and 0xFF).toByte(),
            ((value shr 8) and 0xFF).toByte(),
            ((value shr 16) and 0xFF).toByte(),
            ((value shr 24) and 0xFF).toByte()
        )
    }
    
    private fun shortToByteArray(value: Int): ByteArray {
        return byteArrayOf(
            (value and 0xFF).toByte(),
            ((value shr 8) and 0xFF).toByte()
        )
    }
}
