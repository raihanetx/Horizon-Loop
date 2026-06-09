package com.horizonloop.app.data

import java.text.SimpleDateFormat
import java.util.Date

/**
 * Accumulates a translation log with timestamps.
 * Attach this as a property in the class that needs it:
 *   private val _translationLog = mutableStateOf<List<String>>(emptyList())
 *   val translationLog: List<String> get() = _translationLog
 */
fun buildTranslationLog(existing: List<String>, message: String): List<String> {
    val timestamp = SimpleDateFormat("HH:mm:ss.SSS").format(Date())
    val logEntry = "[$timestamp] $message"
    android.util.Log.d("TranslationDebug", logEntry)
    return existing + logEntry
}

/**
 * Adds a step to a translation steps list.
 */
fun addTranslationStep(existing: List<TranslationStep>, step: TranslationStep): List<TranslationStep> {
    return existing + step
}

/**
 * Updates the status of the last step in the list.
 */
fun updateLastStepStatus(
    existing: List<TranslationStep>,
    status: StepStatus,
    detail: String? = null
): List<TranslationStep> {
    if (existing.isEmpty()) return existing
    val lastStep = existing.last()
    return existing.dropLast(1) + lastStep.copy(status = status, detail = detail)
}