package com.horizonloop.app.core.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowLeft
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.horizonloop.app.core.domain.model.Audio
import com.horizonloop.app.core.ui.theme.Accent
import com.horizonloop.app.core.ui.theme.CardBg
import com.horizonloop.app.core.ui.theme.Mid
import com.horizonloop.app.core.ui.theme.Muted
import com.horizonloop.app.core.ui.theme.TextMut
import com.horizonloop.app.core.ui.theme.TextPri
import com.horizonloop.app.core.ui.theme.TextSec
import com.horizonloop.app.core.ui.theme.Brd

@Composable
fun AudioListItem(
    audio: Audio,
    onClick: () -> Unit,
    onPlayClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    // Subtitle (size · divider · Subtitle: Yes/No) — single AnnotatedString so it
    // truncates as one unit. Cached so it doesn't rebuild on recomposition.
    val subtitleText = remember(audio.size, audio.subtitle) {
        buildAnnotatedString {
            append(audio.size)
            append("  \u00B7  ")
            withStyle(SpanStyle(color = TextSec)) {
                append("Subtitle: ")
            }
            if (audio.subtitle) {
                withStyle(SpanStyle(color = Accent, fontWeight = FontWeight.SemiBold)) {
                    append("Yes")
                }
            } else {
                withStyle(SpanStyle(color = TextMut)) {
                    append("No")
                }
            }
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(CardBg)
            .border(1.dp, Brd, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Muted),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.MusicNote,
                contentDescription = null,
                tint = TextPri,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = audio.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPri,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitleText,
                fontSize = 12.5.sp,
                fontWeight = FontWeight.Normal,
                color = TextSec,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = audio.duration,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = TextPri
        )
        Spacer(modifier = Modifier.width(12.dp))
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Muted)
                .clickable(enabled = onPlayClick != null) { onPlayClick?.invoke() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowLeft,
                contentDescription = "Back",
                tint = TextPri,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
