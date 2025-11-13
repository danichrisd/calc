package com.calc.app.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.calc.app.R
import com.calc.app.ui.components.CalculatorButton
import com.calc.app.viewmodel.CalculatorViewModel
import com.calc.app.viewmodel.CalculatorViewModel.CalculatorKey
import androidx.compose.foundation.lazy.grid.GridItemSpan

@Composable
fun CalculatorScreen(
	vm: CalculatorViewModel = viewModel()
) {
	val configuration = LocalConfiguration.current
	val screenWidthDp = configuration.screenWidthDp
	val screenHeightDp = configuration.screenHeightDp

	// Determine responsive sizing
	val isSmallScreen = screenWidthDp < 360 || screenHeightDp < 640
	val isLargeScreen = screenWidthDp >= 600

	// Responsive spacing and padding
	val displayPadding = when {
		isSmallScreen -> PaddingValues(horizontal = 8.dp, vertical = 4.dp)
		isLargeScreen -> PaddingValues(horizontal = 16.dp, vertical = 8.dp)
		else -> PaddingValues(horizontal = 12.dp, vertical = 6.dp)
	}

	val controlPadding = when {
		isSmallScreen -> Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
		isLargeScreen -> Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
		else -> Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
	}

	val dividerPadding = when {
		isSmallScreen -> Modifier.padding(horizontal = 8.dp)
		isLargeScreen -> Modifier.padding(horizontal = 16.dp)
		else -> Modifier.padding(horizontal = 12.dp)
	}

	val gridSpacing = when {
		isSmallScreen -> 4.dp
		isLargeScreen -> 10.dp
		else -> 8.dp
	}

	val gridPadding = when {
		isSmallScreen -> PaddingValues(6.dp)
		isLargeScreen -> PaddingValues(12.dp)
		else -> PaddingValues(8.dp)
	}

	val uiState by vm.uiState.collectAsState()
	var isScientific by remember { mutableStateOf(false) }
	var showConverter by remember { mutableStateOf(false) }

	Column(modifier = Modifier.fillMaxSize()) {
		Surface(
			tonalElevation = 4.dp,
			shadowElevation = 2.dp,
			modifier = Modifier
				.fillMaxWidth()
				.then(displayPadding.let { Modifier.padding(it) })
		) {
			Column(modifier = Modifier.padding(12.dp)) {
				Text(
					text = uiState.displayExpression,
					style = when {
						isSmallScreen -> MaterialTheme.typography.titleMedium
						isLargeScreen -> MaterialTheme.typography.headlineMedium
						else -> MaterialTheme.typography.headlineSmall
					},
					textAlign = TextAlign.End,
					modifier = Modifier.fillMaxWidth()
				)
				Text(
					text = uiState.result,
					style = when {
						isSmallScreen -> MaterialTheme.typography.headlineSmall
						isLargeScreen -> MaterialTheme.typography.displayMedium
						else -> MaterialTheme.typography.headlineMedium
					},
					textAlign = TextAlign.End,
					color = MaterialTheme.colorScheme.primary,
					modifier = Modifier
						.fillMaxWidth()
						.padding(top = if (isSmallScreen) 2.dp else 4.dp)
				)
			}
		}

		Box(modifier = Modifier.weight(1f)) {
			if (showConverter) {
				ConverterScreen(onBack = { showConverter = false })
			} else if (isScientific) {
				ScientificCalculatorLayout(
					onPress = vm::onKey,
					onToggleScientific = { isScientific = false },
					onToggleConverter = { showConverter = true },
					controlPadding = controlPadding,
					dividerPadding = dividerPadding,
					gridSpacing = gridSpacing,
					gridPadding = gridPadding
				)
			} else {
				StandardPad(
					onPress = vm::onKey,
					onToggleScientific = { isScientific = true },
					onToggleConverter = { showConverter = true },
					controlPadding = controlPadding,
					dividerPadding = dividerPadding,
					gridSpacing = gridSpacing,
					gridPadding = gridPadding
				)
			}
		}
	}
}

