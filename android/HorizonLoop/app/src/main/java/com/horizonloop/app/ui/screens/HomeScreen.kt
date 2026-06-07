package com.horizonloop.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.horizonloop.app.data.Audio
import com.horizonloop.app.data.FilterType
import com.horizonloop.app.ui.components.AudioListItem
import com.horizonloop.app.ui.theme.Dark
import com.horizonloop.app.ui.theme.Deep
import com.horizonloop.app.ui.theme.Mid
import com.horizonloop.app.ui.theme.Muted
import com.horizonloop.app.ui.theme.Surface

@Composable
fun HomeScreen(
    audioFiles: List<Audio>,
    searchQuery: String,
    currentFilter: FilterType,
    onSearchChange: (String) -> Unit,
    onFilterChange: (FilterType) -> Unit,
    onAudioClick: (Audio) -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showFilters by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Deep)
            .padding(15.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column {
                Text("Horizon Loop", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = Dark)
                Text("Master English by Listening", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Mid, letterSpacing = 1.sp)
            }
            Row {
                IconButton(onClick = { showFilters = !showFilters }) {
                    Icon(Icons.Default.FilterList, contentDescription = "Filter", tint = Dark)
                }
                IconButton(onClick = onSettingsClick) {
                    Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Dark)
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(50))
                .background(Mid.copy(alpha = 0.12f))
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Search, contentDescription = null, tint = Mid, modifier = Modifier.size(16.dp))
                BasicTextField(
                    value = searchQuery,
                    onValueChange = onSearchChange,
                    modifier = Modifier.fillMaxWidth().padding(start = 8.dp),
                    textStyle = TextStyle(fontSize = 14.sp, color = Dark),
                    cursorBrush = SolidColor(Dark),
                    decorationBox = { innerTextField ->
                        Box { if (searchQuery.isEmpty()) Text("Search lessons...", color = Mid, fontSize = 14.sp) else innerTextField() }
                    }
                )
            }
        }
        if (showFilters) {
            Spacer(modifier = Modifier.height(12.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                val filters = listOf(
                    FilterType.ALL to "All",
                    FilterType.SIZE_DESC to "Size: High to Low",
                    FilterType.SIZE_ASC to "Size: Low to High",
                    FilterType.SUBTITLE_YES to "Subtitle: Yes",
                    FilterType.SUBTITLE_NO to "Subtitle: No",
                    FilterType.PINNED to "Pinned"
                )
                items(filters) { (filter, label) ->
                    FilterChip(
                        label = label,
                        isActive = currentFilter == filter,
                        onClick = { onFilterChange(filter) }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(audioFiles, key = { it.id }) { audio ->
                AudioListItem(audio = audio, onClick = { onAudioClick(audio) })
            }
        }
    }
}

@Composable
private fun FilterChip(label: String, isActive: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(if (isActive) Dark else Muted.copy(alpha = 0.12f))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = if (isActive) Deep else Mid)
    }
}

@Composable
fun SettingsDialog(
    apiKey: String,
    selectedEngine: String,
    onApiKeyChange: (String) -> Unit,
    onEngineChange: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Settings", color = Dark) },
        text = {
            Column {
                Text("OpenAI API Key", fontSize = 12.sp, color = Mid, modifier = Modifier.padding(bottom = 4.dp))
                OutlinedTextField(
                    value = apiKey,
                    onValueChange = onApiKeyChange,
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(fontSize = 14.sp),
                    placeholder = { Text("sk-...", color = Mid) },
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text("Translation Engine", fontSize = 12.sp, color = Mid, modifier = Modifier.padding(bottom = 4.dp))
                Row {
                    Button(
                        onClick = { onEngineChange("gpt-4o-mini") },
                        colors = if (selectedEngine == "gpt-4o-mini") androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = Dark) else androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = Muted)
                    ) {
                        Text("GPT-4o Mini", fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { onEngineChange("gpt-4o") },
                        colors = if (selectedEngine == "gpt-4o") androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = Dark) else androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = Muted)
                    ) {
                        Text("GPT-4o", fontSize = 12.sp)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = Dark)
            }
        },
        containerColor = Surface
    )
}