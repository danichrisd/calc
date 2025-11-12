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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.calc.app.R
import com.calc.app.ui.components.CalculatorButton
import com.calc.app.viewmodel.CalculatorViewModel
import com.calc.app.viewmodel.CalculatorViewModel.CalculatorKey

@Composable
fun CalculatorScreen(
	vm: CalculatorViewModel = viewModel()
) {
	val uiState by vm.uiState.collectAsState()
	var isScientific by remember { mutableStateOf(false) }

	Column(modifier = Modifier.fillMaxSize()) {
		Surface(
			tonalElevation = 4.dp,
			shadowElevation = 2.dp,
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 16.dp, vertical = 12.dp)
		) {
			Column(modifier = Modifier.padding(16.dp)) {
				Text(
					text = uiState.expression,
					style = MaterialTheme.typography.headlineSmall,
					textAlign = TextAlign.End,
					modifier = Modifier.fillMaxWidth()
				)
				Text(
					text = uiState.result,
					style = MaterialTheme.typography.headlineMedium,
					textAlign = TextAlign.End,
					color = MaterialTheme.colorScheme.primary,
					modifier = Modifier
						.fillMaxWidth()
						.padding(top = 8.dp)
				)
			}
		}

		if (isScientific) {
			ScientificCalculatorLayout(
				onPress = vm::onKey,
				onToggleScientific = { isScientific = false }
			)
		} else {
			StandardPad(
				onPress = vm::onKey,
				onToggleScientific = { isScientific = true }
			)
		}
	}
}

@Composable
private fun StandardPad(onPress: (CalculatorKey) -> Unit, onToggleScientific: () -> Unit) {
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
				.padding(horizontal = 16.dp, vertical = 8.dp),
			horizontalArrangement = Arrangement.SpaceBetween,
			verticalAlignment = Alignment.CenterVertically
		) {
			// Scientific toggle icon (left)
			IconButton(
				onClick = onToggleScientific,
				modifier = Modifier.padding(8.dp)
			) {
				Icon(
					painter = painterResource(id = android.R.drawable.ic_menu_more),
					contentDescription = stringResource(R.string.tab_scientific),
					tint = MaterialTheme.colorScheme.primary
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
			modifier = Modifier.padding(horizontal = 16.dp),
			color = MaterialTheme.colorScheme.outlineVariant
		)

		// Calculator buttons grid
		LazyVerticalGrid(
			columns = GridCells.Fixed(4),
			contentPadding = PaddingValues(12.dp),
			horizontalArrangement = Arrangement.spacedBy(8.dp),
			verticalArrangement = Arrangement.spacedBy(8.dp),
			modifier = Modifier.fillMaxSize()
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
private fun ScientificCalculatorLayout(onPress: (CalculatorKey) -> Unit, onToggleScientific: () -> Unit) {
	// Scientific operators in 4x4 grid
	val scientificOperators = listOf(
		CalculatorKey.DegRadToggle to null,
		CalculatorKey.Fact to R.string.key_fact,
		CalculatorKey.Sin to R.string.key_sin,
		CalculatorKey.Sqrt to R.string.key_sqrt,

		CalculatorKey.Asin to R.string.key_asin,
		CalculatorKey.Acos to R.string.key_acos,
		CalculatorKey.Atan to R.string.key_atan,
		CalculatorKey.Pow to R.string.key_pow,

		CalculatorKey.Ln to R.string.key_ln,
		CalculatorKey.Log to R.string.key_log,
		CalculatorKey.Exp to R.string.key_exp,
		CalculatorKey.Pi to R.string.key_pi,

		CalculatorKey.Tan to R.string.key_tan,
		CalculatorKey.Cos to R.string.key_cos,
		null to null, // Empty space
		null to null  // Empty space
	)

	Column(modifier = Modifier.fillMaxSize()) {
		// Control icons bar
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 16.dp, vertical = 8.dp),
			horizontalArrangement = Arrangement.SpaceBetween,
			verticalAlignment = Alignment.CenterVertically
		) {
			// Back to standard calculator icon (left)
			IconButton(
				onClick = onToggleScientific,
				modifier = Modifier.padding(8.dp)
			) {
				Icon(
					painter = painterResource(id = android.R.drawable.ic_menu_revert),
					contentDescription = "Back to standard calculator",
					tint = MaterialTheme.colorScheme.primary
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

		// Scientific operators in 4x4 grid
		LazyVerticalGrid(
			columns = GridCells.Fixed(4),
			contentPadding = PaddingValues(12.dp),
			horizontalArrangement = Arrangement.spacedBy(4.dp),
			verticalArrangement = Arrangement.spacedBy(4.dp),
			modifier = Modifier
				.fillMaxWidth()
				.height(200.dp) // Fixed height for 4 rows
		) {
			items(scientificOperators) { (key, labelId) ->
				if (key != null) {
					val label = labelId?.let { stringResource(id = it) } ?: key.display
					CalculatorButton(
						label = label,
						onClick = { onPress(key) },
						tonal = true,
						capsule = true,
						modifier = Modifier.aspectRatio(1.2f) // Slightly wider for smaller buttons
					)
				} else {
					// Empty space
					androidx.compose.foundation.layout.Spacer(
						modifier = Modifier.aspectRatio(1.2f)
					)
				}
			}
		}

		// Horizontal divider
		HorizontalDivider(
			modifier = Modifier.padding(horizontal = 16.dp),
			color = MaterialTheme.colorScheme.outlineVariant
		)

		// Main calculator buttons with capsule shape
		val stdItems = listOf(
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

		LazyVerticalGrid(
			columns = GridCells.Fixed(4),
			contentPadding = PaddingValues(12.dp),
			horizontalArrangement = Arrangement.spacedBy(4.dp),
			verticalArrangement = Arrangement.spacedBy(4.dp),
			modifier = Modifier.fillMaxSize()
		) {
			items(stdItems) { (key, labelId) ->
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
					capsule = true // All buttons are capsule shaped in scientific mode
				)
			}
		}
	}
}


