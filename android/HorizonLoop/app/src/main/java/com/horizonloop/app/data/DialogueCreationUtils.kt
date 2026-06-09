package com.horizonloop.app.data

/**
 * Creates dialogue entries from transcript text by splitting into sentences.
 * Generates English and Bangla dialogue pairs with timestamps.
 */
fun createDialoguesFromTranscript(english: String, bangla: String): List<Dialogue> {
    // Split text by whitespace and filter for sentences with punctuation
    val englishParts = english.split(" ").filter { it.isNotBlank() }
    val englishSentences = mutableListOf<String>()
    var currentSentence = StringBuilder()

    for (word in englishParts) {
        currentSentence.append(word).append(" ")
        if (word.endsWith(".") || word.endsWith("?") || word.endsWith("!")) {
            englishSentences.add(currentSentence.toString().trim())
            currentSentence = StringBuilder()
        }
    }
    if (currentSentence.isNotBlank()) {
        englishSentences.add(currentSentence.toString().trim())
    }

    // Split bangla similarly
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

    // Create dialogues with timestamps
    var timeOffset = 0.0
    return englishSentences.mapIndexed { index, eng ->
        val time = formatTime(timeOffset)
        val bang = if (index < banglaSentences.size) banglaSentences[index] else ""
        timeOffset += 3.0
        Dialogue(
            id = index + 1,
            time = time,
            english = eng,
            bangla = bang
        )
    }
}