package com.calc.app.ui.components

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration

@Composable
fun CalculatorButton(
	label: String,
	onClick: () -> Unit,
	tonal: Boolean = false,
	modifier: Modifier = Modifier,
	capsule: Boolean = false,
	aspectRatio: Float? = null,
	fillMaxWidth: Boolean = true
) {
	val configuration = LocalConfiguration.current
	val screenWidthDp = configuration.screenWidthDp
	val screenHeightDp = configuration.screenHeightDp

	// Determine responsive sizing based on screen dimensions
	val isSmallScreen = screenWidthDp < 360 || screenHeightDp < 640
	val isLargeScreen = screenWidthDp >= 600

	// Responsive aspect ratios
	val defaultAspectRatio = when {
		capsule && isSmallScreen -> 2.3f  // More compact on small screens
		capsule && isLargeScreen -> 2.5f  // Less wide on large screens for scientific functions
		capsule -> 2.5f  // More compact default capsule ratio for scientific functions
		isSmallScreen -> 1.6f  // Make buttons shorter to guarantee fit
		isLargeScreen -> 1.4f  // Make buttons shorter
		else -> 1.5f  // Make buttons shorter
	}

	// Responsive typography
	val textStyle = when {
		capsule && isSmallScreen -> MaterialTheme.typography.titleSmall
		capsule && isLargeScreen -> MaterialTheme.typography.titleLarge
		capsule -> MaterialTheme.typography.titleMedium
		isSmallScreen -> MaterialTheme.typography.headlineSmall
		isLargeScreen -> MaterialTheme.typography.displaySmall
		else -> MaterialTheme.typography.headlineMedium
	}

	val colors = if (tonal) {
		ButtonDefaults.filledTonalButtonColors()
	} else {
		ButtonDefaults.buttonColors()
	}

	val shape = if (capsule) {
		MaterialTheme.shapes.extraLarge // Capsule shape
	} else {
		MaterialTheme.shapes.medium // Default button shape
	}

	Button(
		onClick = onClick,
		colors = colors,
		shape = shape,
		modifier = if (fillMaxWidth) {
			modifier
				.fillMaxWidth()
				.aspectRatio(aspectRatio ?: defaultAspectRatio)
		} else {
			modifier
		}
	) {
		Text(
			text = label,
			style = textStyle
		)
	}
}


