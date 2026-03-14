package com.zionhuang.music.ui.hitori

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

object DarkColors {
    val Bg        = Color(0xFF0A0A08)
    val Bg2       = Color(0xFF111110)
    val Card      = Color(0xFF1C1C18)
    val Card2     = Color(0xFF262620)
    val Border    = Color(0x14FF69B4) // Pinkish border
    val Accent    = Color(0xFFFFB6C1) // Light Pink
    val Accent2   = Color(0xFFFF69B4) // Hot Pink
    val AccentDim = Color(0x1FFF69B4)
    val Text      = Color(0xFFFFFFFF)
    val Text2     = Color(0x8CFFFFFF)
    val Text3     = Color(0x47FFFFFF)
    val TabBg     = Color(0xF20A0A08)
    val SheetBg   = Color(0xFF0A0A08)
    val InputBg   = Color(0xFF1C1C18)
    val SetGroup  = Color(0xFF1C1C18)
}

object LightColors {
    val Bg        = Color(0xFFFDF0F3) // Very light pink bg
    val Bg2       = Color(0xFFF8E2E7)
    val Card      = Color(0xFFFFFFFF)
    val Card2     = Color(0xFFFAEAEE)
    val Border    = Color(0x24FF69B4)
    val Accent    = Color(0xFFFF69B4)
    val Accent2   = Color(0xFFFF1493)
    val AccentDim = Color(0x1AFF69B4)
    val Text      = Color(0xFF0A0A08)
    val Text2     = Color(0x8C0A0A08)
    val Text3     = Color(0x4D0A0A08)
    val TabBg     = Color(0xF5FDF0F3)
    val SheetBg   = Color(0xFFFDF0F3)
    val InputBg   = Color(0xFFFFFFFF)
    val SetGroup  = Color(0xFFFFFFFF)
}

data class HitoriColorScheme(
    val Bg: Color,
    val Bg2: Color,
    val Card: Color,
    val Card2: Color,
    val Border: Color,
    val Accent: Color,
    val Accent2: Color,
    val AccentDim: Color,
    val Text: Color,
    val Text2: Color,
    val Text3: Color,
    val TabBg: Color,
    val SheetBg: Color,
    val InputBg: Color,
    val SetGroup: Color,
    val isDark: Boolean
)

val LocalHitoriColors = staticCompositionLocalOf<HitoriColorScheme> {
    error("No HitoriColorScheme provided")
}

enum class ThemeMode { DARK, LIGHT, SYSTEM }

val JakartaFont = FontFamily.Default
val FrauncesFont = FontFamily.Default

val HitoriTypography = Typography(
    bodyLarge = TextStyle(
        fontFamily = JakartaFont,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FrauncesFont,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = JakartaFont,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)

@Composable
fun HitoriTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    content: @Composable () -> Unit
) {
    val isDark = when (themeMode) {
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    val colors = if (isDark) {
        HitoriColorScheme(
            Bg = DarkColors.Bg,
            Bg2 = DarkColors.Bg2,
            Card = DarkColors.Card,
            Card2 = DarkColors.Card2,
            Border = DarkColors.Border,
            Accent = DarkColors.Accent,
            Accent2 = DarkColors.Accent2,
            AccentDim = DarkColors.AccentDim,
            Text = DarkColors.Text,
            Text2 = DarkColors.Text2,
            Text3 = DarkColors.Text3,
            TabBg = DarkColors.TabBg,
            SheetBg = DarkColors.SheetBg,
            InputBg = DarkColors.InputBg,
            SetGroup = DarkColors.SetGroup,
            isDark = true
        )
    } else {
        HitoriColorScheme(
            Bg = LightColors.Bg,
            Bg2 = LightColors.Bg2,
            Card = LightColors.Card,
            Card2 = LightColors.Card2,
            Border = LightColors.Border,
            Accent = LightColors.Accent,
            Accent2 = LightColors.Accent2,
            AccentDim = LightColors.AccentDim,
            Text = LightColors.Text,
            Text2 = LightColors.Text2,
            Text3 = LightColors.Text3,
            TabBg = LightColors.TabBg,
            SheetBg = LightColors.SheetBg,
            InputBg = LightColors.InputBg,
            SetGroup = LightColors.SetGroup,
            isDark = false
        )
    }

    CompositionLocalProvider(
        LocalHitoriColors provides colors
    ) {
        MaterialTheme(
            typography = HitoriTypography,
            content = content
        )
    }
}

val hitoriColors: HitoriColorScheme
    @Composable
    @ReadOnlyComposable
    get() = LocalHitoriColors.current
