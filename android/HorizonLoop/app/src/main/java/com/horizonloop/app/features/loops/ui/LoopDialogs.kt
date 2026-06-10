package com.horizonloop.app.features.loops.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.window.Dialog
import com.horizonloop.app.core.data.formatTimestamp
import com.horizonloop.app.core.data.parseTimeToSeconds
import com.horizonloop.app.core.domain.model.Loop
import com.horizonloop.app.core.ui.theme.AppIcons
import com.horizonloop.app.core.ui.theme.Dark
import com.horizonloop.app.core.ui.theme.Deep
import com.horizonloop.app.core.ui.theme.Mid
import com.horizonloop.app.core.ui.theme.Muted
import com.horizonloop.app.core.ui.theme.Surface

@Composable
fun LoopAddDialog(onAdd: (String, String, String, Int) -> Unit, onDismiss: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var start by remember { mutableStateOf("") }
    var end by remember { mutableStateOf("") }
    var count by remember { mutableStateOf("1") }
    var previewLoop by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    var showPreview by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = {
        onDismiss()
        name = ""
        start = ""
        end = ""
        count = "1"
        previewLoop = emptyMap()
        showPreview = false
    }) {
        Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp)).background(Surface).padding(24.dp)) {
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Add Loop", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Dark)

                if (showPreview && previewLoop.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Muted.copy(alpha = 0.15f))
                            .padding(20.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(previewLoop["name"] ?: "", fontSize = 15.sp, color = Dark, fontWeight = FontWeight.SemiBold)
                            Text(
                                "[${previewLoop["start"] ?: "—"} to ${previewLoop["end"] ?: "—"}] | Loop: ${previewLoop["count"] ?: "1"} time(s)",
                                fontSize = 13.sp,
                                color = Mid
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = name, onValueChange = { name = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("e.g. Chorus Section", color = Mid) },
                    label = { Text("Name", fontSize = 12.sp, color = Mid) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Dark, unfocusedTextColor = Dark,
                        focusedBorderColor = Mid, unfocusedBorderColor = Muted, cursorColor = Dark
                    ),
                    shape = RoundedCornerShape(14.dp)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    OutlinedTextField(
                        value = start, onValueChange = { start = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("e.g. 1:23", color = Mid) },
                        label = { Text("Start", fontSize = 12.sp, color = Mid) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Dark, unfocusedTextColor = Dark,
                            focusedBorderColor = Mid, unfocusedBorderColor = Muted, cursorColor = Dark
                        ),
                        shape = RoundedCornerShape(14.dp)
                    )
                    OutlinedTextField(
                        value = end, onValueChange = { end = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("e.g. 2:45", color = Mid) },
                        label = { Text("End", fontSize = 12.sp, color = Mid) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Dark, unfocusedTextColor = Dark,
                            focusedBorderColor = Mid, unfocusedBorderColor = Muted, cursorColor = Dark
                        ),
                        shape = RoundedCornerShape(14.dp)
                    )
                }

                OutlinedTextField(
                    value = count, onValueChange = { count = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("e.g. 3", color = Mid) },
                    label = { Text("Times", fontSize = 12.sp, color = Mid) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Dark, unfocusedTextColor = Dark,
                        focusedBorderColor = Mid, unfocusedBorderColor = Muted, cursorColor = Dark
                    ),
                    shape = RoundedCornerShape(14.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = {
                        if (name.isNotBlank()) {
                            previewLoop = mapOf(
                                "name" to name,
                                "start" to start.ifBlank { "—" },
                                "end" to end.ifBlank { "—" },
                                "count" to count.ifBlank { "1" }
                            )
                            showPreview = true
                        }
                    }) { Text("Preview", color = Mid, fontSize = 13.sp, fontWeight = FontWeight.SemiBold) }

                    TextButton(onClick = {
                        onDismiss()
                        name = ""
                        start = ""
                        end = ""
                        count = "1"
                        previewLoop = emptyMap()
                        showPreview = false
                    }) { Text("Cancel", color = Mid, fontSize = 13.sp, fontWeight = FontWeight.SemiBold) }

                    Button(
                        onClick = {
                            if (name.isNotBlank()) {
                                onAdd(name.trim(), start, end, count.toIntOrNull() ?: 1)
                                name = ""
                                start = ""
                                end = ""
                                count = "1"
                                previewLoop = emptyMap()
                                showPreview = false
                                onDismiss()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Mid, contentColor = Deep),
                        shape = RoundedCornerShape(14.dp)
                    ) { Text("Save", fontSize = 13.sp, fontWeight = FontWeight.SemiBold) }
                }
            }
        }
    }
}