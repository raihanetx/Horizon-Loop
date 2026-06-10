package com.horizonloop.app.features.player.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
import com.horizonloop.app.core.data.GroqClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** Settings dialog — accepts API key, fetches available Groq models, lets user pick voice-to-text and translation models */
@Composable
fun SettingsDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, String) -> Unit,
    initialApiKey: String = "",
    initialTranscriptionModel: String = "whisper-1",
    initialTranslationModel: String = "llama-3.3-70b-versatile",
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    var apiKey by remember { mutableStateOf(initialApiKey) }
    var transcriptionModel by remember { mutableStateOf(initialTranscriptionModel) }
    var translationModel by remember { mutableStateOf(initialTranslationModel) }
    var transcriptionExpanded by remember { mutableStateOf(false) }
    var translationExpanded by remember { mutableStateOf(false) }
    var isDetecting by remember { mutableStateOf(false) }
    var detectionStatus by remember { mutableStateOf("") }
    var availableTranscriptionModels by remember { mutableStateOf(listOf(initialTranscriptionModel)) }
    var availableTranslationModels by remember { mutableStateOf(listOf(initialTranslationModel)) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Surface)
            .clip(RoundedCornerShape(20.dp))
            .padding(24.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {

            // Header row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Settings", fontWeight = FontWeight.SemiBold, color = Dark, fontSize = 20.sp)
                TextButton(onClick = onDismiss) { Text("×", color = Mid, fontSize = 24.sp) }
            }

            // API Key input
            OutlinedTextField(
                value = apiKey, onValueChange = { apiKey = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("API Key", fontSize = 13.sp, color = Mid) },
                placeholder = { Text("Enter your API key...", color = Mid) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Dark, unfocusedTextColor = Dark,
                    focusedBorderColor = Mid, unfocusedBorderColor = Muted, cursorColor = Dark
                )
            )

            // Fetch Available Models button — calls real Groq /models API
            Button(
                onClick = {
                    if (apiKey.isNotBlank()) {
                        isDetecting = true
                        detectionStatus = ""
                        scope.launch {
                            try {
                                val response = withContext(Dispatchers.IO) {
                                    GroqClient.apiService.listModels("Bearer $apiKey")
                                }
                                if (response.isSuccessful) {
                                    val modelIds = response.body()?.data?.map { it.id } ?: emptyList()
                                    val grouped = groupGroqModels(modelIds)
                                    availableTranscriptionModels = grouped.transcription.ifEmpty { listOf("whisper-1") }
                                    availableTranslationModels = grouped.translation.ifEmpty { listOf("llama-3.3-70b-versatile") }
                                    transcriptionModel = availableTranscriptionModels.first()
                                    translationModel = availableTranslationModels.first()
                                    isDetecting = false
                                    detectionStatus = "✓ Groq — ${availableTranscriptionModels.size} voice models, ${availableTranslationModels.size} translation models"
                                } else {
                                    isDetecting = false
                                    detectionStatus = "Failed to fetch models: ${response.code()}"
                                }
                            } catch (e: Exception) {
                                isDetecting = false
                                detectionStatus = "Could not reach Groq — check your internet connection"
                            }
                        }
                    } else {
                        detectionStatus = "Enter API key first"
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Muted, contentColor = Dark),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isDetecting) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.5.dp, color = Dark)
                } else {
                    Text("Fetch Available Models", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            // Status message
            if (detectionStatus.isNotBlank()) {
                Text(detectionStatus, fontSize = 12.sp, color = Mid)
            }

            // Voice-to-Text model selector
            if (availableTranscriptionModels.isNotEmpty()) {
                ModelSelector(
                    label = "Voice-to-Text Model",
                    selectedModel = transcriptionModel,
                    models = availableTranscriptionModels,
                    expanded = transcriptionExpanded,
                    onExpandChange = { transcriptionExpanded = it },
                    onModelSelected = { transcriptionModel = it }
                )
            }

            // Translation model selector
            if (availableTranslationModels.isNotEmpty()) {
                ModelSelector(
                    label = "Translation Model",
                    selectedModel = translationModel,
                    models = availableTranslationModels,
                    expanded = translationExpanded,
                    onExpandChange = { translationExpanded = it },
                    onModelSelected = { translationModel = it }
                )
            }

            // Action buttons
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                TextButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                    Text("Cancel", color = Mid, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                }
                Button(
                    onClick = { onSave(apiKey, translationModel, transcriptionModel); onDismiss() },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Mid, contentColor = Deep)
                ) {
                    Text("Save", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}