package com.calc.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.calc.app.viewmodel.BMICalculatorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BMICalculatorScreen(
    onBack: () -> Unit,
    vm: BMICalculatorViewModel = viewModel()
) {
    val uiState by vm.uiState.collectAsState()
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("BMI Calculator") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Unit System Selection
            SegmentedButtonRow(uiState.unitSystem, vm::onUnitSystemChange)

            if (uiState.unitSystem == BMICalculatorViewModel.BMIUnitSystem.METRIC) {
                OutlinedTextField(
                    value = uiState.weight,
                    onValueChange = { vm.onWeightChange(it) },
                    label = { Text("Weight (kg)") },
                    placeholder = { Text("e.g., 70") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = uiState.height,
                    onValueChange = { vm.onHeightChange(it) },
                    label = { Text("Height (cm)") },
                    placeholder = { Text("e.g., 170") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                OutlinedTextField(
                    value = uiState.weight,
                    onValueChange = { vm.onWeightChange(it) },
                    label = { Text("Weight (lbs)") },
                    placeholder = { Text("e.g., 154") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = uiState.height,
                        onValueChange = { vm.onHeightChange(it) },
                        label = { Text("Height (ft)") },
                        placeholder = { Text("e.g., 5") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = uiState.heightInches,
                        onValueChange = { vm.onHeightInchesChange(it) },
                        label = { Text("Inches") },
                        placeholder = { Text("e.g., 7") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            // Results
            if (uiState.bmiValue.isNotEmpty()) {
                BMICard(uiState)
                HealthAdviceCard(uiState.healthTip)
            }

            // Action Buttons
            ActionButtons(vm::reset, vm::saveToHistory, uiState.bmiValue.isNotEmpty())
        }
    }
}

@Composable
private fun SegmentedButtonRow(
    selectedUnitSystem: BMICalculatorViewModel.BMIUnitSystem,
    onUnitSystemChange: (BMICalculatorViewModel.BMIUnitSystem) -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Button(
            onClick = { onUnitSystemChange(BMICalculatorViewModel.BMIUnitSystem.METRIC) },
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (selectedUnitSystem == BMICalculatorViewModel.BMIUnitSystem.METRIC) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Text("Metric")
        }
        Spacer(modifier = Modifier.width(8.dp))
        Button(
            onClick = { onUnitSystemChange(BMICalculatorViewModel.BMIUnitSystem.US_STANDARD) },
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (selectedUnitSystem == BMICalculatorViewModel.BMIUnitSystem.US_STANDARD) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Text("US Standard")
        }
    }
}

@Composable
private fun BMICard(uiState: BMICalculatorViewModel.BMIUiState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(uiState.bmiCategory.color)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Your BMI",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )
            Text(
                text = uiState.bmiValue,
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = uiState.bmiCategory.displayName,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
            Text(
                text = "Range: ${uiState.bmiCategory.range}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.9f)
            )
        }
    }
}

@Composable
private fun HealthAdviceCard(healthTip: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "💡 Health Advice",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = healthTip,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Justify
            )
        }
    }
}

@Composable
private fun ActionButtons(onReset: () -> Unit, onSave: () -> Unit, isSaveEnabled: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedButton(
            onClick = onReset,
            modifier = Modifier.weight(1f)
        ) {
            Text("Reset")
        }

        Button(
            onClick = onSave,
            modifier = Modifier.weight(1f),
            enabled = isSaveEnabled
        ) {
            Text("Save to History")
        }
    }
}
