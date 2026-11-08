package com.melven.plexo.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Identidade visual do Plexo
 * --------------------------
 * Tema escuro "aurora": preto tinta profundo, violeta electrico, magenta quente
 * e um ciano de contraste. A ideia e parecer um instrumento de precisao, nao um brinquedo.
 */
object PlexoColors {
    val Ink = Color(0xFF07060D)
    val InkSoft = Color(0xFF0C0A16)
    val Surface = Color(0xFF12101F)
    val SurfaceHi = Color(0xFF1A1730)
    val Stroke = Color(0x1FFFFFFF)
    val StrokeStrong = Color(0x33FFFFFF)

    val Violet = Color(0xFF7C5CFF)
    val VioletDeep = Color(0xFF4C2BD9)
    val Magenta = Color(0xFFFF3D9A)
    val Cyan = Color(0xFF22D3EE)
    val Amber = Color(0xFFFFC857)
    val Mint = Color(0xFF4ADE80)

    val TextPrimary = Color(0xFFF6F4FF)
    val TextSecondary = Color(0xFFA7A2C6)
    val TextMuted = Color(0xFF6E6A8A)

    val Aurora = listOf(Violet, Magenta)
    val AuroraSoft = listOf(Violet.copy(alpha = 0.85f), Magenta.copy(alpha = 0.75f))
    val Cool = listOf(Cyan, Violet)
}

val AuroraBrush: Brush = Brush.horizontalGradient(PlexoColors.Aurora)
val CoolBrush: Brush = Brush.horizontalGradient(PlexoColors.Cool)

private val PlexoTypography = Typography(
    displayLarge = TextStyle(
        fontWeight = FontWeight.Black,
        fontSize = 44.sp,
        lineHeight = 46.sp,
        letterSpacing = (-1.5).sp
    ),
    displayMedium = TextStyle(
        fontWeight = FontWeight.Black,
        fontSize = 34.sp,
        lineHeight = 38.sp,
        letterSpacing = (-1).sp
    ),
    headlineMedium = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 30.sp,
        letterSpacing = (-0.5).sp
    ),
    titleLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 19.sp,
        lineHeight = 24.sp,
        letterSpacing = (-0.2).sp
    ),
    titleMedium = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 21.sp
    ),
    bodyLarge = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 22.sp
    ),
    bodyMedium = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 13.5.sp,
        lineHeight = 19.sp
    ),
    labelLarge = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 15.sp,
        letterSpacing = 0.2.sp
    ),
    labelMedium = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 11.5.sp,
        letterSpacing = 1.1.sp
    )
)

private val PlexoDarkScheme = darkColorScheme(
    primary = PlexoColors.Violet,
    onPrimary = Color.White,
    primaryContainer = PlexoColors.VioletDeep,
    onPrimaryContainer = PlexoColors.TextPrimary,
    secondary = PlexoColors.Magenta,
    onSecondary = Color.White,
    tertiary = PlexoColors.Cyan,
    onTertiary = PlexoColors.Ink,
    background = PlexoColors.Ink,
    onBackground = PlexoColors.TextPrimary,
    surface = PlexoColors.Surface,
    onSurface = PlexoColors.TextPrimary,
    surfaceVariant = PlexoColors.SurfaceHi,
    onSurfaceVariant = PlexoColors.TextSecondary,
    outline = PlexoColors.StrokeStrong,
    error = Color(0xFFFF6B81)
)

@Composable
fun PlexoTheme(content: @Composable () -> Unit) {
    @Suppress("UNUSED_EXPRESSION")
    isSystemInDarkTheme()
    MaterialTheme(
        colorScheme = PlexoDarkScheme,
        typography = PlexoTypography,
        content = content
    )
}
