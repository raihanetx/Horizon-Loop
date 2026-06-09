package com.horizonloop.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
import com.horizonloop.app.data.parseTimeToSeconds
import com.horizonloop.app.data.formatTimestamp
import com.horizonloop.app.ui.theme.Dark
import com.horizonloop.app.ui.theme.Deep
import com.horizonloop.app.ui.theme.Mid
import com.horizonloop.app.ui.theme.Muted
import com.horizonloop.app.ui.theme.Surface
import com.horizonloop.app.ui.components.LoopDetailDialog
import com.horizonloop.app.ui.components.LoopAddDialog

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
    onDeleteLoop: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }
    var selectedLoop by remember { mutableStateOf<Loop?>(null) }

    Box(modifier = modifier.fillMaxSize()) {
        if (loops.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                Text("No loops yet.", fontSize = 12.sp, color = Mid)
            }
        } else {
            Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                loops.forEach { loop -> LoopCard(loop = loop, onClick = { selectedLoop = loop }, onPlay = { onPlayLoop(loop) }) }
            }
        }
        FloatingActionButton(
            onClick = { showDialog = true },
            modifier = Modifier.align(Alignment.BottomEnd).padding(bottom = 34.dp, end = 16.dp).size(48.dp),
            shape = androidx.compose.foundation.shape.CircleShape,
            containerColor = Muted,
            contentColor = Dark
        ) { Icon(AppIcons.Add, contentDescription = "Add Loop", modifier = Modifier.size(20.dp)) }
    }

    if (selectedLoop != null) {
        LoopDetailDialog(loop = selectedLoop!!, onPlay = { onPlayLoop(selectedLoop!!); selectedLoop = null }, onDelete = { onDeleteLoop(selectedLoop!!.id); selectedLoop = null }, onDismiss = { selectedLoop = null })
    }
    if (showDialog) {
        LoopAddDialog(onAdd = { name, start, end, count -> onAddLoop(name, start, end, count); showDialog = false }, onDismiss = { showDialog = false })
    }
}
