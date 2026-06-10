package com.horizonloop.app.features.player.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.horizonloop.app.core.ui.theme.Dark
import com.horizonloop.app.core.ui.theme.Deep
import com.horizonloop.app.core.ui.theme.Mid
import com.horizonloop.app.core.ui.theme.Muted
import com.horizonloop.app.core.ui.theme.Surface

/** API key provider type detected from key prefix */
private enum class ApiProvider { GROQ, ANTHROPIC, GOOGLE, OPENAI, UNKNOWN }

/** Detects which AI provider an API key belongs to based on its prefix */
private fun detectProvider(apiKey: String): ApiProvider = when {
    apiKey.startsWith("gsk_") -> ApiProvider.GROQ
    apiKey.startsWith("sk-ant") -> ApiProvider.ANTHROPIC
    apiKey.startsWith("AIza") -> ApiProvider.GOOGLE
    apiKey.startsWith("sk-") -> ApiProvider.OPENAI
    else -> ApiProvider.UNKNOWN
}

/** Categorizes a Groq model ID into a functional type */
private fun categorizeGroqModel(modelId: String): ModelCategory = when {
    modelId.contains("whisper", ignoreCase = true) -> ModelCategory.TRANSCRIPTION
    modelId.contains("deepseek-r1") -> ModelCategory.OTHER // reasoning model — not for translation
    else -> ModelCategory.LLM_TRANSLATION
}

private enum class ModelCategory { TRANSCRIPTION, LLM_TRANSLATION, OTHER }

/** Groups Groq model IDs into transcription and translation buckets */
internal data class GroqModelGroups(val transcription: List<String>, val translation: List<String>)

internal fun groupGroqModels(modelIds: List<String>): GroqModelGroups {
    val transcription = mutableListOf<String>()
    val translation = mutableListOf<String>()
    for (id in modelIds) {
        when (categorizeGroqModel(id)) {
            ModelCategory.TRANSCRIPTION -> transcription.add(id)
            ModelCategory.LLM_TRANSLATION -> translation.add(id)
            ModelCategory.OTHER -> { /* skip — reasoning/thinking models not suitable for translation */ }
        }
    }
    return GroqModelGroups(transcription, translation)
}

/** Reusable dropdown model selector for both transcription and translation */
@Composable
internal fun ModelSelector(
    label: String,
    selectedModel: String,
    models: List<String>,
    expanded: Boolean,
    onExpandChange: (Boolean) -> Unit,
    onModelSelected: (String) -> Unit
) {
    Box {
        OutlinedTextField(
            value = selectedModel,
            onValueChange = { },
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,                    label = { Text(label, fontSize = 11.sp, color = Mid) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Dark, unfocusedTextColor = Dark,
                focusedBorderColor = Mid, unfocusedBorderColor = Muted
            )
        )
        Box(modifier = Modifier.matchParentSize().clickable { onExpandChange(true) })
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandChange(false) },
            modifier = Modifier.background(Surface)
        ) {
            models.forEach { model ->
                DropdownMenuItem(
                    text = { Text(model, color = Dark, fontSize = 12.sp) },
                    onClick = { onModelSelected(model); onExpandChange(false) }
                )
            }
        }
    }
}