package com.calc.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.calc.app.viewmodel.EMICalculatorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EMICalculatorScreen(
    onBack: () -> Unit,
    vm: EMICalculatorViewModel = viewModel()
) {
    val uiState by vm.uiState.collectAsState()
    val scrollState = rememberScrollState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("EMI Calculator") },
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
            // Principal Amount Input
            OutlinedTextField(
                value = uiState.principalAmount,
                onValueChange = { vm.onPrincipalChange(it) },
                label = { Text("Jumlah Principal") },
                placeholder = { Text("Contoh: 50000000") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            
            // Interest Rate Input
            OutlinedTextField(
                value = uiState.interestRate,
                onValueChange = { vm.onInterestRateChange(it) },
                label = { Text("Bunga per Tahun (%)") },
                placeholder = { Text("Contoh: 10.5") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )
            
            // Loan Tenure in Months
            OutlinedTextField(
                value = uiState.loanTenure,
                onValueChange = { vm.onTenureChange(it) },
                label = { Text("Jangka Waktu (Bulan)") },
                placeholder = { Text("Contoh: 60") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            
            // Results
            if (uiState.emiAmount.isNotEmpty()) {
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
                            text = "Hasil Perhitungan EMI",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        
                        ResultRow("EMI per Bulan", uiState.emiAmount)
                        ResultRow("Total Bunga", uiState.totalInterest)
                        ResultRow("Total Pembayaran", uiState.totalAmount)
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // Percentage Breakdown
                        Text(
                            text = "Komposisi Pembayaran",
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
                                    text = "Bunga ${String.format("%.1f", uiState.interestPercentage)}%",
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
                    enabled = uiState.emiAmount.isNotEmpty()
                ) {
                    Text("Simpan ke History")
                }
            }
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