@Composable
private fun StandardPad(
	onPress: (CalculatorKey) -> Unit,
	onToggleScientific: () -> Unit,
	onToggleConverter: () -> Unit,
	controlPadding: Modifier,
	dividerPadding: Modifier,
	gridSpacing: androidx.compose.ui.unit.Dp,
	gridPadding: PaddingValues
) {
	val items = listOf(
		CalculatorKey.AC to R.string.key_ac,
		CalculatorKey.Parentheses to R.string.key_parentheses,
		CalculatorKey.Percent to R.string.key_percent,
		CalculatorKey.Divide to R.string.key_divide,

		CalculatorKey.Digit7 to null,
		CalculatorKey.Digit8 to null,
		CalculatorKey.Digit9 to null,
		CalculatorKey.Multiply to R.string.key_multiply,

		CalculatorKey.Digit4 to null,
		CalculatorKey.Digit5 to null,
		CalculatorKey.Digit6 to null,
		CalculatorKey.Minus to R.string.key_minus,

		CalculatorKey.Digit1 to null,
		CalculatorKey.Digit2 to null,
		CalculatorKey.Digit3 to null,
		CalculatorKey.Plus to R.string.key_plus,

		CalculatorKey.Sign to R.string.key_sign,
		CalculatorKey.Digit0 to null,
		CalculatorKey.Dot to R.string.key_dot,
		CalculatorKey.Equals to R.string.key_equals,
	)

	Column(modifier = Modifier.fillMaxSize()) {
		// Control icons bar
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.then(controlPadding),
			horizontalArrangement = Arrangement.SpaceBetween,
			verticalAlignment = Alignment.CenterVertically
		) {
			Row(verticalAlignment = Alignment.CenterVertically) {
				IconButton(
					onClick = onToggleScientific,
					modifier = Modifier.padding(8.dp)
				) {
					Icon(
						painter = painterResource(id = R.drawable.ic_scientific),
						contentDescription = stringResource(R.string.tab_scientific),
						tint = MaterialTheme.colorScheme.primary
					)
				}
				CalculatorButton(
					label = "Converter",
					onClick = onToggleConverter,
					tonal = true,
					capsule = true,
					fillMaxWidth = false,
					modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 4.dp, bottom = 4.dp)
				)
			}

			// Backspace icon (right)
			IconButton(
				onClick = { onPress(CalculatorKey.C) },
				modifier = Modifier.padding(8.dp)
			) {
				Icon(
					painter = painterResource(id = android.R.drawable.ic_input_delete),
					contentDescription = stringResource(R.string.key_c),
					tint = MaterialTheme.colorScheme.primary
				)
			}
		}

		// Horizontal divider
		HorizontalDivider(
			modifier = dividerPadding,
			color = MaterialTheme.colorScheme.outlineVariant
		)

		// Calculator buttons grid
		LazyVerticalGrid(
			columns = GridCells.Fixed(4),
			contentPadding = gridPadding,
			horizontalArrangement = Arrangement.spacedBy(gridSpacing),
			verticalArrangement = Arrangement.spacedBy(gridSpacing),
			modifier = Modifier
				.fillMaxWidth()
				.weight(1f)
		) {
			items(items) { (key, labelId) ->
				val label = labelId?.let { stringResource(id = it) } ?: key.display
				val isOp = key.isOperator
				val tonal = when (key) {
					CalculatorKey.AC, CalculatorKey.Parentheses, CalculatorKey.Percent -> false // Use filled style for these
					CalculatorKey.Equals -> false // Same as AC
					else -> !isOp
				}
				CalculatorButton(
					label = label,
					onClick = { onPress(key) },
					tonal = tonal,
					capsule = false // Standard mode uses regular buttons
				)
			}
		}
	}
}

