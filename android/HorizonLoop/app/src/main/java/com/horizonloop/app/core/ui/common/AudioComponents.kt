package com.horizonloop.app.core.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import com.horizonloop.app.core.ui.theme.AppIcons
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.horizonloop.app.core.domain.model.Audio
import com.horizonloop.app.core.ui.theme.Accent
import com.horizonloop.app.core.ui.theme.Dark
import com.horizonloop.app.core.ui.theme.Mid
import com.horizonloop.app.core.ui.theme.Muted
import com.horizonloop.app.core.ui.theme.Surface

@Composable
fun AudioListItem(
    audio: Audio,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(92.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(Surface)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(56.dp).clip(RoundedCornerShape(14.dp)).background(Muted.copy(alpha = 0.5f)), contentAlignment = Alignment.Center) {
            Icon(imageVector = AppIcons.AudioMode, contentDescription = null, tint = Accent, modifier = Modifier.size(36.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = audio.title, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = Dark, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Spacer(modifier = Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = audio.duration, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Mid)
                Spacer(modifier = Modifier.width(10.dp))
                Text(text = audio.size, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Mid)
            }
        }
        Icon(imageVector = AppIcons.ChevronRight, contentDescription = null, tint = Mid, modifier = Modifier.size(18.dp))
    }
}