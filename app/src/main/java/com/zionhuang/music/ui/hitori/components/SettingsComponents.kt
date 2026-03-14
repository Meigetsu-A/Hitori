package com.zionhuang.music.ui.hitori.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zionhuang.music.ui.hitori.ThemeMode
import com.zionhuang.music.ui.hitori.hitoriColors

@Composable
fun AppearanceSection(themeMode: ThemeMode, onThemeChange: (ThemeMode) -> Unit) {
    Column(Modifier.padding(horizontal = 22.dp, vertical = 0.dp).padding(bottom = 18.dp)) {
        Text(
            text = "Appearance",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = hitoriColors.Text3,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ThemeOption("🌑", "Dark", Color(0xFF0A0A08), Color(0xFFFFB6C1), null, themeMode == ThemeMode.DARK, Modifier.weight(1f)) { onThemeChange(ThemeMode.DARK) }
            ThemeOption("☀️", "Light", Color(0xFFFDF0F3), Color(0xFFFF69B4), null, themeMode == ThemeMode.LIGHT, Modifier.weight(1f)) { onThemeChange(ThemeMode.LIGHT) }
            ThemeOption(
                "⚙️", "System", null, Color(0xFF888888),
                Brush.linearGradient(listOf(Color(0xFF0A0A08), Color(0xFF0A0A08), Color(0xFFFDF0F3))),
                themeMode == ThemeMode.SYSTEM, Modifier.weight(1f)
            ) { onThemeChange(ThemeMode.SYSTEM) }
        }
    }
}

@Composable
fun ThemeOption(
    emoji: String,
    label: String,
    bgColor: Color?,
    accentColor: Color,
    bgBrush: Brush?,
    selected: Boolean,
    modifier: Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .border(2.dp, if (selected) hitoriColors.Accent else Color.Transparent, RoundedCornerShape(12.dp))
            .background(hitoriColors.Card)
            .clickable { onClick() }
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .then(if (bgBrush != null) Modifier.background(bgBrush) else Modifier.background(bgColor ?: Color.Gray)),
            contentAlignment = Alignment.Center
        ) {
            Text(emoji, fontSize = 20.sp)
        }
        Spacer(Modifier.height(8.dp))
        Text(label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = if (selected) hitoriColors.Accent else hitoriColors.Text2)
    }
}
