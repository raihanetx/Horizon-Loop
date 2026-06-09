package com.horizonloop.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.horizonloop.app.ui.theme.Dark
import com.horizonloop.app.ui.theme.Mid
import com.horizonloop.app.ui.theme.Muted

/**
 * LoopComponents_AddDialog_Preview.kt
 * Preview box for Add Loop dialog
 */

@Composable
fun LoopPreviewBox(previewLoop: Map<String, String>) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Muted.copy(alpha = 0.15f))
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(text = previewLoop["name"] ?: "", fontSize = 13.sp, color = Dark, fontWeight = FontWeight.SemiBold)
            Text(text = "Time: ${previewLoop["start"] ?: "—"} - ${previewLoop["end"] ?: "—"} | Loop: ${previewLoop["count"] ?: "1"} time(s)", fontSize = 11.sp, color = Mid)
        }
    }
}
