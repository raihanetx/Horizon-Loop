package com.horizonloop.app.core.data

import java.text.SimpleDateFormat
import java.util.Date

fun buildTranslationLog(existing: List<String>, message: String): List<String> {
    val timestamp = SimpleDateFormat("HH:mm:ss.SSS").format(Date())
    val logEntry = "[$timestamp] $message"
    android.util.Log.d("TranslationDebug", logEntry)
    return existing + logEntry
}

fun addTranslationStep(existing: List<com.horizonloop.app.core.domain.model.TranslationStep>, step: com.horizonloop.app.core.domain.model.TranslationStep): List<com.horizonloop.app.core.domain.model.TranslationStep> {
    return existing + step
}

fun updateLastStepStatus(
    existing: List<com.horizonloop.app.core.domain.model.TranslationStep>,
    status: com.horizonloop.app.core.domain.model.StepStatus,
    detail: String? = null
): List<com.horizonloop.app.core.domain.model.TranslationStep> {
    if (existing.isEmpty()) return existing
    val lastStep = existing.last()
    return existing.dropLast(1) + lastStep.copy(status = status, detail = detail)
}