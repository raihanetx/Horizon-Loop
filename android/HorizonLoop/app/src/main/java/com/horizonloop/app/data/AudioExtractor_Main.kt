package com.horizonloop.app.data

import android.content.Context
import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer

/**
 * AudioExtractor_Main.kt - Extracts audio from video file
 */

object AudioExtractor_Main {
    private const val MAX_FILE_SIZE_MB = 25
    private const val TIMEOUT_US = 10000L

    suspend fun extractAudio(context: Context, videoPath: String, outputDir: File, onProgress: (Float) -> Unit = {}): String? = withContext(Dispatchers.IO) {
        var extractor: MediaExtractor? = null; var decoder: MediaCodec? = null
        try {
            val outputFile = File(outputDir, "audio_${System.currentTimeMillis()}.wav")
            extractor = MediaExtractor(); extractor.setDataSource(videoPath)
            var audioTrackIndex = -1
            for (i in 0 until extractor.trackCount) { val format = extractor.getTrackFormat(i); val mime = format.getString(MediaFormat.KEY_MIME) ?: continue; if (mime.startsWith("audio/")) { audioTrackIndex = i; break } }
            if (audioTrackIndex == -1) { log("No audio track found"); return@withContext null }
            extractor.selectTrack(audioTrackIndex)
            val format = extractor.getTrackFormat(audioTrackIndex)
            val sampleRate = format.getInteger(MediaFormat.KEY_SAMPLE_RATE)
            val channelCount = format.getInteger(MediaFormat.KEY_CHANNEL_COUNT)
            decoder = MediaCodec.createDecoderByType(format.getString(MediaFormat.KEY_MIME)!!); decoder.configure(format, null, null, 0); decoder.start()
            val pcmData = mutableListOf<ByteArray>(); var isEOS = false
            while (!isEOS) {
                if (!isEOS) { val inputBufferIdx = decoder.dequeueInputBuffer(TIMEOUT_US); if (inputBufferIdx >= 0) { val inputBuf = decoder.getInputBuffer(inputBufferIdx)!!; val sampleSize = extractor.readSampleData(inputBuf, 0); if (sampleSize < 0) { decoder.queueInputBuffer(inputBufferIdx, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM); isEOS = true } else { decoder.queueInputBuffer(inputBufferIdx, 0, sampleSize, extractor.sampleTime, 0); extractor.advance() } } }
                val info = MediaCodec.BufferInfo(); val outIdx = decoder.dequeueOutputBuffer(info, TIMEOUT_US)
                if (outIdx >= 0) { val outBuf = decoder.getOutputBuffer(outIdx)!!; val data = ByteArray(info.size); outBuf.get(data); if (data.isNotEmpty()) pcmData.add(data); decoder.releaseOutputBuffer(outIdx, false); if (info.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0) break }
            }
            decoder.stop(); decoder.release(); extractor.release(); decoder = null; extractor = null
            if (pcmData.isEmpty()) return@withContext null
            FileOutputStream(outputFile).use { fos -> AudioExtractor_WavWriter.writeWavHeader(fos, sampleRate, channelCount, 16); for (chunk in pcmData) fos.write(chunk) }
            if (outputFile.length() / (1024.0 * 1024.0) > MAX_FILE_SIZE_MB) { outputFile.delete(); return@withContext null }
            outputFile.absolutePath
        } catch (e: Exception) { e.printStackTrace(); null } finally { try { decoder?.stop(); decoder?.release() } catch (e: Exception) { }; try { extractor?.release() } catch (e: Exception) { } }
    }

    private fun log(msg: String) { android.util.Log.d("AudioExtractor", msg) }

    fun splitAudio(audioPath: String, outputDir: File): List<String> {
        val file = File(audioPath); if (file.length() / (1024.0 * 1024.0) <= MAX_FILE_SIZE_MB) return listOf(audioPath)
        return listOf(audioPath)
    }
}
