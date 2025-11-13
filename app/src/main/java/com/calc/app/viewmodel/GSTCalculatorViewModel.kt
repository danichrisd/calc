package com.calc.app.viewmodel

import androidx.lifecycle.ViewModel
import com.calc.app.data.CalculationHistory
import com.calc.app.data.CalculationType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class GSTCalculatorViewModel : ViewModel() {
    
    data class GSTUiState(
        val amount: String = "",
        val gstRate: String = "11", // PPN Indonesia default 11%
        val calculationType: GSTCalculationType = GSTCalculationType.EXCLUSIVE,
        val gstAmount: String = "",
        val totalAmount: String = "",
        val netAmount: String = ""
    )
    
    enum class GSTCalculationType(val displayName: String) {
        EXCLUSIVE("Tambah PPN (Harga belum termasuk PPN)"),
        INCLUSIVE("Pisahkan PPN (Harga sudah termasuk PPN)")
    }
    
    private val _uiState = MutableStateFlow(GSTUiState())
    val uiState = _uiState.asStateFlow()
    
    fun onAmountChange(value: String) {
        _uiState.update { it.copy(amount = value) }
        calculate()
    }
    
    fun onGSTRateChange(value: String) {
        _uiState.update { it.copy(gstRate = value) }
        calculate()
    }
    
    fun onCalculationTypeChange(type: GSTCalculationType) {
        _uiState.update { it.copy(calculationType = type) }
        calculate()
    }
    
    fun reset() {
        _uiState.value = GSTUiState()
    }
    
    private fun calculate() {
        val state = _uiState.value
        
        val amount = state.amount.replace(".", "").replace(",", ".").toDoubleOrNull() ?: return
        val gstRate = state.gstRate.replace(",", ".").toDoubleOrNull() ?: return
        
        if (amount <= 0 || gstRate < 0) return
        
        when (state.calculationType) {
            GSTCalculationType.EXCLUSIVE -> {
                // Add GST to amount
                val gstAmount = amount * gstRate / 100
                val totalAmount = amount + gstAmount
                
                _uiState.update {
                    it.copy(
                        netAmount = formatCurrency(amount),
                        gstAmount = formatCurrency(gstAmount),
                        totalAmount = formatCurrency(totalAmount)
                    )
                }
            }
            GSTCalculationType.INCLUSIVE -> {
                // Extract GST from amount
                val netAmount = amount / (1 + gstRate / 100)
                val gstAmount = amount - netAmount
                
                _uiState.update {
                    it.copy(
                        netAmount = formatCurrency(netAmount),
                        gstAmount = formatCurrency(gstAmount),
                        totalAmount = formatCurrency(amount)
                    )
                }
            }
        }
    }
    
    fun saveToHistory() {
        val state = _uiState.value
        if (state.gstAmount.isNotEmpty()) {
            val expression = "Jumlah: ${state.amount}\n" +
                    "Tarif PPN: ${state.gstRate}%\n" +
                    "Tipe: ${state.calculationType.displayName}"
            val result = "Harga Dasar: ${state.netAmount}\n" +
                    "PPN: ${state.gstAmount}\n" +
                    "Total: ${state.totalAmount}"
            
            HistoryViewModel.getInstance().addHistory(
                CalculationHistory(
                    type = CalculationType.GST,
                    expression = expression,
                    result = result
                )
            )
        }
    }
    
    private fun formatCurrency(number: Double): String {
        val formatted = String.format("%.2f", number)
        val parts = formatted.split(".")
        val integerPart = parts[0]
        val decimalPart = if (parts.size > 1) parts[1] else "00"
        
        val formattedInteger = integerPart.reversed().chunked(3).joinToString(".").reversed()
        
        return "Rp $formattedInteger,$decimalPart"
    }
}

