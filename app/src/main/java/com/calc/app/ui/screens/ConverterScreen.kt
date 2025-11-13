package com.calc.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.calc.app.ui.components.CalculatorButton
import com.calc.app.viewmodel.ConverterAction
import com.calc.app.viewmodel.ConverterViewModel
import com.calc.app.viewmodel.ConversionCategory
import com.calc.app.viewmodel.ConversionUnit

@Composable
fun ConverterScreen(
    onBack: () -> Unit,
    vm: ConverterViewModel = viewModel()
) {
    val uiState by vm.uiState.collectAsState()
    var showHistory by remember { mutableStateOf(false) }
    var showLoan by remember { mutableStateOf(false) }
    var showEMI by remember { mutableStateOf(false) }
    var showVAT by remember { mutableStateOf(false) }
    var showBMI by remember { mutableStateOf(false) }

    if (showHistory) {
        HistoryScreen(onBack = { showHistory = false })
        return
    }

    if (showLoan) {
        LoanCalculatorScreen(onBack = { showLoan = false })
        return
    }

    if (showEMI) {
        EMICalculatorScreen(onBack = { showEMI = false })
        return
    }

    if (showVAT) {
        VATCalculatorScreen(onBack = { showVAT = false })
        return
    }

    if (showBMI) {
        BMICalculatorScreen(onBack = { showBMI = false })
        return
    }

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
            CategoryDropdown(
                selectedCategory = uiState.category,
                onCategorySelected = { vm.onAction(ConverterAction.CategoryChange(it)) }
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CalculatorButton(
                label = "History",
                onClick = { showHistory = true },
                tonal = true,
                capsule = true,
                fillMaxWidth = false,
                modifier = Modifier.padding(4.dp)
            )
            CalculatorButton(
                label = "Loan",
                onClick = { showLoan = true },
                tonal = true,
                capsule = true,
                fillMaxWidth = false,
                modifier = Modifier.padding(4.dp)
            )
            CalculatorButton(
                label = "EMI",
                onClick = { showEMI = true },
                tonal = true,
                capsule = true,
                fillMaxWidth = false,
                modifier = Modifier.padding(4.dp)
            )
            CalculatorButton(
                label = "VAT",
                onClick = { showVAT = true },
                tonal = true,
                capsule = true,
                fillMaxWidth = false,
                modifier = Modifier.padding(4.dp)
            )
            CalculatorButton(
                label = "BMI",
                onClick = { showBMI = true },
                tonal = true,
                capsule = true,
                fillMaxWidth = false,
                modifier = Modifier.padding(4.dp)
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "From",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth()
            )
            UnitRow(
                value = uiState.fromValue,
                onValueChange = { vm.onAction(ConverterAction.FromValueChange(it)) },
                selectedUnit = uiState.fromUnit,
                units = uiState.category.units,
                onUnitSelected = { vm.onAction(ConverterAction.FromUnitChange(it)) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            IconButton(
                onClick = { vm.onAction(ConverterAction.SwapUnits) },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Icon(
                    painter = painterResource(id = android.R.drawable.ic_menu_sort_by_size),
                    contentDescription = "Swap units",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "To",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth()
            )
            UnitRow(
                value = uiState.toValue,
                onValueChange = { /* To value is read-only */ },
                selectedUnit = uiState.toUnit,
                units = uiState.category.units,
                onUnitSelected = { vm.onAction(ConverterAction.ToUnitChange(it)) },
                valueReadOnly = true,
                unitReadOnly = false
            )

            Spacer(modifier = Modifier.weight(1f))

            // Save to History Button
            Button(
                onClick = { vm.saveToHistory() },
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState.toValue.isNotEmpty() && uiState.toValue != "Error"
            ) {
                Text("Save to History")
            }
        }
    }
}

@Composable
fun CategoryDropdown(
    selectedCategory: ConversionCategory,
    onCategorySelected: (ConversionCategory) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val categories = listOf(
        ConversionCategory.Area,
        ConversionCategory.Length,
        ConversionCategory.Temperature,
        ConversionCategory.Volume,
        ConversionCategory.Mass,
        ConversionCategory.Data,
        ConversionCategory.Speed,
        ConversionCategory.Time,
        ConversionCategory.Discount
    )

    Box {
        Button(onClick = { expanded = true }) {
            Text(selectedCategory.name)
            Icon(Icons.Default.ArrowDropDown, contentDescription = "Select category")
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            categories.forEach { category ->
                DropdownMenuItem(
                    text = { Text(category.name) },
                    onClick = {
                        onCategorySelected(category)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun UnitRow(
    value: String,
    onValueChange: (String) -> Unit,
    selectedUnit: ConversionUnit,
    units: List<ConversionUnit>,
    onUnitSelected: (ConversionUnit) -> Unit,
    valueReadOnly: Boolean = false,
    unitReadOnly: Boolean = false
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.weight(1f),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            readOnly = valueReadOnly
        )
        UnitDropdown(
            selectedUnit = selectedUnit,
            units = units,
            onUnitSelected = onUnitSelected,
            isReadOnly = unitReadOnly
        )
    }
}

@Composable
fun UnitDropdown(
    selectedUnit: ConversionUnit,
    units: List<ConversionUnit>,
    onUnitSelected: (ConversionUnit) -> Unit,
    isReadOnly: Boolean = false
) {
    if (isReadOnly) {
        Text(
            text = selectedUnit.displayName,
            style = MaterialTheme.typography.bodyLarge
        )
    } else {
        var expanded by remember { mutableStateOf(false) }
        Box {
            Button(onClick = { expanded = true }) {
                Text(selectedUnit.displayName)
                Icon(Icons.Default.ArrowDropDown, contentDescription = "Select unit")
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                units.forEach { unit ->
                    DropdownMenuItem(
                        text = { Text(unit.displayName) },
                        onClick = {
                            onUnitSelected(unit)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
