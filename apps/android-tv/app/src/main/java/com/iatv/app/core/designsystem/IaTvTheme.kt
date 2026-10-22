package com.iatv.app.core.designsystem

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.BringIntoViewSpec
import androidx.compose.foundation.gestures.LocalBringIntoViewSpec
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

object IaTvColors {
    val Background = Color(0xFF060D1B)
    val Surface = Color(0xFF101D32)
    val Raised = Color(0xFF192B45)
    val Border = Color(0xFF2D415B)
    val Cyan = Color(0xFF66E4F5)
    val Blue = Color(0xFF3278D4)
    val Purple = Color(0xFFBA9AFF)
    val Text = Color(0xFFF3F6FC)
    val Muted = Color(0xFFA8B8D0)
    val Warning = Color(0xFFFFD58D)
    val Live = Color(0xFFFF4158)
    val Transparent = Color.Transparent
    val Art = listOf(Color(0xFF245A78), Color(0xFF55509A), Color(0xFF236C68), Color(0xFF8C5B53))
}
object IaTvSpacing {
    val Small = 8.dp
    val Medium = 16.dp
    val Large = 24.dp
    val Page = 28.dp
    val Rail = 64.dp
    val RailExpanded = 192.dp
    val Hero = 240.dp
    val PosterWidth = 140.dp
    val LandscapeWidth = 180.dp
}
object IaTvShapes {
    val Card = RoundedCornerShape(12.dp)
    val Button = RoundedCornerShape(10.dp)
    val Pill = RoundedCornerShape(50)
}
object IaTvMotion { const val FocusMillis = 160; const val FocusScale = 1.06f }
val IaTvTypography = Typography(
    displaySmall = TextStyle(fontSize = 42.sp, lineHeight = 46.sp, fontWeight = FontWeight.Bold),
    headlineLarge = TextStyle(fontSize = 30.sp, lineHeight = 36.sp, fontWeight = FontWeight.SemiBold),
    titleLarge = TextStyle(fontSize = 22.sp, lineHeight = 28.sp, fontWeight = FontWeight.SemiBold),
    titleMedium = TextStyle(fontSize = 16.sp, lineHeight = 21.sp, fontWeight = FontWeight.SemiBold),
    bodyLarge = TextStyle(fontSize = 16.sp, lineHeight = 24.sp),
    bodyMedium = TextStyle(fontSize = 14.sp, lineHeight = 21.sp),
    labelMedium = TextStyle(fontSize = 12.sp, lineHeight = 17.sp, fontWeight = FontWeight.Medium)
)
@OptIn(ExperimentalFoundationApi::class)
private val visibleFocus = object : BringIntoViewSpec {
    override fun calculateScrollDistance(offset: Float, size: Float, containerSize: Float): Float = when {
        offset < 0f -> offset
        offset + size > containerSize -> offset + size - containerSize
        else -> 0f
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable fun IaTvTheme(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalBringIntoViewSpec provides visibleFocus) {
    MaterialTheme(colorScheme = darkColorScheme(primary = IaTvColors.Cyan,
        secondary = IaTvColors.Purple, background = IaTvColors.Background,
        surface = IaTvColors.Surface, onSurface = IaTvColors.Text,
        onBackground = IaTvColors.Text, onPrimary = IaTvColors.Background),
        typography = IaTvTypography, content = content)
    }
}
