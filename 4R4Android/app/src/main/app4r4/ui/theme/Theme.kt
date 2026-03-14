package com.app4r4.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Green10 = Color(0xFF002204)
val Green20 = Color(0xFF003908)
val Green30 = Color(0xFF00530D)
val Green40 = Color(0xFF006E13)
val Green80 = Color(0xFF7EDB7B)
val Green90 = Color(0xFF99F794)

val GreenGrey30 = Color(0xFF316847)
val GreenGrey50 = Color(0xFF52A672)
val GreenGrey60 = Color(0xFF6DBF8A)
val GreenGrey80 = Color(0xFFB7EFCA)
val GreenGrey90 = Color(0xFFD3FBE4)

val DarkGreen10 = Color(0xFF0D1F12)
val DarkGreen20 = Color(0xFF1A3D24)
val DarkGreen30 = Color(0xFF275C35)
val DarkGreen40 = Color(0xFF347A47)
val DarkGreen80 = Color(0xFFA3D6B0)
val DarkGreen90 = Color(0xFFCDEDD7)

val LightGreenLightColorScheme = lightColorScheme(
    primary = Color(0xFF1B6B2F),
    onPrimary = Color.White,
    primaryContainer = Color(0xFF9DF5A9),
    onPrimaryContainer = Color(0xFF002109),
    secondary = Color(0xFF516350),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD3E8CF),
    onSecondaryContainer = Color(0xFF0F1F0E),
    tertiary = Color(0xFF39656B),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFBCEBF1),
    onTertiaryContainer = Color(0xFF001F23),
    background = Color(0xFFF8FDF7),
    onBackground = Color(0xFF191D18),
    surface = Color(0xFFF8FDF7),
    onSurface = Color(0xFF191D18),
    surfaceVariant = Color(0xFFDDE5D9),
    onSurfaceVariant = Color(0xFF414940),
)

val LightGreenDarkColorScheme = darkColorScheme(
    primary = Color(0xFF81D98F),
    onPrimary = Color(0xFF003914),
    primaryContainer = Color(0xFF005221),
    onPrimaryContainer = Color(0xFF9DF5A9),
    secondary = Color(0xFFB7CCB4),
    onSecondary = Color(0xFF233523),
    secondaryContainer = Color(0xFF394B38),
    onSecondaryContainer = Color(0xFFD3E8CF),
    tertiary = Color(0xFFA1CED5),
    onTertiary = Color(0xFF00363C),
    tertiaryContainer = Color(0xFF1F4D53),
    onTertiaryContainer = Color(0xFFBCEBF1),
    background = Color(0xFF111610),
    onBackground = Color(0xFFE1E4DE),
    surface = Color(0xFF111610),
    onSurface = Color(0xFFE1E4DE),
    surfaceVariant = Color(0xFF414940),
    onSurfaceVariant = Color(0xFFC1C9BC),
)

@Composable
fun App4R4Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) LightGreenDarkColorScheme else LightGreenLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
