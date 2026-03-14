package com.zionhuang.music.ui.hitori.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zionhuang.innertube.models.AlbumItem
import com.zionhuang.innertube.models.ArtistItem
import com.zionhuang.innertube.models.PlaylistItem
import com.zionhuang.innertube.models.SongItem
import com.zionhuang.innertube.models.YTItem
import com.zionhuang.music.ui.hitori.hitoriColors

@Composable
fun AlbumCard(item: YTItem, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed) 0.95f else 1f, label = "")

    val subtitle = when (item) {
        is SongItem -> item.artists.joinToString { it.name }
        is AlbumItem -> item.artists?.joinToString { it.name }
        else -> null
    }

    Column(
        Modifier
            .width(132.dp)
            .scale(scale)
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
    ) {
        MusicThumbnail(
            url = item.thumbnail,
            modifier = Modifier.size(132.dp),
            shape = RoundedCornerShape(14.dp)
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = item.title,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = hitoriColors.Text,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = subtitle ?: "",
            fontSize = 11.sp,
            color = hitoriColors.Text2,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun PlaylistCard(item: YTItem, onClick: () -> Unit) {
    val subtitle = when (item) {
        is PlaylistItem -> item.author?.name
        else -> null
    }

    Column(
        Modifier
            .width(190.dp)
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, hitoriColors.Border, RoundedCornerShape(14.dp))
            .background(hitoriColors.Card)
            .clickable(onClick = onClick)
    ) {
        MusicThumbnail(
            url = item.thumbnail,
            modifier = Modifier.fillMaxWidth().height(110.dp),
            shape = RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp)
        )
        Column(Modifier.padding(10.dp, 10.dp, 12.dp, 12.dp)) {
            Text(
                text = item.title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = hitoriColors.Text,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = subtitle ?: "",
                fontSize = 11.sp,
                color = hitoriColors.Text2,
                modifier = Modifier.padding(top = 2.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun ArtistChip(item: YTItem, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(80.dp)
            .clickable(onClick = onClick)
    ) {
        MusicThumbnail(
            url = item.thumbnail,
            modifier = Modifier
                .size(70.dp)
                .border(2.dp, hitoriColors.Border, CircleShape),
            shape = CircleShape
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = item.title,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = hitoriColors.Text,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}
