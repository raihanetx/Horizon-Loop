package com.horizonloop.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.horizonloop.app.data.Loop
import com.horizonloop.app.ui.theme.Dark
import com.horizonloop.app.ui.theme.Deep
import com.horizonloop.app.ui.theme.Mid

/**
 * LoopComponents_Tab.kt
 * LoopsTab composable for displaying loops
 */

@Composable
fun LoopsTab(loops: List<Loop>, onAddLoop: (String, String, String, Int) -> Unit, onDeleteLoop: (Int) -> Unit, onPlayLoop: (Loop) -> Unit, modifier: Modifier = Modifier) {
    if (loops.isEmpty()) {
        Box(modifier = modifier.fillMaxSize().background(Deep), contentAlignment = Alignment.Center) {
            Text("No loops yet. Create one to get started.", fontSize = 14.sp, color = Mid)
        }
    } else {
        LazyColumn(modifier = modifier.fillMaxSize().background(Deep).padding(16.dp)) {
            items(loops, key = { it.id }) { loop ->
                LoopCard(loop = loop, onPlay = { onPlayLoop(loop) }, onDelete = { onDeleteLoop(loop.id) })
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun LoopCard(loop: Loop, onPlay: () -> Unit, onDelete: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(Dark).padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(1f)) {
            Text(loop.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Deep)
            Text("${loop.start} - ${loop.end}", fontSize = 12.sp, color = Mid)
        }
        Text("▶", fontSize = 18.sp, color = Mid, modifier = Modifier.clickable { onPlay() }.padding(8.dp))
        Text("✕", fontSize = 18.sp, color = Mid, modifier = Modifier.clickable { onDelete() }.padding(8.dp))
    }
}
