package com.horizonloop.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.horizonloop.app.data.Audio
import com.horizonloop.app.ui.components.AudioListItem
import com.horizonloop.app.ui.theme.Dark
import com.horizonloop.app.ui.theme.Deep
import com.horizonloop.app.ui.theme.Mid

/**
 * HomeScreen_Main.kt - Main home screen composable
 */

@Composable
fun HomeScreen(
    isLoading: Boolean, audioFiles: List<Audio>, searchQuery: String, onSearchChange: (String) -> Unit,
    onAudioSelect: (Audio) -> Unit, modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize().background(Deep)) {
        SearchBar(searchQuery, onSearchChange, Modifier.fillMaxWidth().padding(16.dp))
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Mid)
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
                items(audioFiles, key = { it.id }) { audio ->
                    AudioListItem(audio = audio, onClick = { onAudioSelect(audio) })
                }
            }
        }
    }
}
