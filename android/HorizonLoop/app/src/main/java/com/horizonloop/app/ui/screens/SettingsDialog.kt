package com.horizonloop.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.horizonloop.app.data.ApiKeyStorage
import com.horizonloop.app.ui.theme.Dark
import com.horizonloop.app.ui.theme.Deep
import com.horizonloop.app.ui.theme.Mid
import com.horizonloop.app.ui.theme.Muted
import com.horizonloop.app.ui.theme.Surface

@Composable
fun SettingsDialog(
    apiKey: String,
    selectedSttModel: String,
    selectedLlmModel: String,
    onApiKeyChange: (String) -> Unit,
    onSttModelChange: (String) -> Unit,
    onLlmModelChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {
    var sttExpanded by remember { mutableStateOf(false) }
    var llmExpanded by remember { mutableStateOf(false) }
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Surface)
            .clip(RoundedCornerShape(16.dp))
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Settings", fontWeight = FontWeight.SemiBold, color = Dark, fontSize = 16.sp)
                TextButton(onClick = onDismiss) { Text("×", color = Mid, fontSize = 20.sp) }
            }
            
            // API Key Input
            Text("Groq API Key", fontSize = 11.sp, color = Mid, fontWeight = FontWeight.SemiBold, letterSpacing = 1.sp)
            OutlinedTextField(
                value = apiKey,
                onValueChange = onApiKeyChange,
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(fontSize = 14.sp, color = Dark),
                placeholder = { Text("gsk_...", color = Mid, fontSize = 14.sp) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Dark,
                    unfocusedTextColor = Dark,
                    focusedBorderColor = Mid,
                    unfocusedBorderColor = Muted,
                    cursorColor = Dark
                )
            )
            
            // STT Model Selection (Audio to Text)
            Text("Speech-to-Text Model (Audio → English)", fontSize = 11.sp, color = Mid, fontWeight = FontWeight.SemiBold, letterSpacing = 1.sp)
            Box {
                OutlinedTextField(
                    value = selectedSttModel,
                    onValueChange = { },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    textStyle = TextStyle(fontSize = 14.sp, color = Dark),
                    trailingIcon = {
                        Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown", tint = Mid)
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Dark,
                        unfocusedTextColor = Dark,
                        focusedBorderColor = Mid,
                        unfocusedBorderColor = Muted
                    )
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable { sttExpanded = true }
                )
                DropdownMenu(
                    expanded = sttExpanded,
                    onDismissRequest = { sttExpanded = false },
                    modifier = Modifier.background(Surface)
                ) {
                    ApiKeyStorage.STT_MODELS.forEach { model ->
                        DropdownMenuItem(
                            text = { 
                                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                                    Text(model, color = Dark, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                }
                            },
                            onClick = { 
                                onSttModelChange(model)
                                sttExpanded = false 
                            }
                        )
                    }
                }
            }
            
            // LLM Model Selection (Translation to Bangla)
            Text("Translation Model (English → Bangla)", fontSize = 11.sp, color = Mid, fontWeight = FontWeight.SemiBold, letterSpacing = 1.sp)
            Box {
                OutlinedTextField(
                    value = selectedLlmModel,
                    onValueChange = { },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    textStyle = TextStyle(fontSize = 14.sp, color = Dark),
                    trailingIcon = {
                        Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown", tint = Mid)
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Dark,
                        unfocusedTextColor = Dark,
                        focusedBorderColor = Mid,
                        unfocusedBorderColor = Muted
                    )
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable { llmExpanded = true }
                )
                DropdownMenu(
                    expanded = llmExpanded,
                    onDismissRequest = { llmExpanded = false },
                    modifier = Modifier.background(Surface)
                ) {
                    ApiKeyStorage.LLM_MODELS.forEach { model ->
                        DropdownMenuItem(
                            text = { 
                                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                                    Text(model, color = Dark, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                }
                            },
                            onClick = { 
                                onLlmModelChange(model)
                                llmExpanded = false 
                            }
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancel", color = Mid, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
                Button(
                    onClick = onSave,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Mid, contentColor = Deep)
                ) {
                    Text("Save", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
