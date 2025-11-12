package com.calc.app.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.calc.app.R

enum class TabPage { Standard, Converter }

@Composable
fun RootScreen() {
	var selectedTab by remember { mutableStateOf(TabPage.Standard) }

	val tabs = listOf(
		TabPage.Standard to stringResource(id = R.string.tab_standard),
		TabPage.Converter to stringResource(id = R.string.tab_converter),
	)

	Column {
		TabRow(
			selectedTabIndex = tabs.indexOfFirst { it.first == selectedTab },
			containerColor = MaterialTheme.colorScheme.surface
		) {
			tabs.forEachIndexed { _, item ->
				Tab(
					selected = selectedTab == item.first,
					onClick = { selectedTab = item.first },
					text = { Text(text = item.second) }
				)
			}
		}

		when (selectedTab) {
			TabPage.Standard -> CalculatorScreen()
			TabPage.Converter -> ConverterScreen()
		}
	}
}


