package com.zionhuang.music.ui.hitori.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.Player
import com.zionhuang.music.LocalPlayerConnection
import com.zionhuang.music.R
import com.zionhuang.music.db.entities.LyricsEntity.Companion.LYRICS_NOT_FOUND
import com.zionhuang.music.extensions.togglePlayPause
import com.zionhuang.music.lyrics.LyricsEntry
import com.zionhuang.music.lyrics.LyricsEntry.Companion.HEAD_LYRICS_ENTRY
import com.zionhuang.music.lyrics.LyricsUtils.findCurrentLineIndex
import com.zionhuang.music.lyrics.LyricsUtils.parseLyrics
import com.zionhuang.music.ui.hitori.FrauncesFont
import com.zionhuang.music.ui.hitori.hitoriColors
import com.zionhuang.music.utils.makeTimeString
import kotlinx.coroutines.delay

@Composable
fun NowPlayingSheet(
    visible: Boolean,
    onDismiss: () -> Unit,
    position: Long,
    duration: Long
) {
    val playerConnection = LocalPlayerConnection.current ?: return
    val mediaMetadata by playerConnection.mediaMetadata.collectAsState()

    if (mediaMetadata == null) return

    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(tween(380, easing = FastOutSlowInEasing)) { it },
        exit  = slideOutVertically(tween(380, easing = FastOutSlowInEasing)) { it }
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .background(hitoriColors.SheetBg)
                .systemBarsPadding()
        ) {
            Column(Modifier.fillMaxSize()) {
                // Handle
                Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        Modifier
                            .width(36.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(hitoriColors.Text3)
                            .clickable { onDismiss() }
                    )
                }

                // Sub-tabs
                var tab by remember { mutableStateOf(0) }
                Row(Modifier.fillMaxWidth().padding(horizontal = 26.dp, vertical = 14.dp)) {
                    listOf("Now Playing", "Lyrics", "Queue").forEachIndexed { i, t ->
                        NpTab(t, tab == i, Modifier.weight(1f)) { tab = i }
                    }
                }

                when (tab) {
                    0 -> NowPlayingArtView(position, duration, Modifier.fillMaxSize())
                    1 -> LyricsView(position, duration, Modifier.fillMaxSize())
                    2 -> QueueView(Modifier.fillMaxSize())
                }
            }
        }
    }
}

@Composable
fun NpTab(label: String, active: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Column(
        modifier = modifier.clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = if (active) FontWeight.Bold else FontWeight.Medium,
            color = if (active) hitoriColors.Text else hitoriColors.Text3
        )
        Spacer(Modifier.height(4.dp))
        if (active) {
            Box(Modifier.size(4.dp).clip(CircleShape).background(hitoriColors.Accent))
        }
    }
}

