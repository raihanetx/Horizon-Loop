package com.horizonloop.app.features.player.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.horizonloop.app.core.domain.model.Audio
import com.horizonloop.app.core.domain.model.FilterType
import com.horizonloop.app.core.ui.theme.HomeAccent
import com.horizonloop.app.core.ui.theme.HomeBg
import com.horizonloop.app.core.ui.theme.HomeCard
import com.horizonloop.app.core.ui.theme.HomeDivider
import com.horizonloop.app.core.ui.theme.HomeElevated
import com.horizonloop.app.core.ui.theme.HomeSubtitleNo
import com.horizonloop.app.core.ui.theme.HomeTextPrimary
import com.horizonloop.app.core.ui.theme.HomeTextSecondary
import com.horizonloop.app.core.ui.theme.HomeTextTertiary

private val filterOptions = listOf(
    "All" to FilterType.ALL,
    "Size: Low to High" to FilterType.SIZE_ASC,
    "Size: High to Low" to FilterType.SIZE_DESC,
    "Pinned" to FilterType.PINNED,
    "With Subtitle" to FilterType.SUBTITLE_YES,
    "No Subtitle" to FilterType.SUBTITLE_NO
)

private fun selectedFilterLabel(type: FilterType): String =
    filterOptions.firstOrNull { it.second == type }?.first ?: "All"

private val ChevronRightIcon: ImageVector by lazy {
    ImageVector.Builder(
        name = "ChevronRight",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            fill = SolidColor(Color.Black),
            fillAlpha = 1.0f,
            pathFillType = PathFillType.NonZero
        ) {
            moveTo(13.1717f, 12.0007f)
            lineTo(8.22192f, 7.05093f)
            lineTo(9.63614f, 5.63672f)
            lineTo(16.0001f, 12.0007f)
            lineTo(9.63614f, 18.3646f)
            lineTo(8.22192f, 16.9504f)
            lineTo(13.1717f, 12.0007f)
            close()
        }
    }.build()
}

private fun parseSizeMb(sizeText: String): Float =
    Regex("""([0-9]+(?:\.[0-9]+)?)""").find(sizeText)?.groupValues?.get(1)?.toFloatOrNull() ?: 0f

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
    var filterVisible by remember { mutableStateOf(false) }
    val actionStates = remember(audioFiles) { mutableStateMapOf<Int, Boolean>() }

    val displayItems = remember(audioFiles, currentFilter, actionStates.toMap(), searchQuery) {
        val q = searchQuery.trim().lowercase()
        var result = audioFiles.filter { audio ->
            q.isEmpty() || audio.title.lowercase().contains(q)
        }
        when (currentFilter) {
            FilterType.SIZE_ASC -> result = result.sortedBy { parseSizeMb(it.size) }
            FilterType.SIZE_DESC -> result = result.sortedByDescending { parseSizeMb(it.size) }
            FilterType.PINNED -> result = result.filter { (actionStates[it.id] ?: it.pin) }
            FilterType.SUBTITLE_YES -> result = result.filter { it.subtitle }
            FilterType.SUBTITLE_NO -> result = result.filter { !it.subtitle }
            FilterType.ALL -> Unit
        }
        result
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(HomeBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            Header(onSettingsClick = onSettingsClick)
            Spacer(Modifier.height(24.dp))

            SearchBar(
                searchQuery = searchQuery,
                onSearchChange = onSearchChange,
                onFilterClick = { filterVisible = !filterVisible }
            )
            Spacer(Modifier.height(16.dp))

            if (filterVisible) {
                FilterDropdown(
                    options = filterOptions.map { it.first },
                    selected = selectedFilterLabel(currentFilter),
                    onOptionSelected = { label ->
                        filterOptions.firstOrNull { it.first == label }?.let { onFilterChange(it.second) }
                    }
                )
                Spacer(Modifier.height(16.dp))
            }

            if (isScanning) {
                Text(
                    "Scanning device for media...",
                    color = HomeTextSecondary,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
            scanError?.let { error ->
                Text(
                    text = error,
                    color = HomeCard,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(HomeTextSecondary.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                        .padding(12.dp)
                )
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(displayItems, key = { it.id }) { item ->
                    PodcastCard(
                        item = item,
                        isPinned = actionStates[item.id] ?: item.pin,
                        onPinToggle = {
                            actionStates[item.id] = !(actionStates[item.id] ?: item.pin)
                        },
                        onClick = { onAudioClick(item) }
                    )
                }
            }
        }
    }
}

@Composable
private fun Header(onSettingsClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column {
            Text(
                text = "Horizon Loop",
                color = HomeTextPrimary,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.5).sp
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Learn through listening, discover through stories",
                color = HomeTextSecondary,
                fontSize = 14.sp
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
            Icon(
                imageVector = Icons.Filled.Notifications,
                contentDescription = "Notifications",
                tint = HomeTextPrimary,
                modifier = Modifier
                    .size(24.dp)
                    .clickable { }
            )
            Icon(
                imageVector = Icons.Filled.Settings,
                contentDescription = "Settings",
                tint = HomeTextPrimary,
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onSettingsClick() }
            )
        }
    }
}

@Composable
private fun SearchBar(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onFilterClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(HomeElevated)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.Search,
            contentDescription = "Search",
            tint = HomeTextSecondary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(8.dp))
        TextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            placeholder = {
                Text(
                    "Search podcasts, music...",
                    color = HomeTextTertiary,
                    fontSize = 14.sp
                )
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = HomeAccent
            ),
            modifier = Modifier.weight(1f),
            singleLine = true
        )
        Spacer(Modifier.width(4.dp))
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(12.dp))
                .clickable { onFilterClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Tune,
                contentDescription = "Filter",
                tint = HomeAccent,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
private fun FilterDropdown(
    options: List<String>,
    selected: String,
    onOptionSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(HomeElevated)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            items(options) { option ->
                val isSelected = option == selected
                val textColor by animateColorAsState(
                    if (isSelected) HomeAccent else HomeTextSecondary
                )
                Text(
                    text = option,
                    color = textColor,
                    fontSize = 14.sp,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                    maxLines = 1,
                    modifier = Modifier.clickable { onOptionSelected(option) }
                )
            }
        }
    }
}

@Composable
private fun PodcastCard(
    item: Audio,
    isPinned: Boolean,
    onPinToggle: () -> Unit,
    onClick: () -> Unit
) {
    val subtitleText = if (item.subtitle) "Yes" else "No"

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(HomeCard)
            .clickable(onClick = onClick)
            .padding(start = 20.dp, end = 8.dp, top = 16.dp, bottom = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(HomeElevated),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Headphones,
                    contentDescription = "Podcast",
                    tint = HomeTextPrimary,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(Modifier.width(16.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 80.dp)
            ) {
                Text(
                    text = item.title,
                    color = HomeTextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(6.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = item.size,
                        color = HomeTextSecondary,
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Text(
                        text = "¦",
                        color = HomeDivider,
                        fontSize = 12.sp,
                        maxLines = 1
                    )
                    Text(
                        text = "Subtitle:",
                        color = HomeTextTertiary,
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = subtitleText,
                        color = if (item.subtitle) HomeAccent else HomeSubtitleNo,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            Text(
                text = item.duration,
                color = HomeTextTertiary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onPinToggle() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = ChevronRightIcon,
                    contentDescription = "Action",
                    tint = if (isPinned) HomeAccent else HomeTextTertiary,
                    modifier = Modifier
                        .size(24.dp)
                        .graphicsLayer {
                            scaleX = if (isPinned) -1f else 1f
                        }
                )
            }
        }
    }
}
