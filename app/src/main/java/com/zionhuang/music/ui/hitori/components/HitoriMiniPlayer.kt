package com.zionhuang.music.ui.hitori.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zionhuang.music.LocalPlayerConnection
import com.zionhuang.music.R
import com.zionhuang.music.extensions.togglePlayPause
import com.zionhuang.music.ui.hitori.hitoriColors

@Composable
fun HitoriMiniPlayer(
    onTap: () -> Unit,
    position: Long,
    duration: Long
) {
    val playerConnection = LocalPlayerConnection.current ?: return
    val isPlaying by playerConnection.isPlaying.collectAsState()
    val mediaMetadata by playerConnection.mediaMetadata.collectAsState()
    val canSkipNext by playerConnection.canSkipNext.collectAsState()

    if (mediaMetadata == null) return

    val progress = if (duration > 0) position.toFloat() / duration else 0f
    val isLight = !hitoriColors.isDark

    Box(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
            .padding(bottom = 6.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, hitoriColors.Border, RoundedCornerShape(16.dp))
            .background(
                if (isLight) Brush.linearGradient(listOf(Color.White, Color(0xFFF0EDD8)))
                else Brush.linearGradient(listOf(Color(0xFF1C1C14), Color(0xFF141410)))
            )
            .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) { onTap() }
    ) {
        // accent tint
        Box(
            Modifier
                .matchParentSize()
                .background(Brush.horizontalGradient(listOf(hitoriColors.Accent.copy(alpha = 0.05f), Color.Transparent)))
        )

        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            MusicThumbnail(
                url = mediaMetadata?.thumbnailUrl,
                modifier = Modifier.size(42.dp),
                shape = RoundedCornerShape(10.dp)
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = mediaMetadata?.title ?: "",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = hitoriColors.Text,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = mediaMetadata?.artists?.joinToString { it.name } ?: "",
                    fontSize = 11.sp,
                    color = hitoriColors.Text2,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            MpIconButton(icon = R.drawable.skip_previous, onClick = { playerConnection.player.seekToPrevious() })
            MpIconButton(
                icon = if (isPlaying) R.drawable.pause else R.drawable.play,
                onClick = { playerConnection.player.togglePlayPause() }
            )
            MpIconButton(icon = R.drawable.skip_next, onClick = { playerConnection.seekToNext() }, enabled = canSkipNext)
        }

        // Progress bar
        Box(
            Modifier
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .height(2.dp)
                .align(Alignment.BottomStart)
                .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
                .background(hitoriColors.Accent)
        )
    }
}

@Composable
fun MpIconButton(icon: Int, onClick: () -> Unit, enabled: Boolean = true) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = if (enabled) hitoriColors.Text else hitoriColors.Text3,
            modifier = Modifier.size(20.dp)
        )
    }
}
