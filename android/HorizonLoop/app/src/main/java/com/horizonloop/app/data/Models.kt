package com.horizonloop.app.data

data class Audio(
    val id: Int,
    val title: String,
    val size: String,
    val subtitle: Boolean,
    val pin: Boolean,
    val bitrate: String,
    val duration: String,
    val durationSec: Double,
    val filePath: String = "",  // Path to the actual audio/video file for extraction
    val mimeType: String = "",   // MIME type (video/* or audio/*)
    val contentUri: String = ""  // Content URI for MediaStore access
)

data class Note(
    val id: Int,
    val text: String,
    val date: String
)

data class Loop(
    val id: Int,
    val name: String,
    val start: String,
    val end: String,
    val count: Int = 1
)

data class Dialogue(
    val id: Int,
    val time: String,
    val english: String,
    val bangla: String
)

enum class ActiveTab(val value: String) {
    CLEAN("clean"),
    SAVE("save"),
    SPEED("speed"),
    LOOP("loop"),
    NOTES("notes")
}

enum class FilterType {
    ALL, SIZE_DESC, SIZE_ASC, SUBTITLE_YES, SUBTITLE_NO, PINNED
}

enum class StepStatus {
    PENDING, IN_PROGRESS, COMPLETED, FAILED
}

data class TranslationStep(
    val stepNumber: Int,
    val title: String,
    val status: StepStatus = StepStatus.PENDING,
    val detail: String? = null,
    val icon: String = "→"
)