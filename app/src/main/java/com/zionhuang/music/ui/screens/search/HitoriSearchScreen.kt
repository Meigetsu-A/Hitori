package com.zionhuang.music.ui.screens.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.zionhuang.music.R
import com.zionhuang.music.playback.queues.YouTubeQueue
import com.zionhuang.music.ui.hitori.FrauncesFont
import com.zionhuang.music.ui.hitori.hitoriColors
import com.zionhuang.music.ui.hitori.components.HitoriSearchBar
import com.zionhuang.music.ui.hitori.components.SearchResultRow
import com.zionhuang.music.viewmodels.OnlineSearchSuggestionViewModel
import com.zionhuang.music.models.toMediaMetadata
import com.zionhuang.music.utils.urlEncode

@Composable
fun HitoriSearchScreen(
    navController: NavController,
    viewModel: OnlineSearchSuggestionViewModel = hiltViewModel(),
) {
    val playerConnection = LocalPlayerConnection.current ?: return
    val query by viewModel.query.collectAsState()
    val viewState by viewModel.viewState.collectAsState()

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
                    query = query,
                    onQueryChange = { viewModel.query.value = it },
                    onSearch = {
                        if (it.isNotEmpty()) {
                            navController.navigate("search/${it.urlEncode()}")
                        }
                    }
                )
            }
        }

        if (viewState.items.isEmpty()) {
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
            // Browse categories based on suggestions or history could go here
            items(viewState.history) { history ->
                SearchResultRowForQuery(history.query, isHistory = true) {
                    navController.navigate("search/${history.query.urlEncode()}")
                }
            }
            items(viewState.suggestions) { suggestion ->
                SearchResultRowForQuery(suggestion, isHistory = false) {
                    navController.navigate("search/${suggestion.urlEncode()}")
                }
            }
        } else {
            items(viewState.items) { result ->
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

@Composable
fun SearchResultRowForQuery(query: String, isHistory: Boolean, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 22.dp, vertical = 12.dp),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {
        androidx.compose.material3.Icon(
            painter = androidx.compose.ui.res.painterResource(if (isHistory) R.drawable.history else R.drawable.search),
            contentDescription = null,
            tint = hitoriColors.Text3,
            modifier = Modifier.size(18.dp)
        )
        Spacer(Modifier.width(16.dp))
        Text(text = query, color = hitoriColors.Text, fontSize = 15.sp, fontWeight = FontWeight.Medium)
    }
}
