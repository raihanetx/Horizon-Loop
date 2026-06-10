package com.horizonloop.app.features.translation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.horizonloop.app.core.domain.model.StepStatus
import com.horizonloop.app.core.domain.model.TranslationStep
import com.horizonloop.app.core.ui.theme.Dark
import com.horizonloop.app.core.ui.theme.Deep
import com.horizonloop.app.core.ui.theme.Mid
import com.horizonloop.app.core.ui.theme.Muted
import com.horizonloop.app.core.ui.theme.Surface
import com.horizonloop.app.core.ui.theme.White12
import kotlinx.coroutines.launch

@Composable
fun TranslationDebugPanel(
    steps: List<TranslationStep>,
    log: List<String>,
    isVisible: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!isVisible) return

    var isExpanded by remember { mutableStateOf(true) }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(log.size) {
        if (log.isNotEmpty()) {
            coroutineScope.launch { listState.animateScrollToItem(log.size - 1) }
        }
    }

    Column(modifier = modifier.fillMaxWidth().padding(16.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp))
                .background(Muted)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Translation Debug", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Dark)
                Spacer(modifier = Modifier.width(10.dp))
                Text(text = "[${steps.count { it.status == StepStatus.COMPLETED }}/${steps.size}]", fontSize = 12.sp, color = Mid)
            }
            Row {
                IconButton(onClick = { isExpanded = !isExpanded }, modifier = Modifier.size(36.dp)) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = if (isExpanded) "Collapse" else "Expand",
                        tint = Mid, modifier = Modifier.size(24.dp)
                    )
                }
                IconButton(onClick = onDismiss, modifier = Modifier.size(36.dp)) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Dismiss", tint = Mid, modifier = Modifier.size(24.dp))
                }
            }
        }

        if (isExpanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp))
                    .background(Deep)
                    .border(1.dp, Muted.copy(alpha = 0.3f), RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "PROGRESS:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Mid)
                    Spacer(modifier = Modifier.height(12.dp))
                    steps.forEach { step ->
                        StepRow(step = step)
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                }
                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Muted.copy(alpha = 0.2f)))
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "LOG:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Mid)
                    Spacer(modifier = Modifier.height(12.dp))
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 240.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Dark.copy(alpha = 0.5f))
                            .padding(10.dp)
                    ) {
                        items(log) { entry ->
                            Text(
                                text = entry,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                color = when {
                                    entry.contains("ERROR") -> Surface
                                    entry.startsWith("[+]") -> Muted
                                    entry.startsWith("[STEP") -> Dark
                                    else -> White12
                                },
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}