package com.calc.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.calc.app.viewmodel.LoanCalculatorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoanCalculatorScreen(
    onBack: () -> Unit,
    vm: LoanCalculatorViewModel = viewModel()
) {
    val uiState by vm.uiState.collectAsState()
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Loan Calculator") },
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
            // Interest Type Selection
            SegmentedButtonRow(uiState.interestType, vm::onInterestTypeChange)

            // Loan Amount Input
            OutlinedTextField(
                value = uiState.loanAmount,
                onValueChange = { vm.onLoanAmountChange(it) },
                label = { Text("Loan Amount") },
                placeholder = { Text("e.g., 100000000") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            // Interest Rate Input
            OutlinedTextField(
                value = uiState.interestRate,
                onValueChange = { vm.onInterestRateChange(it) },
                label = { Text("Annual Interest Rate (%)") },
                placeholder = { Text("e.g., 8.5") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )

            // Loan Tenure Input
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = uiState.loanTenure,
                    onValueChange = { vm.onLoanTenureChange(it) },
                    label = { Text("Term") },
                    placeholder = { Text("e.g., 15") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )

                // Tenure Type Dropdown
                var expanded by remember { mutableStateOf(false) }
                Box(modifier = Modifier.weight(1f)) {
                    OutlinedButton(
                        onClick = { expanded = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(uiState.tenureType.displayName)
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        LoanCalculatorViewModel.TenureType.values().forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type.displayName) },
                                onClick = {
                                    vm.onTenureTypeChange(type)
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            // Results
            if (uiState.monthlyPayment.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Calculation Results",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        ResultRow("Monthly Payment", uiState.monthlyPayment)
                        ResultRow("Total Interest", uiState.totalInterest)
                        ResultRow("Total Payment", uiState.totalPayment)

                        Spacer(modifier = Modifier.height(8.dp))

                        // Percentage Breakdown
                        Text(
                            text = "Payment Breakdown",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(40.dp)
                        ) {
                            // Principal Bar
                            Box(
                                modifier = Modifier
                                    .weight(uiState.principalPercentage)
                                    .fillMaxHeight()
                            ) {
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color(0xFF66BB6A)
                                    ),
                                    modifier = Modifier.fillMaxSize()
                                ) {}
                            }

                            // Interest Bar
                            Box(
                                modifier = Modifier
                                    .weight(uiState.interestPercentage)
                                    .fillMaxHeight()
                            ) {
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color(0xFFEF5350)
                                    ),
                                    modifier = Modifier.fillMaxSize()
                                ) {}
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row {
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                ) {
                                    Card(
                                        colors = CardDefaults.cardColors(
                                            containerColor = Color(0xFF66BB6A)
                                        ),
                                        modifier = Modifier.fillMaxSize()
                                    ) {}
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Principal ${String.format("%.1f", uiState.principalPercentage)}%",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }

                            Row {
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                ) {
                                    Card(
                                        colors = CardDefaults.cardColors(
                                            containerColor = Color(0xFFEF5350)
                                        ),
                                        modifier = Modifier.fillMaxSize()
                                    ) {}
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Interest ${String.format("%.1f", uiState.interestPercentage)}%",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { vm.reset() },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Reset")
                }

                Button(
                    onClick = { vm.saveToHistory() },
                    modifier = Modifier.weight(1f),
                    enabled = uiState.monthlyPayment.isNotEmpty()
                ) {
                    Text("Save to History")
                }
            }
        }
    }
}

@Composable
private fun SegmentedButtonRow(
    selectedInterestType: LoanCalculatorViewModel.InterestType,
    onInterestTypeChange: (LoanCalculatorViewModel.InterestType) -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        LoanCalculatorViewModel.InterestType.values().forEach { type ->
            Button(
                onClick = { onInterestTypeChange(type) },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedInterestType == type) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Text(type.displayName)
            }
            Spacer(modifier = Modifier.width(8.dp))
        }
    }
}

@Composable
private fun ResultRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}
