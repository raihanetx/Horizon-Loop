package com.horizonloop.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.horizonloop.app.ui.theme.AppIcons
import com.horizonloop.app.ui.theme.Dark
import com.horizonloop.app.ui.theme.Mid
import com.horizonloop.app.ui.theme.White12

/**
 * HomeScreen_Search.kt
 * Search bar composable for HomeScreen
 */

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxWidth().clip(RoundedCornerShape(50.dp)).background(White12).padding(horizontal = 16.dp, vertical = 10.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(AppIcons.Search, contentDescription = null, tint = Mid, modifier = Modifier.size(16.dp))
            BasicTextField(value = query, onValueChange = onQueryChange, modifier = Modifier.fillMaxWidth().padding(start = 8.dp), textStyle = TextStyle(fontSize = 14.sp, color = Dark), cursorBrush = SolidColor(Dark), decorationBox = { innerTextField -> Box { if (query.isEmpty()) Text("Search lessons...", color = Mid, fontSize = 14.sp) else innerTextField() } })
        }
    }
}
