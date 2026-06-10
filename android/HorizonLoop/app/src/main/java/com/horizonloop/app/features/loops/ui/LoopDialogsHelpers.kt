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
fun LoopDetailDialog(loop: Loop, onPlay: (Loop) -> Unit, onDelete: (Int) -> Unit, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp)).background(Surface).padding(22.dp)) {
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(loop.name, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Dark)
                Text("🗑️", fontSize = 18.sp, modifier = Modifier.clickable { onDelete(loop.id); onDismiss() })
                }
                val startSec = parseTimeToSeconds(loop.start)
                val endSec = parseTimeToSeconds(loop.end)
                Text("[${formatTimestamp(startSec)} to ${formatTimestamp(endSec)}]", fontSize = 15.sp, color = Mid)
                Text("Loop: ${loop.count} time(s)", fontSize = 15.sp, color = Mid)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.End)) {
                    TextButton(onClick = onDismiss) { Text("Close", color = Mid, fontSize = 13.sp, fontWeight = FontWeight.SemiBold) }
                    Button(
                        onClick = { onPlay(loop); onDismiss() },
                        colors = ButtonDefaults.buttonColors(containerColor = Mid, contentColor = Deep),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(AppIcons.Play, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Play", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}