package com.zionhuang.music.ui.hitori.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import com.zionhuang.music.R
import com.zionhuang.music.ui.hitori.hitoriColors
import com.zionhuang.music.ui.screens.Screens

@Composable
fun HitoriTabBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val items = listOf(
        TabItem(Screens.Home.route, "Home", R.drawable.home),
        TabItem(Screens.Search.route, "Search", R.drawable.search),
        TabItem(Screens.Library.route, "Library", R.drawable.library_music),
        TabItem("settings", "Settings", R.drawable.settings)
    )

    Row(
        Modifier
            .fillMaxWidth()
            .background(hitoriColors.TabBg)
            .height(64.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEach { item ->
            val selected = currentDestination?.hierarchy?.any { it.route == item.route } == true
            HitoriTabItem(item, selected) {
                if (!selected) {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }
        }
    }
}

data class TabItem(val route: String, val label: String, val iconRes: Int)

@Composable
fun HitoriTabItem(item: TabItem, selected: Boolean, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed) 0.88f else 1f, label = "")

    Column(
        Modifier
            .scale(scale)
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(item.iconRes),
            contentDescription = item.label,
            tint = if (selected) hitoriColors.Accent else hitoriColors.Text3,
            modifier = Modifier.size(24.dp)
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = item.label,
            fontSize = 10.sp,
            color = if (selected) hitoriColors.Accent else hitoriColors.Text3
        )
    }
}
