package com.horizonloop.app.ui.components

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.horizonloop.app.data.TranslationStep
import com.horizonloop.app.ui.theme.*
import kotlinx.coroutines.launch

/**
 * TranslationDebugPanel_Main.kt
 * Translation debug panel main composable
 */

@Composable
fun TranslationDebugPanel(
    context: Context, steps: List<TranslationStep>, log: List<String>,
    isVisible: Boolean, onDismiss: () -> Unit, modifier: Modifier = Modifier
) {
    if (!isVisible) return
    var isExpanded by remember { mutableStateOf(true) }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(log.size) { if (log.isNotEmpty()) coroutineScope.launch { listState.animateScrollToItem(log.size - 1) } }
    Column(modifier = modifier.fillMaxWidth().padding(12.dp)) {
        TranslationDebugHeader(steps = steps, modifier = Modifier.fillMaxWidth())
        if (isExpanded) {
            Column(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp)).background(Deep).border(1.dp, Muted.copy(alpha = 0.3f), RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp))) {
                Column(modifier = Modifier.padding(12.dp)) { Text("PROGRESS:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Mid); Spacer(modifier = Modifier.height(8.dp)); steps.forEach { step -> Text("Step ${step.stepNumber}: ${step.title}", fontSize = 12.sp, color = Mid); Spacer(modifier = Modifier.height(4.dp)) } }
                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Muted.copy(alpha = 0.2f)))
                Column(modifier = Modifier.padding(12.dp)) { Text("LOG:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Mid); Spacer(modifier = Modifier.height(8.dp)); LazyColumn(state = listState, modifier = Modifier.fillMaxWidth().heightIn(max = 200.dp).clip(RoundedCornerShape(8.dp)).background(Dark.copy(alpha = 0.5f)).padding(8.dp)) { items(log) { entry -> Text(entry, fontSize = 9.sp, fontFamily = FontFamily.Monospace, color = White12, modifier = Modifier.padding(vertical = 1.dp)) } } }
            }
        }
    }
}
