package com.zionhuang.music.ui.hitori.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.zionhuang.music.ui.hitori.hitoriColors

@Composable
fun HitoriToggle(checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    val offset by animateDpAsState(if (checked) 17.dp else 0.dp, spring(0.6f, 400f), label = "")
    Box(
        Modifier
            .width(42.dp)
            .height(25.dp)
            .clip(RoundedCornerShape(13.dp))
            .border(1.dp, if (checked) hitoriColors.Accent else hitoriColors.Border, RoundedCornerShape(13.dp))
            .background(if (checked) hitoriColors.Accent else hitoriColors.Card2)
            .clickable { onCheckedChange(!checked) }
    ) {
        Box(
            Modifier
                .offset(x = 3.dp + offset, y = 3.dp)
                .size(17.dp)
                .clip(CircleShape)
                .background(Color.White)
                .shadow(2.dp, CircleShape)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HitoriSlider(value: Float, onValueChange: (Float) -> Unit, modifier: Modifier) {
    Slider(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        colors = SliderDefaults.colors(
            thumbColor = hitoriColors.Accent,
            activeTrackColor = hitoriColors.Accent,
            inactiveTrackColor = hitoriColors.Card2
        ),
        thumb = {
            Box(
                Modifier
                    .size(13.dp)
                    .clip(CircleShape)
                    .background(hitoriColors.Accent)
            )
        }
    )
}
