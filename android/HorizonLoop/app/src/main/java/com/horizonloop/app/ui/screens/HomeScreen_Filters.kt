package com.horizonloop.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.horizonloop.app.data.FilterType
import com.horizonloop.app.ui.theme.Dark
import com.horizonloop.app.ui.theme.Deep
import com.horizonloop.app.ui.theme.Mid

/**
 * HomeScreen_Filters.kt
 * Filter row composable for HomeScreen
 */

@Composable
fun FilterChipRow(
    currentFilter: FilterType,
    onFilterChange: (FilterType) -> Unit,
    modifier: Modifier = Modifier
) {
    val filters = listOf(
        FilterType.ALL to "All",
        FilterType.SIZE_DESC to "Size: High to Low",
        FilterType.SIZE_ASC to "Size: Low to High",
        FilterType.SUBTITLE_YES to "Subtitle: Yes",
        FilterType.SUBTITLE_NO to "Subtitle: No",
        FilterType.PINNED to "Pinned"
    )
    Row(modifier = modifier) {
        filters.forEach { (filter, label) ->
            Box(modifier = Modifier
                .clip(RoundedCornerShape(50.dp))
                .then(if (currentFilter == filter) Modifier.background(Dark) else Modifier)
                .clickable { onFilterChange(filter) }
                .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(text = label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold,
                    color = if (currentFilter == filter) Deep else Mid)
            }
        }
    }
}