@Composable
private fun ScientificCalculatorLayout(
	onPress: (CalculatorKey) -> Unit,
	onToggleScientific: () -> Unit,
	onToggleConverter: () -> Unit,
	controlPadding: Modifier,
	dividerPadding: Modifier,
	gridSpacing: androidx.compose.ui.unit.Dp,
	gridPadding: PaddingValues
) {
	var scientificPage by remember { mutableStateOf(0) }
	// Reorganized for a uniform 4-column layout
	val scientificKeysPage1 = listOf(
		// Row 1
		CalculatorKey.ScientificToggle to null,
		CalculatorKey.DegRadToggle to null,
		CalculatorKey.Sqrt to R.string.key_sqrt,
		CalculatorKey.Abs to R.string.key_abs,
		// Row 2
		CalculatorKey.Sin to R.string.key_sin,
		CalculatorKey.Cos to R.string.key_cos,
		CalculatorKey.Tan to R.string.key_tan,
		CalculatorKey.Pi to R.string.key_pi,
		// Row 3
		CalculatorKey.Ln to R.string.key_ln,
		CalculatorKey.Log to R.string.key_log,
		CalculatorKey.Reciprocal to R.string.key_reciprocal,
		CalculatorKey.Euler to R.string.key_e,
		// Row 4
		CalculatorKey.Exp to R.string.key_exp,
		CalculatorKey.Square to R.string.key_x_squared,
		CalculatorKey.Pow to R.string.key_pow,
		CalculatorKey.Fact to R.string.key_fact,
		// Row 5
		CalculatorKey.AC to R.string.key_ac,
		CalculatorKey.Parentheses to R.string.key_parentheses,
		CalculatorKey.Percent to R.string.key_percent,
		CalculatorKey.Divide to R.string.key_divide,
		// Row 6
		CalculatorKey.Digit7 to null,
		CalculatorKey.Digit8 to null,
		CalculatorKey.Digit9 to null,
		CalculatorKey.Multiply to R.string.key_multiply,
		// Row 7
		CalculatorKey.Digit4 to null,
		CalculatorKey.Digit5 to null,
		CalculatorKey.Digit6 to null,
		CalculatorKey.Minus to R.string.key_minus,
		// Row 8
		CalculatorKey.Digit1 to null,
		CalculatorKey.Digit2 to null,
		CalculatorKey.Digit3 to null,
		CalculatorKey.Plus to R.string.key_plus,
		// Row 9
		CalculatorKey.Sign to R.string.key_sign,
		CalculatorKey.Digit0 to null,
		CalculatorKey.Dot to R.string.key_dot,
		CalculatorKey.Equals to R.string.key_equals,
	)
	val scientificKeysPage2 = listOf(
		// Row 1
		CalculatorKey.ScientificToggle to null,
		CalculatorKey.DegRadToggle to null,
		CalculatorKey.CubeRoot to R.string.key_cubert,
		CalculatorKey.TwoPowX to R.string.key_2_power_x,
		// Row 2
		CalculatorKey.Asin to R.string.key_asin,
		CalculatorKey.Acos to R.string.key_acos,
		CalculatorKey.Atan to R.string.key_atan,
		CalculatorKey.Cube to R.string.key_x_cubed,
		// Row 3
		CalculatorKey.Sinh to R.string.key_sinh,
		CalculatorKey.Cosh to R.string.key_cosh,
		CalculatorKey.Tanh to R.string.key_tanh,
		null to null, // Empty space
		// Row 4
		CalculatorKey.Asinh to R.string.key_asinh,
		CalculatorKey.Acosh to R.string.key_acosh,
		CalculatorKey.Atanh to R.string.key_atanh,
		null to null, // Empty
		// Row 5
		CalculatorKey.AC to R.string.key_ac,
		CalculatorKey.Parentheses to R.string.key_parentheses,
		CalculatorKey.Percent to R.string.key_percent,
		CalculatorKey.Divide to R.string.key_divide,
		// Row 6
		CalculatorKey.Digit7 to null,
		CalculatorKey.Digit8 to null,
		CalculatorKey.Digit9 to null,
		CalculatorKey.Multiply to R.string.key_multiply,
		// Row 7
		CalculatorKey.Digit4 to null,
		CalculatorKey.Digit5 to null,
		CalculatorKey.Digit6 to null,
		CalculatorKey.Minus to R.string.key_minus,
		// Row 8
		CalculatorKey.Digit1 to null,
		CalculatorKey.Digit2 to null,
		CalculatorKey.Digit3 to null,
		CalculatorKey.Plus to R.string.key_plus,
		// Row 9
		CalculatorKey.Sign to R.string.key_sign,
		CalculatorKey.Digit0 to null,
		CalculatorKey.Dot to R.string.key_dot,
		CalculatorKey.Equals to R.string.key_equals,
	)

	val items = if (scientificPage == 0) scientificKeysPage1 else scientificKeysPage2

	Column(modifier = Modifier.fillMaxSize()) {
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.then(controlPadding),
			horizontalArrangement = Arrangement.SpaceBetween,
			verticalAlignment = Alignment.CenterVertically
		) {
			Row(verticalAlignment = Alignment.CenterVertically) {
				IconButton(
					onClick = onToggleScientific,
					modifier = Modifier.padding(8.dp)
				) {
					Icon(
						painter = painterResource(id = R.drawable.ic_calc),
						contentDescription = "Back to standard calculator",
						tint = MaterialTheme.colorScheme.primary
					)
				}
				CalculatorButton(
					label = "Converter",
					onClick = onToggleConverter,
					tonal = true,
					capsule = true,
					fillMaxWidth = false,
					modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 4.dp, bottom = 4.dp)
				)
			}
			IconButton(
				onClick = { onPress(CalculatorKey.C) },
				modifier = Modifier.padding(8.dp)
			) {
				Icon(
					painter = painterResource(id = android.R.drawable.ic_input_delete),
					contentDescription = stringResource(R.string.key_c),
					tint = MaterialTheme.colorScheme.primary
				)
			}
		}

		HorizontalDivider(
			modifier = dividerPadding,
			color = MaterialTheme.colorScheme.outlineVariant
		)

		LazyVerticalGrid(
			columns = GridCells.Fixed(4),
			contentPadding = gridPadding,
			horizontalArrangement = Arrangement.spacedBy(gridSpacing),
			verticalArrangement = Arrangement.spacedBy(gridSpacing),
			modifier = Modifier
				.fillMaxWidth()
				.weight(1f)
		) {
			// Scientific keys (first 16 items)
			items(items.take(16)) { (key, labelId) ->
				if (key != null) {
					val label = labelId?.let { stringResource(id = it) } ?: key.display
					val tonal = when (key) {
						CalculatorKey.AC, CalculatorKey.Parentheses, CalculatorKey.Percent -> false
						CalculatorKey.Equals -> false
						else -> !key.isOperator
					}
					CalculatorButton(
						label = label,
						onClick = {
							if (key == CalculatorKey.ScientificToggle) {
								scientificPage = 1 - scientificPage
							} else {
								onPress(key)
							}
						},
						tonal = tonal,
						capsule = true
					)
				} else {
					// Empty space, if any
					androidx.compose.foundation.layout.Spacer(modifier = Modifier)
				}
			}

			// Separator line spanning all 4 columns
			item(span = { GridItemSpan(4) }) {
				HorizontalDivider(
					modifier = Modifier.padding(vertical = 8.dp),
					color = MaterialTheme.colorScheme.outlineVariant
				)
			}

			// Standard keys (remaining items)
			items(items.drop(16)) { (key, labelId) ->
				if (key != null) {
					val label = labelId?.let { stringResource(id = it) } ?: key.display
					val tonal = when (key) {
						CalculatorKey.AC, CalculatorKey.Parentheses, CalculatorKey.Percent -> false
						CalculatorKey.Equals -> false
						else -> !key.isOperator
					}
					CalculatorButton(
						label = label,
						onClick = {
							if (key == CalculatorKey.ScientificToggle) {
								scientificPage = 1 - scientificPage
							} else {
								onPress(key)
							}
						},
						tonal = tonal,
						capsule = true
					)
				} else {
					// Empty space, if any
					androidx.compose.foundation.layout.Spacer(modifier = Modifier)
				}
			}
		}
	}
}
