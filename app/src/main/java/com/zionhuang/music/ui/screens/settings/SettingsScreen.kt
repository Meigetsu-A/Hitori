package com.zionhuang.music.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.zionhuang.music.LocalDatabase
import com.zionhuang.music.ui.hitori.FrauncesFont
import com.zionhuang.music.ui.hitori.ThemeMode
import com.zionhuang.music.ui.hitori.ThemeViewModel
import com.zionhuang.music.ui.hitori.hitoriColors
import com.zionhuang.music.ui.hitori.components.AppearanceSection
import com.zionhuang.music.viewmodels.AccountViewModel
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    navController: NavController,
    themeViewModel: ThemeViewModel = hiltViewModel(),
    accountViewModel: AccountViewModel = hiltViewModel()
) {
    val themeMode by themeViewModel.themeMode.collectAsState()
    val accountInfo by accountViewModel.accountInfo.collectAsState()
    val database = LocalDatabase.current
    val coroutineScope = rememberCoroutineScope()

    LazyColumn(
        Modifier
            .fillMaxSize()
            .background(hitoriColors.Bg),
        contentPadding = PaddingValues(bottom = 20.dp)
    ) {
        item {
            Text(
                text = "Settings",
                fontFamily = FrauncesFont,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = hitoriColors.Text,
                modifier = Modifier.padding(top = 52.dp, start = 22.dp, end = 22.dp, bottom = 0.dp)
            )
        }
        // Profile
        item {
            Row(
                Modifier.padding(horizontal = 22.dp, vertical = 18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(hitoriColors.Accent),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = accountInfo?.name?.firstOrNull()?.toString() ?: "H",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0A0A08)
                    )
                }
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(text = accountInfo?.name ?: "Guest", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = hitoriColors.Text)
                    Text(
                        text = accountInfo?.email ?: "Sign in to sync",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = hitoriColors.Accent,
                        modifier = Modifier.padding(top = 3.dp)
                    )
                }
            }
        }
        // Theme picker
        item {
            AppearanceSection(themeMode = themeMode, onThemeChange = { themeViewModel.setThemeMode(it) })
        }

        item {
            SettingsGroupLabel("Privacy")
            SettingsRow(label = "Clear Listen History") {
                coroutineScope.launch {
                    database.query {
                        clearListenHistory()
                    }
                }
            }
            SettingsRow(label = "Clear Search History") {
                coroutineScope.launch {
                    database.query {
                        clearSearchHistory()
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsGroupLabel(label: String) {
    Text(
        text = label.uppercase(),
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.12.sp,
        color = hitoriColors.Text3,
        modifier = Modifier.padding(horizontal = 22.dp, vertical = 10.dp)
    )
}

@Composable
fun SettingsRow(label: String, onClick: () -> Unit) {
    Box(
        Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 22.dp, vertical = 14.dp)
    ) {
        Text(text = label, color = hitoriColors.Text, fontSize = 15.sp)
    }
}
