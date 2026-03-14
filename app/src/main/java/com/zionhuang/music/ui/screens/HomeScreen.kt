package com.zionhuang.music.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.zionhuang.innertube.models.AlbumItem
import com.zionhuang.innertube.models.ArtistItem
import com.zionhuang.innertube.models.PlaylistItem
import com.zionhuang.innertube.models.SongItem
import com.zionhuang.innertube.models.WatchEndpoint
import com.zionhuang.music.LocalPlayerAwareWindowInsets
import com.zionhuang.music.LocalPlayerConnection
import com.zionhuang.music.playback.queues.YouTubeQueue
import com.zionhuang.music.ui.hitori.FrauncesFont
import com.zionhuang.music.ui.hitori.hitoriColors
import com.zionhuang.music.ui.hitori.components.AlbumCard
import com.zionhuang.music.ui.hitori.components.ArtistChip
import com.zionhuang.music.ui.hitori.components.HeroCarousel
import com.zionhuang.music.ui.hitori.components.PlaylistCard
import com.zionhuang.music.viewmodels.HomeViewModel
import com.zionhuang.music.models.toMediaMetadata

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val playerConnection = LocalPlayerConnection.current ?: return
    val homePage by viewModel.homePage.collectAsState()
    val explorePage by viewModel.explorePage.collectAsState()

    LazyColumn(
        Modifier
            .fillMaxSize()
            .background(hitoriColors.Bg),
        contentPadding = LocalPlayerAwareWindowInsets.current.asPaddingValues()
    ) {
        // Hero carousel: first shelf or featured items
        item {
            val featuredItems = homePage?.sections?.firstOrNull()?.items ?: emptyList()
            HeroCarousel(
                items = featuredItems,
                onItemClick = { item ->
                    when (item) {
                        is SongItem -> playerConnection.playQueue(YouTubeQueue(item.endpoint ?: WatchEndpoint(videoId = item.id), item.toMediaMetadata()))
                        is AlbumItem -> navController.navigate("album/${item.id}")
                        is ArtistItem -> navController.navigate("artist/${item.id}")
                        is PlaylistItem -> navController.navigate("online_playlist/${item.id}")
                    }
                }
            )
        }

        homePage?.sections?.drop(1)?.forEach { section ->
            item {
                SectionHeader(title = section.title)
            }
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 22.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(section.items) { item ->
                        when (item) {
                            is AlbumItem -> AlbumCard(item, onClick = { navController.navigate("album/${item.id}") })
                            is ArtistItem -> ArtistChip(item, onClick = { navController.navigate("artist/${item.id}") })
                            is PlaylistItem -> PlaylistCard(item, onClick = { navController.navigate("online_playlist/${item.id}") })
                            is SongItem -> AlbumCard(item, onClick = { playerConnection.playQueue(YouTubeQueue(item.endpoint ?: WatchEndpoint(videoId = item.id), item.toMediaMetadata())) })
                        }
                    }
                }
            }
        }

        explorePage?.newReleaseAlbums?.let { newReleases ->
            item {
                SectionHeader(title = "New Releases")
            }
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 22.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(newReleases) { album ->
                        AlbumCard(album, onClick = { navController.navigate("album/${album.id}") })
                    }
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 22.dp, vertical = 14.dp)
    ) {
        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = hitoriColors.Text
        )
    }
}
