package com.horizonloop.app.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.horizonloop.app.ui.theme.Dark
import com.horizonloop.app.ui.theme.Mid
import com.horizonloop.app.ui.theme.Muted

/**
 * LoopComponents_AddDialog_Form.kt
 * Form fields for Add Loop dialog
 */

@Composable
fun AddLoopFormFields(
    name: String,
    start: String,
    end: String,
    count: String,
    onNameChange: (String) -> Unit,
    onStartChange: (String) -> Unit,
    onEndChange: (String) -> Unit,
    onCountChange: (String) -> Unit
) {
    OutlinedTextField(
        value = name, onValueChange = onNameChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text("e.g. Chorus Section", color = Mid) },
        label = { Text("Name", fontSize = 11.sp, color = Mid) },
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Dark, unfocusedTextColor = Dark,
            focusedBorderColor = Mid, unfocusedBorderColor = Muted, cursorColor = Dark
        ),
        shape = RoundedCornerShape(12.dp)
    )

    Row(horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(16.dp)) {
        OutlinedTextField(
            value = start, onValueChange = onStartChange,
            modifier = Modifier.weight(1f),
            placeholder = { Text("e.g. 1:23", color = Mid) },
            label = { Text("Start", fontSize = 11.sp, color = Mid) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Dark, unfocusedTextColor = Dark,
                focusedBorderColor = Mid, unfocusedBorderColor = Muted, cursorColor = Dark
            ),
            shape = RoundedCornerShape(12.dp)
        )
        OutlinedTextField(
            value = end, onValueChange = onEndChange,
            modifier = Modifier.weight(1f),
            placeholder = { Text("e.g. 2:45", color = Mid) },
            label = { Text("End", fontSize = 11.sp, color = Mid) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Dark, unfocusedTextColor = Dark,
                focusedBorderColor = Mid, unfocusedBorderColor = Muted, cursorColor = Dark
            ),
            shape = RoundedCornerShape(12.dp)
        )
    }

    OutlinedTextField(
        value = count, onValueChange = onCountChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text("e.g. 3", color = Mid) },
        label = { Text("Times", fontSize = 11.sp, color = Mid) },
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Dark, unfocusedTextColor = Dark,
            focusedBorderColor = Mid, unfocusedBorderColor = Muted, cursorColor = Dark
        ),
        shape = RoundedCornerShape(12.dp)
    )
}
