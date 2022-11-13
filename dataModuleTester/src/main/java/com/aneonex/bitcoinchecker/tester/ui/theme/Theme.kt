package com.aneonex.bitcoinchecker.tester.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = Purple500,
//    primaryVariant = Purple700,
//    secondary = Teal200

/*
    primary = black,
    primaryVariant = black,
    onPrimary = white,
    secondary = teal200,
    secondaryVariant = teal700,
    onSecondary = black,
    surface = white,
    onSurface = black,
    background = white,
    onBackground = black,
*/
)

private val DarkColorScheme = darkColorScheme(
    primary = Purple200,
//    primaryVariant = Purple700,
    secondary = Teal200

/*
    primary = white,
    primaryVariant = white,
    onPrimary = black,
    secondary = teal200,
    secondaryVariant = teal200,
    onSecondary = white,
    surface = black,
    onSurface = white,
    background = black,
    onBackground = white,
 */
)

@Composable
fun MyAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        shapes = AppShapes,
        content = content
    )
}