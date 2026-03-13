package com.zionhuang.music.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.zionhuang.music.LocalPlayerAwareWindowInsets
import com.zionhuang.music.R
import com.zionhuang.music.ui.component.NavigationTile

@Composable
fun LibraryScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(LocalPlayerAwareWindowInsets.current.asPaddingValues())
    ) {
        Text(
            text = stringResource(R.string.library),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Horizontal))
                .padding(horizontal = 16.dp, vertical = 16.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
        ) {
            NavigationTile(
                title = stringResource(R.string.songs),
                icon = R.drawable.music_note,
                onClick = { navController.navigate("songs") },
                modifier = Modifier.weight(1f)
            )

            NavigationTile(
                title = stringResource(R.string.artists),
                icon = R.drawable.artist,
                onClick = { navController.navigate("artists") },
                modifier = Modifier.weight(1f)
            )

            NavigationTile(
                title = stringResource(R.string.albums),
                icon = R.drawable.album,
                onClick = { navController.navigate("albums") },
                modifier = Modifier.weight(1f)
            )

            NavigationTile(
                title = stringResource(R.string.playlists),
                icon = R.drawable.queue_music,
                onClick = { navController.navigate("playlists") },
                modifier = Modifier.weight(1f)
            )
        }
    }
}
