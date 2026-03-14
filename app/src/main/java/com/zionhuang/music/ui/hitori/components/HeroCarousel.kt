package com.zionhuang.music.ui.hitori.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zionhuang.innertube.models.YTItem
import com.zionhuang.music.ui.hitori.FrauncesFont
import com.zionhuang.music.ui.hitori.hitoriColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun HeroCarousel(items: List<YTItem>, onItemClick: (YTItem) -> Unit) {
    if (items.isEmpty()) return

    val pageCount = items.size.coerceAtMost(5)
    val pagerState = rememberPagerState(pageCount = { pageCount })
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        while (true) {
            delay(4200)
            val nextPage = (pagerState.currentPage + 1) % pagerState.pageCount
            pagerState.animateScrollToPage(
                page = nextPage,
                animationSpec = tween(420, easing = FastOutSlowInEasing)
            )
        }
    }

    Column {
        HorizontalPager(state = pagerState, modifier = Modifier.fillMaxWidth().height(210.dp)) { page ->
            val item = items[page]
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { onItemClick(item) }
            ) {
                MusicThumbnail(
                    url = item.thumbnail,
                    modifier = Modifier.fillMaxSize(),
                    shape = RectangleShape,
                    contentScale = ContentScale.Crop
                )
                // Scrim
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, Color(0x26000000), Color(0xD9000000))
                            )
                        )
                )
                // Labels
                Column(Modifier.align(Alignment.BottomStart).padding(18.dp)) {
                    Text(
                        text = item.title,
                        color = Color.White,
                        fontFamily = FrauncesFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        lineHeight = 26.4.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
        // Dots
        Row(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(pagerState.pageCount) { i ->
                val active = pagerState.currentPage == i
                Box(
                    Modifier
                        .padding(horizontal = 3.dp)
                        .animateContentSize(tween(280))
                        .width(if (active) 18.dp else 6.dp)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(if (active) hitoriColors.Accent else hitoriColors.Text3)
                        .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) {
                            scope.launch { pagerState.animateScrollToPage(i) }
                        }
                )
            }
        }
    }
}
