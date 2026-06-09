package com.horizonloop.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.horizonloop.app.ui.theme.Deep
import com.horizonloop.app.ui.theme.Mid

/**
 * LoopComponents_AddDialog_Buttons.kt
 * Buttons for Add Loop dialog
 */

@Composable
fun AddLoopDialogButtons(
    onPreview: () -> Unit,
    onCancel: () -> Unit,
    onSave: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(onClick = onPreview) {
            Text("Preview", color = Mid, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        }
        TextButton(onClick = onCancel) {
            Text("Cancel", color = Mid, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        }
        Button(
            onClick = onSave,
            colors = ButtonDefaults.buttonColors(containerColor = Mid, contentColor = Deep),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Save", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}
