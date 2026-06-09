package com.horizonloop.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.horizonloop.app.data.TranslationStep
import com.horizonloop.app.ui.theme.Dark
import com.horizonloop.app.ui.theme.Deep
import com.horizonloop.app.ui.theme.Mid

/**
 * TranslationDebugPanel_Header.kt
 * Header component for translation debug panel
 */

@Composable
fun TranslationDebugHeader(steps: List<TranslationStep>, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth().clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)).background(Dark).padding(16.dp)) {
        Text("Translation Debug", fontSize = 18.sp, color = Deep)
        Spacer(modifier = Modifier.height(8.dp))
        steps.forEach { step ->
            Text("Step ${step.stepNumber}: ${step.title} - ${step.status}", fontSize = 12.sp, color = Mid)
        }
    }
}
