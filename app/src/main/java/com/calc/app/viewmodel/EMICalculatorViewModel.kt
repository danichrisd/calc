package com.calc.app.viewmodel

import androidx.lifecycle.ViewModel
import com.calc.app.data.CalculationHistory
import com.calc.app.data.CalculationType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.math.pow

class EMICalculatorViewModel : ViewModel() {
    
    data class EMIUiState(
        val principalAmount: String = "",
        val interestRate: String = "",
        val loanTenure: String = "",
        val emiAmount: String = "",
        val totalInterest: String = "",
        val totalAmount: String = "",
        val principalPercentage: Float = 0f,
        val interestPercentage: Float = 0f
    )
    
    private val _uiState = MutableStateFlow(EMIUiState())
    val uiState = _uiState.asStateFlow()
    
    fun onPrincipalChange(value: String) {
        _uiState.update { it.copy(principalAmount = value) }
        calculate()
    }
    
    fun onInterestRateChange(value: String) {
        _uiState.update { it.copy(interestRate = value) }
        calculate()
    }
    
    fun onTenureChange(value: String) {
        _uiState.update { it.copy(loanTenure = value) }
        calculate()
    }
    
    fun reset() {
        _uiState.value = EMIUiState()
    }
    
    private fun calculate() {
        val state = _uiState.value
        
        val principal = state.principalAmount.replace(".", "").replace(",", ".").toDoubleOrNull() ?: return
        val annualRate = state.interestRate.replace(",", ".").toDoubleOrNull() ?: return
        val months = state.loanTenure.toIntOrNull() ?: return
        
        if (principal <= 0 || annualRate < 0 || months <= 0) return
        
        val monthlyRate = annualRate / 12 / 100
        
        // EMI = [P x R x (1+R)^N] / [(1+R)^N-1]
        val emi = if (monthlyRate > 0) {
            val factor = (1 + monthlyRate).pow(months)
            (principal * monthlyRate * factor) / (factor - 1)
        } else {
            principal / months
        }
        
        val totalPayment = emi * months
        val totalInterest = totalPayment - principal
        
        val principalPercent = (principal / totalPayment * 100).toFloat()
        val interestPercent = (totalInterest / totalPayment * 100).toFloat()
        
        _uiState.update {
            it.copy(
                emiAmount = formatCurrency(emi),
                totalInterest = formatCurrency(totalInterest),
                totalAmount = formatCurrency(totalPayment),
                principalPercentage = principalPercent,
                interestPercentage = interestPercent
            )
        }
    }
    
    fun saveToHistory() {
        val state = _uiState.value
        if (state.emiAmount.isNotEmpty()) {
            val expression = "Principal: ${state.principalAmount}\n" +
                    "Interest: ${state.interestRate}%\n" +
                    "Term: ${state.loanTenure} months"
            val result = "EMI: ${state.emiAmount}\n" +
                    "Total Interest: ${state.totalInterest}\n" +
                    "Total: ${state.totalAmount}"
            
            HistoryViewModel.getInstance().addHistory(
                CalculationHistory(
                    type = CalculationType.EMI,
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

