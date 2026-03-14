package com.zionhuang.music.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.zionhuang.music.LocalPlayerAwareWindowInsets
import com.zionhuang.music.LocalPlayerConnection
import com.zionhuang.music.models.toMediaMetadata
import com.zionhuang.music.ui.hitori.FrauncesFont
import com.zionhuang.music.ui.hitori.hitoriColors
import com.zionhuang.music.ui.hitori.components.HitoriFilterChip
import com.zionhuang.music.ui.hitori.components.LibraryRow
import com.zionhuang.music.viewmodels.LibraryAlbumsViewModel
import com.zionhuang.music.viewmodels.LibraryArtistsViewModel
import com.zionhuang.music.viewmodels.LibraryPlaylistsViewModel
import com.zionhuang.music.viewmodels.LibrarySongsViewModel

@Composable
fun LibraryScreen(
    navController: NavController,
    songsViewModel: LibrarySongsViewModel = hiltViewModel(),
    artistsViewModel: LibraryArtistsViewModel = hiltViewModel(),
    albumsViewModel: LibraryAlbumsViewModel = hiltViewModel(),
    playlistsViewModel: LibraryPlaylistsViewModel = hiltViewModel(),
) {
    val playerConnection = LocalPlayerConnection.current ?: return
    val songs by songsViewModel.allSongs.collectAsState()
    val artists by artistsViewModel.allArtists.collectAsState()
    val albums by albumsViewModel.allAlbums.collectAsState()
    val playlists by playlistsViewModel.allPlaylists.collectAsState()

    var activeFilter by remember { mutableStateOf("All") }
    val filters = listOf("All", "Playlists", "Artists", "Albums", "Songs")

    LazyColumn(
        Modifier
            .fillMaxSize()
            .background(hitoriColors.Bg),
        contentPadding = LocalPlayerAwareWindowInsets.current.asPaddingValues()
    ) {
        item {
            Column(
                Modifier
                    .fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(hitoriColors.Bg, hitoriColors.Bg, Color.Transparent)))
                    .padding(top = 16.dp, start = 22.dp, end = 22.dp, bottom = 10.dp)
            ) {
                Text(
                    text = "Library",
                    fontFamily = FrauncesFont,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = hitoriColors.Text
                )
                Spacer(Modifier.height(14.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(filters) { f ->
                        HitoriFilterChip(label = f, active = f == activeFilter) { activeFilter = f }
                    }
                }
            }
        }

        when (activeFilter) {
            "All" -> {
                // Combine some items or just show playlists as default
                playlists?.let { list ->
                    items(list) { item ->
                        LibraryRow(
                            title = item.playlist.name,
                            subtitle = "Playlist",
                            thumbnailUrl = null,
                            onClick = { navController.navigate("local_playlist/${item.playlist.id}") }
                        )
                    }
                }
            }
            "Playlists" -> {
                playlists?.let { list ->
                    items(list) { item ->
                        LibraryRow(
                            title = item.playlist.name,
                            subtitle = "Playlist",
                            thumbnailUrl = null,
                            onClick = { navController.navigate("local_playlist/${item.playlist.id}") }
                        )
                    }
                }
            }
            "Artists" -> {
                artists?.let { list ->
                    items(list) { item ->
                        LibraryRow(
                            title = item.artist.name,
                            subtitle = "Artist",
                            thumbnailUrl = item.artist.thumbnailUrl,
                            isArtist = true,
                            onClick = { navController.navigate("artist/${item.id}") }
                        )
                    }
                }
            }
            "Albums" -> {
                albums?.let { list ->
                    items(list) { item ->
                        LibraryRow(
                            title = item.album.title,
                            subtitle = item.artists.joinToString { it.name },
                            thumbnailUrl = item.album.thumbnailUrl,
                            onClick = { navController.navigate("album/${item.id}") }
                        )
                    }
                }
            }
            "Songs" -> {
                songs?.let { list ->
                    items(list) { item ->
                        LibraryRow(
                            title = item.song.title,
                            subtitle = item.artists.joinToString { it.name },
                            thumbnailUrl = item.song.thumbnailUrl,
                            onClick = {
                                // Play song radio or similar
                            }
                        )
                    }
                }
            }
        }
    }
}
