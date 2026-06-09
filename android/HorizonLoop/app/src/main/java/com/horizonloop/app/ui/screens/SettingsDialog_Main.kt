package com.horizonloop.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.horizonloop.app.ui.theme.Dark
import com.horizonloop.app.ui.theme.Deep
import com.horizonloop.app.ui.theme.Mid

/**
 * SettingsDialog_Main.kt
 * Settings dialog composable
 */

@Composable
fun SettingsDialog(onDismiss: () -> Unit, onSave: (String, String) -> Unit, modifier: Modifier = Modifier) {
    var groqKey by remember { mutableStateOf("") }
    var whisperModel by remember { mutableStateOf("whisper-1") }

    Box(modifier = modifier.fillMaxSize().clip(RoundedCornerShape(16.dp)).background(Dark).padding(24.dp)) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text("Settings", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Deep)
            Spacer(modifier = Modifier.height(24.dp))
            OutlinedTextField(value = groqKey, onValueChange = { groqKey = it }, label = { Text("Groq API Key") },
                modifier = Modifier.fillMaxWidth(), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Deep, unfocusedBorderColor = Mid))
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(value = whisperModel, onValueChange = { whisperModel = it }, label = { Text("Whisper Model") },
                modifier = Modifier.fillMaxWidth(), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Deep, unfocusedBorderColor = Mid))
            Spacer(modifier = Modifier.height(32.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = Mid)) {
                    Text("Cancel", color = Deep)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(onClick = { onSave(groqKey, whisperModel) }, colors = ButtonDefaults.buttonColors(containerColor = Deep)) {
                    Text("Save", color = Dark)
                }
            }
        }
    }
}
