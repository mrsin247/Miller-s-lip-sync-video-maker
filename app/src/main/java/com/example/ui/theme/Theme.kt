package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = NeonViolet,       // #D0BCFF lavender
    secondary = NeonCyan,     // #B1D18A mint green
    tertiary = NeonGold,      // #FFC400 gold
    background = DeepSlate,    // #1C1B1F background
    surface = SurfaceSlate,    // #211F26 surface
    onPrimary = Purple40,      // #381E72 deep violet
    onSecondary = DeepSlate,
    onTertiary = DeepSlate,
    onBackground = LightGray,  // #E6E1E5 light text
    onSurface = LightGray,     // #E6E1E5 text
    surfaceVariant = SurfaceCard, // #2B2930 card background
    onSurfaceVariant = MutedText  // #CAC4D0 secondary text
  )

private val LightColorScheme = DarkColorScheme

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Force dark theme to match the premium "Immersive UI" aesthetic
  dynamicColor: Boolean = false, // Use our brand colors exactly
  content: @Composable () -> Unit,
) {
  val colorScheme = DarkColorScheme

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