@Composable
fun NowPlayingArtView(position: Long, duration: Long, modifier: Modifier) {
    val playerConnection = LocalPlayerConnection.current ?: return
    val isPlaying by playerConnection.isPlaying.collectAsState()
    val mediaMetadata by playerConnection.mediaMetadata.collectAsState()
    val shuffleModeEnabled by playerConnection.shuffleModeEnabled.collectAsState()
    val repeatMode by playerConnection.repeatMode.collectAsState()
    val canSkipNext by playerConnection.canSkipNext.collectAsState()
    val canSkipPrevious by playerConnection.canSkipPrevious.collectAsState()

    val artScale by animateFloatAsState(if (isPlaying) 1.03f else 1f, spring(0.6f, 300f), label = "")

    LazyColumn(modifier, contentPadding = PaddingValues(horizontal = 28.dp, vertical = 18.dp)) {
        item {
            MusicThumbnail(
                url = mediaMetadata?.thumbnailUrl,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .scale(artScale)
                    .shadow(24.dp, RoundedCornerShape(22.dp)),
                shape = RoundedCornerShape(22.dp),
                contentScale = ContentScale.Crop
            )
        }
        item {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 22.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        text = mediaMetadata?.title ?: "",
                        fontFamily = FrauncesFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = hitoriColors.Text,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = mediaMetadata?.artists?.joinToString { it.name } ?: "",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = hitoriColors.Text2,
                        modifier = Modifier.padding(top = 3.dp)
                    )
                }
                IconButton(onClick = { playerConnection.toggleLike() }) {
                    Icon(
                        painter = painterResource(R.drawable.favorite),
                        contentDescription = null,
                        tint = hitoriColors.Accent
                    )
                }
            }
        }
        item {
            Column(Modifier.fillMaxWidth().padding(top = 22.dp)) {
                val progress = if (duration > 0) position.toFloat() / duration else 0f
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(hitoriColors.Card2)
                        .pointerInput(Unit) {
                            detectTapGestures { offset ->
                                playerConnection.player.seekTo((duration * (offset.x / size.width)).toLong())
                            }
                        }
                ) {
                    Box(
                        Modifier
                            .fillMaxWidth(progress.coerceIn(0f, 1f))
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(2.dp))
                            .background(hitoriColors.Accent)
                    )
                }
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = makeTimeString(position), fontSize = 11.sp, color = hitoriColors.Text3, fontWeight = FontWeight.Medium)
                    Text(text = makeTimeString(duration), fontSize = 11.sp, color = hitoriColors.Text3, fontWeight = FontWeight.Medium)
                }
            }
        }
        item {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 18.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { playerConnection.player.shuffleModeEnabled = !shuffleModeEnabled }) {
                    Icon(
                        painter = painterResource(R.drawable.shuffle),
                        contentDescription = null,
                        tint = if (shuffleModeEnabled) hitoriColors.Accent else hitoriColors.Text3,
                        modifier = Modifier.size(20.dp)
                    )
                }
                IconButton(onClick = { playerConnection.seekToPrevious() }, enabled = canSkipPrevious) {
                    Icon(
                        painter = painterResource(R.drawable.skip_previous),
                        contentDescription = null,
                        tint = if (canSkipPrevious) hitoriColors.Text else hitoriColors.Text3,
                        modifier = Modifier.size(24.dp)
                    )
                }
                PlayPauseButton(isPlaying) { playerConnection.player.togglePlayPause() }
                IconButton(onClick = { playerConnection.seekToNext() }, enabled = canSkipNext) {
                    Icon(
                        painter = painterResource(R.drawable.skip_next),
                        contentDescription = null,
                        tint = if (canSkipNext) hitoriColors.Text else hitoriColors.Text3,
                        modifier = Modifier.size(24.dp)
                    )
                }
                IconButton(onClick = {
                    playerConnection.player.repeatMode = when (repeatMode) {
                        Player.REPEAT_MODE_OFF -> Player.REPEAT_MODE_ALL
                        Player.REPEAT_MODE_ALL -> Player.REPEAT_MODE_ONE
                        else -> Player.REPEAT_MODE_OFF
                    }
                }) {
                    Icon(
                        painter = painterResource(
                            when (repeatMode) {
                                Player.REPEAT_MODE_ONE -> R.drawable.repeat_one
                                else -> R.drawable.repeat
                            }
                        ),
                        contentDescription = null,
                        tint = if (repeatMode != Player.REPEAT_MODE_OFF) hitoriColors.Accent else hitoriColors.Text3,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun PlayPauseButton(isPlaying: Boolean, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed) 0.91f else 1f, spring(0.6f, 400f), label = "")
    Box(
        Modifier
            .size(64.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(hitoriColors.Accent)
            .clickable(interactionSource = interactionSource, indication = null) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(if (isPlaying) R.drawable.pause else R.drawable.play),
            contentDescription = null,
            tint = Color(0xFF0A0A08),
            modifier = Modifier.size(26.dp).then(if (!isPlaying) Modifier.offset(x = 2.dp) else Modifier)
        )
    }
}

