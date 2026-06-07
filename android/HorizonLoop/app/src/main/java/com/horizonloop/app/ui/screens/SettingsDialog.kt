package com.horizonloop.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.horizonloop.app.ui.theme.Dark
import com.horizonloop.app.ui.theme.Deep
import com.horizonloop.app.ui.theme.Mid
import com.horizonloop.app.ui.theme.Muted
import com.horizonloop.app.ui.theme.Surface

@Composable
fun SettingsDialog(
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var apiKey by remember { mutableStateOf("") }
    var engine by remember { mutableStateOf("gpt-4o") }
    var expanded by remember { mutableStateOf(false) }
    val engines = listOf("gpt-4o", "gpt-4o-mini", "gpt-3.5-turbo", "claude-3-opus", "claude-3-sonnet", "gemini-pro")

    Box(
        modifier = modifier
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
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                Text("Settings", fontWeight = FontWeight.SemiBold, color = Dark, fontSize = 16.sp)
                TextButton(onClick = onDismiss) { Text("×", color = Mid, fontSize = 20.sp) }
            }
            OutlinedTextField(
                value = apiKey,
                onValueChange = { apiKey = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("API Key", fontSize = 11.sp, color = Mid) },
                placeholder = { Text("Enter your API key...", color = Mid) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Dark,
                    unfocusedTextColor = Dark,
                    focusedBorderColor = Mid,
                    unfocusedBorderColor = Muted,
                    cursorColor = Dark
                )
            )
            Box {
                OutlinedTextField(
                    value = engine,
                    onValueChange = { },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    label = { Text("Engine", fontSize = 11.sp, color = Mid) },
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
                        .clickable { expanded = true }
                )
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.background(Surface)
                ) {
                    engines.forEach { e ->
                        DropdownMenuItem(
                            text = { Text(e, color = Dark) },
                            onClick = { engine = e; expanded = false }
                        )
                    }
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(
                    onClick = { onSave(apiKey, engine); onDismiss() },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Mid, contentColor = Deep)
                ) { Text("Save", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Mid, contentColor = Deep)
                ) { Text("Cancel", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }
            }
        }
    }
}