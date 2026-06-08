package com.horizonloop.app.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
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
import androidx.compose.material.icons.filled.ContentCopy
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
import com.horizonloop.app.data.StepStatus
import com.horizonloop.app.data.TranslationStep
import com.horizonloop.app.ui.theme.Dark
import com.horizonloop.app.ui.theme.Deep
import com.horizonloop.app.ui.theme.Mid
import com.horizonloop.app.ui.theme.Muted
import com.horizonloop.app.ui.theme.Surface
import com.horizonloop.app.ui.theme.White12
import kotlinx.coroutines.launch

@Composable
fun TranslationDebugPanel(
    context: Context,
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
            coroutineScope.launch {
                listState.animateScrollToItem(log.size - 1)
            }
        }
    }
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                .background(Muted)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Translation Debug",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Dark
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "[${steps.count { it.status == StepStatus.COMPLETED }}/${steps.size}]",
                    fontSize = 10.sp,
                    color = Mid
                )
            }
            Row {
                IconButton(
                    onClick = { isExpanded = !isExpanded },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = if (isExpanded) "Collapse" else "Expand",
                        tint = Mid,
                        modifier = Modifier.size(20.dp)
                    )
                }
                IconButton(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Translation Log", log.joinToString("\n"))
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Copied to clipboard!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy Log",
                        tint = Mid,
                        modifier = Modifier.size(18.dp)
                    )
                }
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Dismiss",
                        tint = Mid,
                        modifier = Modifier.size(20.dp)
                    )
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
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "PROGRESS:",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Mid
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    steps.forEach { step ->
                        StepRow(step = step)
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Muted.copy(alpha = 0.2f))
                )
                
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "LOG:",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Mid
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 200.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Dark.copy(alpha = 0.5f))
                            .padding(8.dp)
                    ) {
                        items(log) { entry ->
                            Text(
                                text = entry,
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                color = when {
                                    entry.contains("ERROR") -> Surface
                                    entry.startsWith("[+]") -> Muted
                                    entry.startsWith("[STEP") -> Dark
                                    else -> White12
                                },
                                modifier = Modifier.padding(vertical = 1.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StepRow(step: TranslationStep) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(if (step.status == StepStatus.IN_PROGRESS) Muted.copy(alpha = 0.3f) else Deep)
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = step.icon,
            fontSize = 12.sp,
            modifier = Modifier.width(24.dp)
        )
        Text(
            text = "${step.stepNumber}.",
            fontSize = 10.sp,
            color = Mid,
            modifier = Modifier.width(20.dp)
        )
        Text(
            text = step.title,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = when (step.status) {
                StepStatus.PENDING -> Mid.copy(alpha = 0.7f)
                StepStatus.IN_PROGRESS -> Dark
                StepStatus.COMPLETED -> Muted
                StepStatus.FAILED -> Surface
            },
            modifier = Modifier.weight(1f)
        )
        Text(
            text = when (step.status) {
                StepStatus.PENDING -> ""
                StepStatus.IN_PROGRESS -> "..."
                StepStatus.COMPLETED -> "OK"
                StepStatus.FAILED -> "X"
            },
            fontSize = 10.sp,
            color = when (step.status) {
                StepStatus.COMPLETED -> Muted
                StepStatus.FAILED -> Surface
                else -> Mid
            }
        )
        step.detail?.let { detail ->
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = detail,
                fontSize = 9.sp,
                color = Mid,
                maxLines = 1,
                modifier = Modifier.width(80.dp)
            )
        }
    }
}