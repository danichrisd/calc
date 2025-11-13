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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.calc.app.viewmodel.GSTCalculatorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GSTCalculatorScreen(
    onBack: () -> Unit,
    vm: GSTCalculatorViewModel = viewModel()
) {
    val uiState by vm.uiState.collectAsState()
    val scrollState = rememberScrollState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kalkulator PPN") },
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
            // Calculation Type Selection
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(12.dp)
                ) {
                    Text(
                        text = "Tipe Perhitungan",
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    GSTCalculatorViewModel.GSTCalculationType.values().forEach { type ->
                        Row(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            RadioButton(
                                selected = uiState.calculationType == type,
                                onClick = { vm.onCalculationTypeChange(type) }
                            )
                            Text(
                                text = type.displayName,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier
                                    .padding(start = 8.dp)
                                    .align(androidx.compose.ui.Alignment.CenterVertically)
                            )
                        }
                    }
                }
            }
            
            // Amount Input
            OutlinedTextField(
                value = uiState.amount,
                onValueChange = { vm.onAmountChange(it) },
                label = { Text("Jumlah") },
                placeholder = { Text("Contoh: 1000000") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            
            // GST Rate Input
            OutlinedTextField(
                value = uiState.gstRate,
                onValueChange = { vm.onGSTRateChange(it) },
                label = { Text("Tarif PPN (%)") },
                placeholder = { Text("Default: 11") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )
            
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            
            // Results
            if (uiState.gstAmount.isNotEmpty()) {
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
                            text = "Hasil Perhitungan",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        
                        ResultRow("Harga Dasar", uiState.netAmount)
                        ResultRow("PPN (${uiState.gstRate}%)", uiState.gstAmount)
                        Divider()
                        ResultRow("Total", uiState.totalAmount, isTotal = true)
                    }
                }
                
                // Info Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Text(
                            text = "ℹ️ Informasi",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = when (uiState.calculationType) {
                                GSTCalculatorViewModel.GSTCalculationType.EXCLUSIVE ->
                                    "PPN ditambahkan ke harga dasar untuk mendapatkan harga final."
                                GSTCalculatorViewModel.GSTCalculationType.INCLUSIVE ->
                                    "PPN diekstrak dari harga final untuk mendapatkan harga dasar."
                            },
                            style = MaterialTheme.typography.bodySmall
                        )
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
                    enabled = uiState.gstAmount.isNotEmpty()
                ) {
                    Text("Simpan ke History")
                }
            }
        }
    }
}

@Composable
private fun ResultRow(label: String, value: String, isTotal: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = if (isTotal) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium,
            fontWeight = if (isTotal) FontWeight.Bold else FontWeight.Normal
        )
        Text(
            text = value,
            style = if (isTotal) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