@Composable
fun LyricsView(position: Long, duration: Long, modifier: Modifier) {
    val playerConnection = LocalPlayerConnection.current ?: return
    val lyricsEntity by playerConnection.currentLyrics.collectAsState()

    val lines = remember(lyricsEntity) {
        val lyrics = lyricsEntity?.lyrics
        if (lyrics == null || lyrics == LYRICS_NOT_FOUND) emptyList()
        else if (lyrics.startsWith("[")) listOf(HEAD_LYRICS_ENTRY) + parseLyrics(lyrics)
        else lyrics.lines().mapIndexed { index, line -> LyricsEntry(index * 1000L, line) }
    }

    val currentLyricIndex = remember(lines, position) {
        findCurrentLineIndex(lines, position)
    }

    val listState = rememberLazyListState()

    LaunchedEffect(currentLyricIndex) {
        if (currentLyricIndex >= 0 && currentLyricIndex < lines.size) {
            listState.animateScrollToItem(currentLyricIndex, scrollOffset = -200)
        }
    }

    if (lines.isEmpty()) {
        Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("♪", fontSize = 48.sp, color = hitoriColors.Text3)
                Spacer(Modifier.height(12.dp))
                Text("No lyrics available", fontSize = 14.sp, color = hitoriColors.Text2, fontWeight = FontWeight.Medium)
            }
        }
        return
    }

    LazyColumn(
        state = listState,
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 28.dp, vertical = 20.dp)
    ) {
        itemsIndexed(lines) { index, line ->
            val isActive = index == currentLyricIndex
            val isPast   = index < currentLyricIndex
            val scale by animateFloatAsState(if (isActive) 1.04f else 1f, tween(300), label = "")
            val color = when { isActive -> hitoriColors.Text; isPast -> hitoriColors.Text2; else -> hitoriColors.Text3 }

            Text(
                text = line.text,
                fontFamily = FrauncesFont,
                fontWeight = FontWeight.Bold,
                fontSize = 26.sp,
                lineHeight = 35.sp,
                color = color,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
                    .graphicsLayer(scaleX = scale, scaleY = scale, transformOrigin = TransformOrigin(0f, 0.5f))
                    .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) {
                        playerConnection.player.seekTo(line.time)
                    }
            )
        }
    }
}

@Composable
fun QueueView(modifier: Modifier) {
    val playerConnection = LocalPlayerConnection.current ?: return
    val queueWindows by playerConnection.queueWindows.collectAsState()
    val currentIndex by playerConnection.currentWindowIndex.collectAsState()

    LazyColumn(modifier, contentPadding = PaddingValues(bottom = 60.dp)) {
        item {
            Text(
                text = "UP NEXT",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.12.sp,
                color = hitoriColors.Text3,
                modifier = Modifier.padding(start = 22.dp, end = 22.dp, top = 12.dp, bottom = 8.dp)
            )
        }
        itemsIndexed(queueWindows) { index, window ->
            val isPlaying = index == currentIndex
            val mediaItem = window.mediaItem

            Row(
                Modifier
                    .fillMaxWidth()
                    .clickable { playerConnection.player.seekToDefaultPosition(index) }
                    .padding(horizontal = 22.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(Modifier.width(18.dp), contentAlignment = Alignment.Center) {
                    if (isPlaying) EqBarsAnimation()
                    else Text("${index + 1}", fontSize = 12.sp, color = hitoriColors.Text3)
                }
                MusicThumbnail(url = mediaItem.mediaMetadata.artworkUri?.toString(), modifier = Modifier.size(44.dp), shape = RoundedCornerShape(10.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        text = mediaItem.mediaMetadata.title?.toString() ?: "",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isPlaying) hitoriColors.Accent else hitoriColors.Text,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = mediaItem.mediaMetadata.artist?.toString() ?: "",
                        fontSize = 11.sp,
                        color = hitoriColors.Text2,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
fun EqBarsAnimation() {
    val inf = rememberInfiniteTransition(label = "")
    val h1 by inf.animateFloat(0.3f, 1f, infiniteRepeatable(tween(650, easing = LinearEasing), RepeatMode.Reverse), label = "")
    val h2 by inf.animateFloat(0.3f, 1f, infiniteRepeatable(tween(650, delayMillis = 130, easing = LinearEasing), RepeatMode.Reverse), label = "")
    val h3 by inf.animateFloat(0.3f, 1f, infiniteRepeatable(tween(650, delayMillis = 60, easing = LinearEasing), RepeatMode.Reverse), label = "")
    Row(Modifier.width(16.dp).height(14.dp), verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
        listOf(5.dp to h1, 12.dp to h2, 7.dp to h3).forEach { (maxH, scale) ->
            Box(Modifier.width(3.dp).height(maxH * scale).clip(RoundedCornerShape(1.dp)).background(hitoriColors.Accent))
        }
    }
}
