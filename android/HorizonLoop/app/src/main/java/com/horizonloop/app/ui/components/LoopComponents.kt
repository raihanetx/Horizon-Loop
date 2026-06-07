package com.horizonloop.app.ui.components

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import com.horizonloop.app.ui.theme.AppIcons
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
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
import com.horizonloop.app.data.Loop
import com.horizonloop.app.ui.theme.Dark
import com.horizonloop.app.ui.theme.Deep
import com.horizonloop.app.ui.theme.Mid
import com.horizonloop.app.ui.theme.Muted
import com.horizonloop.app.ui.theme.Surface

fun parseTimeToSeconds(timeStr: String): Double {
    return try {
        val parts = timeStr.split(":")
        if (parts.size == 2) parts[0].toDouble() * 60 + parts[1].toDouble()
        else timeStr.toDoubleOrNull() ?: Double.NaN
    } catch (e: Exception) { Double.NaN }
}

fun formatTimestamp(seconds: Double): String {
    return if (seconds.isNaN()) "—" else {
        val m = (seconds / 60).toInt()
        val s = (seconds % 60).toInt()
        "$m:${if (s < 10) "0" else ""}$s"
    }
}

@Composable
fun LoopCard(
    loop: Loop,
    onClick: () -> Unit,
    onPlay: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Muted.copy(alpha = 0.04f))
            .clickable(onClick = onClick)
            .padding(18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = AppIcons.Play,
            contentDescription = null,
            tint = Mid,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(loop.name, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Dark)
            Spacer(modifier = Modifier.height(4.dp))
            val startSec = parseTimeToSeconds(loop.start)
            val endSec = parseTimeToSeconds(loop.end)
            val duration = if (!startSec.isNaN() && !endSec.isNaN()) kotlin.math.abs(endSec - startSec) else null
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (loop.start.isNotBlank() && loop.end.isNotBlank()) {
                    Text("Time:${formatTimestamp(startSec)}-${formatTimestamp(endSec)}", fontSize = 12.sp, color = Mid)
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(modifier = Modifier.width(1.dp).height(10.dp).background(Dark.copy(alpha = 0.3f)))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Total:${duration?.let { String.format("%.1f", it / 60) } ?: "—"} min", fontSize = 12.sp, color = Mid)
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(modifier = Modifier.width(1.dp).height(10.dp).background(Dark.copy(alpha = 0.3f)))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Loop:${loop.count} time", fontSize = 12.sp, color = Mid)
                } else {
                    Text("No time set", fontSize = 12.sp, color = Mid)
                }
            }
        }
    }
}

@Composable
fun LoopsTab(
    loops: List<Loop>,
    onAddLoop: (String, String, String, Int) -> Unit,
    onPlayLoop: (Loop) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var start by remember { mutableStateOf("") }
    var end by remember { mutableStateOf("") }
    var count by remember { mutableStateOf("1") }
    var previewLoop by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    var showPreview by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxWidth()) {
        if (loops.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                Text("No loops yet.", fontSize = 12.sp, color = Mid)
            }
        } else {
            Column(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                loops.forEach { loop ->
                    LoopCard(loop = loop, onClick = { }, onPlay = { onPlayLoop(loop) })
                }
            }
        }
        FloatingActionButton(
            onClick = { showDialog = true },
            modifier = Modifier.align(Alignment.BottomEnd).padding(bottom = 20.dp, end = 16.dp).size(48.dp),
            containerColor = Muted,
            contentColor = Dark
        ) {
            Icon(AppIcons.Add, contentDescription = "Add Loop", modifier = Modifier.size(20.dp))
        }
    }
    if (showDialog) {
        Dialog(onDismissRequest = { showDialog = false; name = ""; start = ""; end = ""; count = "1"; previewLoop = emptyMap(); showPreview = false }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Surface)
                    .padding(20.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Add Loop",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Dark
                    )

                    if (showPreview && previewLoop.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Muted.copy(alpha = 0.15f))
                                .padding(16.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = previewLoop["name"] ?: "",
                                    fontSize = 13.sp,
                                    color = Dark,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Time: ${previewLoop["start"] ?: "—"} - ${previewLoop["end"] ?: "—"} | Loop: ${previewLoop["count"] ?: "1"} time(s)",
                                    fontSize = 11.sp,
                                    color = Mid
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = name, onValueChange = { name = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("e.g. Chorus Section", color = Mid) },
                        label = { Text("Name", fontSize = 11.sp, color = Mid) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Dark, unfocusedTextColor = Dark,
                            focusedBorderColor = Mid, unfocusedBorderColor = Muted, cursorColor = Dark
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        OutlinedTextField(
                            value = start, onValueChange = { start = it },
                            modifier = Modifier.weight(1f),
                            placeholder = { Text("e.g. 1:23", color = Mid) },
                            label = { Text("Start", fontSize = 11.sp, color = Mid) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Dark, unfocusedTextColor = Dark,
                                focusedBorderColor = Mid, unfocusedBorderColor = Muted, cursorColor = Dark
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                        OutlinedTextField(
                            value = end, onValueChange = { end = it },
                            modifier = Modifier.weight(1f),
                            placeholder = { Text("e.g. 2:45", color = Mid) },
                            label = { Text("End", fontSize = 11.sp, color = Mid) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Dark, unfocusedTextColor = Dark,
                                focusedBorderColor = Mid, unfocusedBorderColor = Muted, cursorColor = Dark
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    OutlinedTextField(
                        value = count, onValueChange = { count = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("e.g. 3", color = Mid) },
                        label = { Text("Times", fontSize = 11.sp, color = Mid) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Dark, unfocusedTextColor = Dark,
                            focusedBorderColor = Mid, unfocusedBorderColor = Muted, cursorColor = Dark
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.End)
                    ) {
                        TextButton(onClick = { showDialog = false; name = ""; start = ""; end = ""; count = "1"; previewLoop = emptyMap(); showPreview = false }) {
                            Text("Cancel", color = Mid, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }
                        TextButton(
                            onClick = {
                                if (name.isNotBlank()) {
                                    previewLoop = mapOf(
                                        "name" to name,
                                        "start" to start.ifBlank { "—" },
                                        "end" to end.ifBlank { "—" },
                                        "count" to count.ifBlank { "1" }
                                    )
                                    showPreview = true
                                }
                            }
                        ) {
                            Text("Preview", color = Mid, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Button(
                            onClick = {
                                if (name.isNotBlank()) {
                                    onAddLoop(name.trim(), start, end, count.toIntOrNull() ?: 1)
                                    name = ""; start = ""; end = ""; count = "1"
                                    previewLoop = emptyMap()
                                    showPreview = false
                                    showDialog = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Mid, contentColor = Deep),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Save", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}
