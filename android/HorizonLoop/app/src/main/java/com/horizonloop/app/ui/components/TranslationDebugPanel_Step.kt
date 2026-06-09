package com.horizonloop.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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

/**
 * TranslationDebugPanel_Step.kt
 * Step row composable
 */

@Composable
fun StepRow(step: TranslationStep) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(if (step.status == StepStatus.IN_PROGRESS) Muted.copy(alpha = 0.3f) else Deep)
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = step.icon, fontSize = 12.sp, modifier = Modifier.width(24.dp))
        Text(text = "${step.stepNumber}.", fontSize = 10.sp, color = Mid, modifier = Modifier.width(20.dp))
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
            Text(text = detail, fontSize = 9.sp, color = Mid, maxLines = 1, modifier = Modifier.width(80.dp))
        }
    }
}
