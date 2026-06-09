package com.horizonloop.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.horizonloop.app.ui.theme.Dark
import com.horizonloop.app.ui.theme.Deep
import com.horizonloop.app.ui.theme.Mid
import com.horizonloop.app.ui.theme.Surface

/**
 * LoopComponents_AddDialog.kt
 * Add Loop dialog composable
 */

@Composable
fun AddLoopDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, String, Int) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var start by remember { mutableStateOf("") }
    var end by remember { mutableStateOf("") }
    var count by remember { mutableStateOf("1") }
    var previewLoop by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    var showPreview by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
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
                Text(text = "Add Loop", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Dark)

                if (showPreview && previewLoop.isNotEmpty()) {
                    LoopPreviewBox(previewLoop)
                }

                AddLoopFormFields(name, start, end, count, { name = it }, { start = it }, { end = it }, { count = it })

                AddLoopDialogButtons(
                    onPreview = {
                        if (name.isNotBlank()) {
                            previewLoop = mapOf("name" to name, "start" to start.ifBlank { "—" }, "end" to end.ifBlank { "—" }, "count" to count.ifBlank { "1" })
                            showPreview = true
                        }
                    },
                    onCancel = onDismiss,
                    onSave = {
                        if (name.isNotBlank()) {
                            onSave(name.trim(), start, end, count.toIntOrNull() ?: 1)
                            onDismiss()
                        }
                    }
                )
            }
        }
    }
}
