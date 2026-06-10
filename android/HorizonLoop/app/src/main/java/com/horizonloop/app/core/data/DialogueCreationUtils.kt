package com.horizonloop.app.core.data

import com.horizonloop.app.core.domain.model.Dialogue
import com.horizonloop.app.core.data.WhisperWord

/**
 * Creates dialogue entries from transcript text by splitting into sentences.
 * Uses word-level timestamps from Whisper verbose_json for accurate start/end times.
 * Generates English and Bangla dialogue pairs with timestamps.
 */
fun createDialoguesFromTranscript(
    english: String,
    bangla: String,
    words: List<WhisperWord>? = null
): List<Dialogue> {
    val englishParts = english.split(" ").filter { it.isNotBlank() }
    val englishSentences = mutableListOf<SentenceWithTimestamp>()
    var currentSentenceWords = mutableListOf<WhisperWord?>()

    if (words != null) {
        // Use actual word timestamps from Whisper verbose_json
        for (wordObj in words) {
            currentSentenceWords.add(wordObj)
            if (wordObj.word.endsWith(".") || wordObj.word.endsWith("?") || wordObj.word.endsWith("!")) {
                if (currentSentenceWords.isNotEmpty()) {
                    val start = currentSentenceWords.first()!!.start
                    val end = currentSentenceWords.last()!!.end
                    val text = currentSentenceWords.filterNotNull().joinToString(" ") { it.word }
                    englishSentences.add(SentenceWithTimestamp(text.trim(), start, end))
                    currentSentenceWords = mutableListOf()
                }
            }
        }
        if (currentSentenceWords.isNotEmpty()) {
            val start = currentSentenceWords.first()!!.start
            val end = currentSentenceWords.last()!!.end
            val text = currentSentenceWords.filterNotNull().joinToString(" ") { it.word }
            englishSentences.add(SentenceWithTimestamp(text.trim(), start, end))
        }
    } else {
        // Fallback: split by punctuation without real timestamps
        var timeOffset = 0.0
        var currentSentence = StringBuilder()
        for (word in englishParts) {
            currentSentence.append(word).append(" ")
            if (word.endsWith(".") || word.endsWith("?") || word.endsWith("!")) {
                englishSentences.add(SentenceWithTimestamp(currentSentence.toString().trim(), timeOffset, timeOffset + 3.0))
                timeOffset += 3.0
                currentSentence = StringBuilder()
            }
        }
        if (currentSentence.isNotBlank()) {
            englishSentences.add(SentenceWithTimestamp(currentSentence.toString().trim(), timeOffset, timeOffset + 3.0))
        }
    }

    val banglaParts = bangla.split(" ").filter { it.isNotBlank() }
    val banglaSentences = mutableListOf<String>()
    var currentBangla = StringBuilder()

    for (word in banglaParts) {
        currentBangla.append(word).append(" ")
        if (word.endsWith(".") || word.endsWith("?") || word.endsWith("!") || word.contains("।")) {
            banglaSentences.add(currentBangla.toString().trim())
            currentBangla = StringBuilder()
        }
    }
    if (currentBangla.isNotBlank()) {
        banglaSentences.add(currentBangla.toString().trim())
    }

    return englishSentences.mapIndexed { index, sentence ->
        val time = formatTime(sentence.start)
        val bang = if (index < banglaSentences.size) banglaSentences[index] else ""
        Dialogue(
            id = index + 1,
            time = time,
            english = sentence.text,
            bangla = bang,
            startTime = sentence.start,
            endTime = sentence.end
        )
    }
}

private data class SentenceWithTimestamp(
    val text: String,
    val start: Double,
    val end: Double
)