package com.horizonloop.app.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.horizonloop.app.ui.theme.Deep
import com.horizonloop.app.ui.theme.Mid
import com.horizonloop.app.ui.theme.Muted
import com.horizonloop.app.ui.theme.Surface
import androidx.compose.foundation.layout.padding

/**
 * HomeScreen_Settings.kt
 * Settings dialog for API key and engine configuration
 */

@Composable
fun SettingsDialog(
    apiKey: String,
    selectedEngine: String,
    onApiKeyChange: (String) -> Unit,
    onEngineChange: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Settings", color = Deep, fontWeight = FontWeight.SemiBold) },
        text = {
            Column {
                Text("OpenAI API Key", fontSize = 11.sp, color = Mid, fontWeight = FontWeight.SemiBold, letterSpacing = 1.sp, modifier = Modifier.padding(bottom = 4.dp))
                OutlinedTextField(value = apiKey, onValueChange = onApiKeyChange, modifier = Modifier.fillMaxWidth(), textStyle = TextStyle(fontSize = 14.sp), placeholder = { Text("sk-...", color = Mid) }, singleLine = true)
                Spacer(modifier = Modifier.height(16.dp))
                Text("Translation Engine", fontSize = 11.sp, color = Mid, fontWeight = FontWeight.SemiBold, letterSpacing = 1.sp, modifier = Modifier.padding(bottom = 4.dp))
                Row {
                    Button(onClick = { onEngineChange("gpt-4o-mini") }, colors = if (selectedEngine == "gpt-4o-mini") ButtonDefaults.buttonColors(containerColor = Deep) else ButtonDefaults.buttonColors(containerColor = Muted)) { Text("GPT-4o Mini", fontSize = 12.sp) }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = { onEngineChange("gpt-4o") }, colors = if (selectedEngine == "gpt-4o") ButtonDefaults.buttonColors(containerColor = Deep) else ButtonDefaults.buttonColors(containerColor = Muted)) { Text("GPT-4o", fontSize = 12.sp) }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Save", color = Deep, fontWeight = FontWeight.SemiBold) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel", color = Mid) } },
        containerColor = Surface
    )
}
