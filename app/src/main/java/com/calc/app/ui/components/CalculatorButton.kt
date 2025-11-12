package com.calc.app.ui.components

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun CalculatorButton(
	label: String,
	onClick: () -> Unit,
	tonal: Boolean = false,
	modifier: Modifier = Modifier,
	capsule: Boolean = false
) {
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
		modifier = modifier
			.fillMaxWidth()
			.aspectRatio(if (capsule) 2f else 1f) // Wider for capsule
	) {
		Text(
			text = label,
			style = if (capsule) MaterialTheme.typography.titleMedium else MaterialTheme.typography.headlineMedium
		)
	}
}


