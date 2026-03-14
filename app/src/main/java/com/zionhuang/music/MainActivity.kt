package com.zionhuang.music

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.zionhuang.music.playback.PlayerConnection
import com.zionhuang.music.ui.hitori.HitoriTheme
import com.zionhuang.music.ui.hitori.ThemeMode
import com.zionhuang.music.ui.hitori.ThemeViewModel
import com.zionhuang.music.ui.hitori.components.HitoriMiniPlayer
import com.zionhuang.music.ui.hitori.components.HitoriTabBar
import com.zionhuang.music.ui.hitori.components.NowPlayingSheet
import com.zionhuang.music.ui.hitori.hitoriColors
import com.zionhuang.music.ui.screens.Screens
import com.zionhuang.music.ui.screens.HomeScreen
import com.zionhuang.music.ui.screens.LibraryScreen
import com.zionhuang.music.ui.screens.settings.SettingsScreen
import com.zionhuang.music.ui.screens.search.OnlineSearchResult
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import javax.inject.Inject
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.lifecycleScope
import com.zionhuang.music.playback.MusicService
import com.zionhuang.music.db.MusicDatabase
import com.zionhuang.music.playback.DownloadUtil
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarScrollBehavior
import com.zionhuang.music.ui.screens.navigationBuilder
import com.zionhuang.music.ui.utils.appBarScrollBehavior
import androidx.core.net.toUri
import androidx.core.util.Consumer
import com.zionhuang.innertube.YouTube
import com.zionhuang.music.utils.reportException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var database: MusicDatabase
    @Inject
    lateinit var downloadUtil: DownloadUtil

    private val themeViewModel: ThemeViewModel by viewModels()

    private var playerConnection by mutableStateOf<PlayerConnection?>(null)
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            if (service is MusicService.MusicBinder) {
                playerConnection = PlayerConnection(this@MainActivity, service, database, lifecycleScope)
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            playerConnection?.dispose()
            playerConnection = null
        }
    }

    override fun onStart() {
        super.onStart()
        startService(Intent(this, MusicService::class.java))
        bindService(Intent(this, MusicService::class.java), serviceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onStop() {
        unbindService(serviceConnection)
        super.onStop()
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            val themeMode by themeViewModel.themeMode.collectAsState()

            HitoriTheme(themeMode = themeMode) {
                val navController = rememberNavController()
                var showNowPlaying by remember { mutableStateOf(false) }

                // Track player position for MiniPlayer
                var position by remember { mutableStateOf(0L) }
                var duration by remember { mutableStateOf(0L) }

                LaunchedEffect(playerConnection) {
                    while (true) {
                        playerConnection?.player?.let {
                            position = it.currentPosition
                            duration = it.duration
                        }
                        delay(1000)
                    }
                }

                val coroutineScope = rememberCoroutineScope()
                DisposableEffect(Unit) {
                    val listener = Consumer<Intent> { intent ->
                        val uri = intent.data ?: intent.extras?.getString(Intent.EXTRA_TEXT)?.toUri() ?: return@Consumer
                        when (val path = uri.pathSegments.firstOrNull()) {
                            "playlist" -> uri.getQueryParameter("list")?.let { playlistId ->
                                if (playlistId.startsWith("OLAK5uy_")) {
                                    coroutineScope.launch {
                                        YouTube.albumSongs(playlistId).onSuccess { songs ->
                                            songs.firstOrNull()?.album?.id?.let { browseId ->
                                                navController.navigate("album/$browseId")
                                            }
                                        }.onFailure {
                                            reportException(it)
                                        }
                                    }
                                } else {
                                    navController.navigate("online_playlist/$playlistId")
                                }
                            }

                            "channel", "c" -> uri.lastPathSegment?.let { artistId ->
                                navController.navigate("artist/$artistId")
                            }

                            else -> when {
                                path == "watch" -> uri.getQueryParameter("v")
                                uri.host == "youtu.be" -> path
                                else -> null
                            }?.let { videoId ->
                                // Handle shared song if needed
                            }
                        }
                    }

                    addOnNewIntentListener(listener)
                    onDispose { removeOnNewIntentListener(listener) }
                }

                CompositionLocalProvider(
                    LocalPlayerConnection provides playerConnection,
                    LocalDatabase provides database,
                    LocalDownloadUtil provides downloadUtil,
                    LocalPlayerAwareWindowInsets provides WindowInsets.systemBars
                ) {
                    val playerConnectionVal = playerConnection
                    Box(Modifier.fillMaxSize().background(hitoriColors.Bg)) {
                        Scaffold(
                            bottomBar = {
                                if (playerConnectionVal != null) {
                                    Column {
                                        HitoriMiniPlayer(
                                            onTap = { showNowPlaying = true },
                                            position = position,
                                            duration = duration
                                        )
                                        HitoriTabBar(navController)
                                    }
                                }
                            },
                            containerColor = Color.Transparent,
                            contentWindowInsets = WindowInsets(0, 0, 0, 0)
                        ) { padding ->
                            HitoriNavHost(
                                navController = navController,
                                padding = padding
                            )
                        }
                        NowPlayingSheet(
                            visible = showNowPlaying,
                            onDismiss = { showNowPlaying = false },
                            position = position,
                            duration = duration
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HitoriNavHost(
    navController: NavHostController,
    padding: PaddingValues
) {
    val scrollBehavior = appBarScrollBehavior()

    NavHost(
        navController = navController,
        startDestination = Screens.Home.route,
        modifier = Modifier.padding(padding),
        enterTransition = { fadeIn(tween(220)) + slideInVertically(tween(220)) { (it * 0.04f).toInt() } },
        exitTransition = { fadeOut(tween(220)) }
    ) {
        navigationBuilder(navController, scrollBehavior, BuildConfig.VERSION_NAME)
    }
}
