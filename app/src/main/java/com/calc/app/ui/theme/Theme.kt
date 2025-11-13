package com.calc.app.ui.theme

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

// Light Theme Colors
private val LightColors = lightColorScheme(
	primary = Color(0xFF1976D2),
	onPrimary = Color.White,
	primaryContainer = Color(0xFFBBDEFB),
	onPrimaryContainer = Color(0xFF003C8F),
	secondary = Color(0xFF03A9F4),
	onSecondary = Color.White,
	secondaryContainer = Color(0xFFB3E5FC),
	onSecondaryContainer = Color(0xFF01579B),
	tertiary = Color(0xFF0277BD),
	onTertiary = Color.White,
	background = Color(0xFFFAFAFA),
	onBackground = Color(0xFF212121),
	surface = Color.White,
	onSurface = Color(0xFF212121),
	surfaceVariant = Color(0xFFF5F5F5),
	onSurfaceVariant = Color(0xFF424242),
	error = Color(0xFFD32F2F),
	onError = Color.White,
	outline = Color(0xFFBDBDBD),
	outlineVariant = Color(0xFFE0E0E0)
)

// Dark Theme Colors
private val DarkColors = darkColorScheme(
	primary = Color(0xFF90CAF9),
	onPrimary = Color(0xFF003C8F),
	primaryContainer = Color(0xFF1565C0),
	onPrimaryContainer = Color(0xFFBBDEFB),
	secondary = Color(0xFF81D4FA),
	onSecondary = Color(0xFF01579B),
	secondaryContainer = Color(0xFF0277BD),
	onSecondaryContainer = Color(0xFFB3E5FC),
	tertiary = Color(0xFF4FC3F7),
	onTertiary = Color(0xFF01579B),
	background = Color(0xFF121212),
	onBackground = Color(0xFFE0E0E0),
	surface = Color(0xFF1E1E1E),
	onSurface = Color(0xFFE0E0E0),
	surfaceVariant = Color(0xFF2C2C2C),
	onSurfaceVariant = Color(0xFFBDBDBD),
	error = Color(0xFFEF5350),
	onError = Color(0xFF1E1E1E),
	outline = Color(0xFF616161),
	outlineVariant = Color(0xFF424242)
)

@Composable
fun CalcTheme(
	darkTheme: Boolean = isSystemInDarkTheme(),
	dynamicColor: Boolean = false, // Disabled by default for consistency
	content: @Composable () -> Unit
) {
	val colorScheme = when {
		dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
			val context = LocalContext.current
			if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
		}
		darkTheme -> DarkColors
		else -> LightColors
	}

	MaterialTheme(
		colorScheme = colorScheme,
		typography = Typography,
		content = content
	)
}


