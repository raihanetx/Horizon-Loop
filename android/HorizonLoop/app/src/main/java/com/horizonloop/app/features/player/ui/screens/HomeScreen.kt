package com.horizonloop.app.features.player.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.horizonloop.app.core.ui.theme.AppIcons
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
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
import com.horizonloop.app.core.domain.model.Audio
import com.horizonloop.app.core.domain.model.FilterType
import com.horizonloop.app.core.ui.common.AudioListItem
import com.horizonloop.app.core.ui.theme.Dark
import com.horizonloop.app.core.ui.theme.Deep
import com.horizonloop.app.core.ui.theme.Mid
import com.horizonloop.app.core.ui.theme.Muted
import com.horizonloop.app.core.ui.theme.Surface
import com.horizonloop.app.core.ui.theme.White12

@Composable
fun HomeScreen(
    audioFiles: List<Audio>,
    searchQuery: String,
    currentFilter: FilterType,
    onSearchChange: (String) -> Unit,
    onFilterChange: (FilterType) -> Unit,
    onAudioClick: (Audio) -> Unit,
    onSettingsClick: () -> Unit,
    isScanning: Boolean = false,
    scanError: String? = null,
    modifier: Modifier = Modifier
) {
    var showFilters by remember { mutableStateOf(false) }
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Deep)
            .padding(14.dp)
    ) {
        if (isScanning) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Mid, strokeWidth = 2.dp)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Scanning device for media...", fontSize = 12.sp, color = Mid)
            }
        }
        scanError?.let { error ->
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = error, fontSize = 12.sp, color = Surface, modifier = Modifier.fillMaxWidth().background(Mid.copy(alpha = 0.2f), RoundedCornerShape(8.dp)).padding(12.dp))
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column {
                Text("Horizon Loop", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = Dark, letterSpacing = (-0.5).sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Master English by Listening", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Mid, letterSpacing = 1.sp)
            }
            Row {
                IconButton(onClick = { showFilters = !showFilters }) {
                    Icon(AppIcons.Filter, contentDescription = "Filter", tint = Dark, modifier = Modifier.size(22.dp))
                }
                Spacer(modifier = Modifier.width(4.dp))
                IconButton(onClick = onSettingsClick) {
                    Icon(AppIcons.Settings, contentDescription = "Settings", tint = Dark, modifier = Modifier.size(22.dp))
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(50.dp))
                .background(White12)
                .padding(horizontal = 18.dp, vertical = 12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(AppIcons.Search, contentDescription = null, tint = Mid, modifier = Modifier.size(16.dp))
                BasicTextField(
                    value = searchQuery,
                    onValueChange = onSearchChange,
                    modifier = Modifier.fillMaxWidth().padding(start = 8.dp),
                    textStyle = TextStyle(fontSize = 14.sp, color = Dark),
                    cursorBrush = SolidColor(Dark),
                    decorationBox = { innerTextField -> Box { if (searchQuery.isEmpty()) Text("Search lessons...", color = Mid, fontSize = 14.sp) else innerTextField() } }
                )
            }
        }
        if (showFilters) {
            Spacer(modifier = Modifier.height(16.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                val filters = listOf(
                    FilterType.ALL to "All",
                    FilterType.SIZE_DESC to "Size: High to Low",
                    FilterType.SIZE_ASC to "Size: Low to High",
                    FilterType.SUBTITLE_YES to "Subtitle: Yes",
                    FilterType.SUBTITLE_NO to "Subtitle: No",
                    FilterType.PINNED to "Pinned"
                )
                items(filters) { (filter, label) ->
                    FilterChip(label = label, isActive = currentFilter == filter, onClick = { onFilterChange(filter) })
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(14.dp)) {
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
            .clip(RoundedCornerShape(50.dp))
            .then(if (isActive) Modifier.background(Dark) else Modifier.border(1.dp, Mid.copy(alpha = 0.4f), RoundedCornerShape(50.dp)))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(text = label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = if (isActive) Deep else Mid)
    }
}