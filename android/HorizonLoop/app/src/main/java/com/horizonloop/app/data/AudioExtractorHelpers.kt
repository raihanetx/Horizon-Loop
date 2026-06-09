package com.horizonloop.app.data

import android.content.Context
import android.media.MediaExtractor
import android.media.MediaFormat
import android.net.Uri
import android.util.Log
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

/**
 * Audio file splitting and duration utilities.
 * Extracted from AudioExtractor to keep each file under 200 lines.
 */
object AudioExtractorHelpers {
    private const val TAG = "AudioExtractorHelpers"

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
                chunks.add(audioPath)
                return chunks
            }

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