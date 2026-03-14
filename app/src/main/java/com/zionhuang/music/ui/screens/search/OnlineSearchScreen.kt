package com.zionhuang.music.ui.screens.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.zionhuang.innertube.models.AlbumItem
import com.zionhuang.innertube.models.ArtistItem
import com.zionhuang.innertube.models.PlaylistItem
import com.zionhuang.innertube.models.SongItem
import com.zionhuang.innertube.models.WatchEndpoint
import com.zionhuang.music.LocalPlayerConnection
import com.zionhuang.music.playback.queues.YouTubeQueue
import com.zionhuang.music.ui.hitori.FrauncesFont
import com.zionhuang.music.ui.hitori.hitoriColors
import com.zionhuang.music.ui.hitori.components.HitoriSearchBar
import com.zionhuang.music.ui.hitori.components.SearchResultRow
import com.zionhuang.music.viewmodels.OnlineSearchViewModel
import com.zionhuang.music.models.toMediaMetadata
import com.zionhuang.music.utils.urlEncode

@Composable
fun OnlineSearchScreen(
    navController: NavController,
    viewModel: OnlineSearchViewModel = hiltViewModel(),
) {
    val playerConnection = LocalPlayerConnection.current ?: return
    var searchFieldText by remember { mutableStateOf(viewModel.query) }

    val searchSummary = viewModel.summaryPage
    val results = searchSummary?.summaries?.flatMap { it.items } ?: emptyList()

    LazyColumn(
        Modifier
            .fillMaxSize()
            .background(hitoriColors.Bg),
        contentPadding = PaddingValues(bottom = 20.dp)
    ) {
        item {
            Column(
                Modifier
                    .fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(hitoriColors.Bg, hitoriColors.Bg, Color.Transparent)))
                    .padding(top = 52.dp, start = 22.dp, end = 22.dp, bottom = 10.dp)
            ) {
                Text(
                    text = "Search",
                    fontFamily = FrauncesFont,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = hitoriColors.Text
                )
                Spacer(Modifier.height(14.dp))
                HitoriSearchBar(
                    query = searchFieldText,
                    onQueryChange = { searchFieldText = it },
                    onSearch = {
                        if (it.isNotEmpty()) {
                            navController.navigate("search/${it.urlEncode()}")
                        }
                    }
                )
            }
        }

        if (results.isEmpty()) {
            item {
                Text(
                    text = "BROWSE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.12.sp,
                    color = hitoriColors.Text3,
                    modifier = Modifier.padding(horizontal = 22.dp, vertical = 10.dp)
                )
            }
            // BrowseCategoriesGrid would go here
        } else {
            items(results) { result ->
                SearchResultRow(
                    item = result,
                    onClick = {
                        when (result) {
                            is SongItem -> playerConnection.playQueue(YouTubeQueue(result.endpoint ?: WatchEndpoint(videoId = result.id), result.toMediaMetadata()))
                            is AlbumItem -> navController.navigate("album/${result.id}")
                            is ArtistItem -> navController.navigate("artist/${result.id}")
                            is PlaylistItem -> navController.navigate("online_playlist/${result.id}")
                        }
                    }
                )
            }
        }
    }
}
