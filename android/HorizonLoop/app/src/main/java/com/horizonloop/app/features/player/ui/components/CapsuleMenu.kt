package com.horizonloop.app.features.player.ui.components

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.content.Context
import com.horizonloop.app.core.ui.theme.Dark
import com.horizonloop.app.core.ui.theme.Mid
import com.horizonloop.app.core.ui.theme.Muted
import com.horizonloop.app.core.ui.theme.Surface

data class CapsuleMenuItem(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val isActive: Boolean = false,
    val onClick: () -> Unit
)

@Composable
fun CapsuleMenu(
    activeTab: String,
    audioMode: Boolean,
    onTabClick: (String) -> Unit,
    onAudioModeToggle: () -> Unit,
    onTranslate: (Context) -> Unit,
    onDismiss: () -> Unit,
    context: Context,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .background(Surface)
            .padding(18.dp)
            .padding(bottom = 24.dp)
    ) {
        Box(modifier = Modifier.width(44.dp).height(5.dp).clip(RoundedCornerShape(3.dp)).background(Muted).align(Alignment.CenterHorizontally))
        Spacer(modifier = Modifier.height(16.dp))
        val menuItems = listOf(
            CapsuleMenuItem("Subtitle", "Enable or disable subtitles display", AppIcons.Subtitles, activeTab == "clean") { onTabClick("clean") },
            CapsuleMenuItem("List", "View full dialogue and transcript list", AppIcons.List, activeTab == "save") { onTabClick("save") },
            CapsuleMenuItem("Save time frame", "Set start and end time markers", AppIcons.Loop, activeTab == "loop") { onTabClick("loop") },
            CapsuleMenuItem("Note", "Add and manage your personal notes", AppIcons.Note, activeTab == "notes") { onTabClick("notes") },
            CapsuleMenuItem("Audio", "Toggle audio only mode for listening", AppIcons.AudioMode, audioMode) { onAudioModeToggle() },
            CapsuleMenuItem("Translate", "Generate English and Bangla subtitles", AppIcons.Translate) { onTranslate(context) }
        )
        menuItems.forEachIndexed { index, item ->
            CapsuleMenuRow(item = item, isLast = index == menuItems.lastIndex)
            if (index < menuItems.lastIndex) { Spacer(modifier = Modifier.height(6.dp)) }
        }
    }
}

@Composable
private fun CapsuleMenuRow(item: CapsuleMenuItem, isLast: Boolean) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .clickable(onClick = item.onClick)
                .background(if (item.isActive) Muted.copy(alpha = 0.1f) else androidx.compose.ui.graphics.Color.Transparent)
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(Muted), contentAlignment = Alignment.Center) {
                Icon(imageVector = item.icon, contentDescription = null, tint = if (item.isActive) Dark else Mid, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(item.title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Dark)
                Spacer(modifier = Modifier.height(4.dp))
                Text(item.subtitle, fontSize = 12.sp, fontWeight = FontWeight.Normal, color = Mid.copy(alpha = 0.8f))
            }
            Icon(imageVector = AppIcons.ChevronRight, contentDescription = null, tint = Mid, modifier = Modifier.size(18.dp))
        }
    }
}