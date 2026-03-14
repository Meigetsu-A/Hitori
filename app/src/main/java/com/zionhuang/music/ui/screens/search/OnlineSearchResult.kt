package com.zionhuang.music.ui.screens.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
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
import com.zionhuang.music.ui.hitori.hitoriColors
import com.zionhuang.music.ui.hitori.components.SearchResultRow
import com.zionhuang.music.viewmodels.OnlineSearchViewModel
import com.zionhuang.music.models.toMediaMetadata

@Composable
fun OnlineSearchResult(
    navController: NavController,
    viewModel: OnlineSearchViewModel = hiltViewModel(),
) {
    val playerConnection = LocalPlayerConnection.current ?: return
    val searchFilter by viewModel.filter.collectAsState()
    val searchSummary = viewModel.summaryPage

    val results = if (searchFilter == null) {
        searchSummary?.summaries?.flatMap { it.items } ?: emptyList()
    } else {
        viewModel.viewStateMap[searchFilter!!.value]?.items ?: emptyList()
    }

    LazyColumn(
        Modifier
            .fillMaxSize()
            .background(hitoriColors.Bg),
        contentPadding = PaddingValues(top = 16.dp, bottom = 20.dp)
    ) {
        items(results) { item ->
            SearchResultRow(
                item = item,
                onClick = {
                    when (item) {
                        is SongItem -> playerConnection.playQueue(YouTubeQueue(item.endpoint ?: WatchEndpoint(videoId = item.id), item.toMediaMetadata()))
                        is AlbumItem -> navController.navigate("album/${item.id}")
                        is ArtistItem -> navController.navigate("artist/${item.id}")
                        is PlaylistItem -> navController.navigate("online_playlist/${item.id}")
                    }
                }
            )
        }
    }
}
