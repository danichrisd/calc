package com.calc.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.calc.app.R

@Composable
fun ConverterScreen(
	onBack: () -> Unit
) {
	Column(modifier = Modifier.fillMaxSize()) {
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(4.dp),
			verticalAlignment = Alignment.CenterVertically
		) {
			IconButton(onClick = onBack) {
				Icon(
					imageVector = Icons.AutoMirrored.Filled.ArrowBack,
					contentDescription = "Back"
				)
			}
		}
		Column(
			modifier = Modifier.fillMaxSize(),
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.Center
		) {
			Text(
				text = stringResource(id = R.string.label_coming_soon),
				style = MaterialTheme.typography.headlineSmall
			)
		}
	}
}


