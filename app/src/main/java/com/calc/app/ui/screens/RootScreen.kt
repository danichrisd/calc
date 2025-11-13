package com.calc.app.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp

@Composable
fun RootScreen() {
    val configuration = LocalConfiguration.current
    val isLargeScreen = configuration.screenWidthDp >= 600

    if (isLargeScreen) {
        var showConverter by remember { mutableStateOf(false) }
        Row(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.weight(1f)) {
                CalculatorScreen()
            }
            Box(modifier = Modifier.weight(1f)) {
                ConverterScreen(onBack = { showConverter = false })
            }
        }
    } else {
        var selectedTab by remember { mutableStateOf(0) }
        Column(modifier = Modifier.fillMaxSize()) {
            when (selectedTab) {
                0 -> CalculatorScreen()
                1 -> ConverterScreen(onBack = { selectedTab = 0 })
            }

            androidx.compose.material3.TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                androidx.compose.material3.Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Calculator") }
                )
                androidx.compose.material3.Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Converter") }
                )
            }
        }
    }
}
