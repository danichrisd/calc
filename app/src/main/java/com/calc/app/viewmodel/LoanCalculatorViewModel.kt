package com.calc.app.viewmodel

import androidx.lifecycle.ViewModel
import com.calc.app.data.CalculationHistory
import com.calc.app.data.CalculationType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.text.DecimalFormat
import kotlin.math.pow

class LoanCalculatorViewModel : ViewModel() {

    data class LoanUiState(
        val loanAmount: String = "",
        val interestRate: String = "",
        val loanTenure: String = "",
        val tenureType: TenureType = TenureType.YEARS,
        val monthlyPayment: String = "",
        val totalInterest: String = "",
        val totalPayment: String = ""
    )

    enum class TenureType(val displayName: String, val multiplier: Int) {
        YEARS("Years", 12),
        MONTHS("Months", 1)
    }

    private val _uiState = MutableStateFlow(LoanUiState())
    val uiState = _uiState.asStateFlow()

    fun onLoanAmountChange(value: String) {
        _uiState.update { it.copy(loanAmount = value) }
        calculate()
    }

    fun onInterestRateChange(value: String) {
        _uiState.update { it.copy(interestRate = value) }
        calculate()
    }

    fun onLoanTenureChange(value: String) {
        _uiState.update { it.copy(loanTenure = value) }
        calculate()
    }

    fun onTenureTypeChange(type: TenureType) {
        _uiState.update { it.copy(tenureType = type) }
        calculate()
    }

    fun reset() {
        _uiState.value = LoanUiState()
    }

    private fun calculate() {
        val state = _uiState.value

        val principal = state.loanAmount.replace(",", ".").toDoubleOrNull() ?: return
        val annualRate = state.interestRate.replace(",", ".").toDoubleOrNull() ?: return
        val tenure = state.loanTenure.toIntOrNull() ?: return

        if (principal <= 0 || annualRate < 0 || tenure <= 0) return

        val months = tenure * state.tenureType.multiplier
        val monthlyRate = annualRate / 12 / 100

        val emi = if (monthlyRate > 0) {
            val factor = (1 + monthlyRate).pow(months)
            principal * monthlyRate * factor / (factor - 1)
        } else {
            principal / months
        }

        val totalPayment = emi * months
        val totalInterest = totalPayment - principal

        _uiState.update {
            it.copy(
                monthlyPayment = formatCurrency(emi),
                totalInterest = formatCurrency(totalInterest),
                totalPayment = formatCurrency(totalPayment)
            )
        }
    }

    fun saveToHistory() {
        val state = _uiState.value
        if (state.monthlyPayment.isNotEmpty()) {
            val expression = "Loan: ${state.loanAmount}\n" +
                    "Interest: ${state.interestRate}%\n" +
                    "Term: ${state.loanTenure} ${state.tenureType.displayName}"
            val result = "EMI: ${state.monthlyPayment}\n" +
                    "Total Interest: ${state.totalInterest}\n" +
                    "Total Payment: ${state.totalPayment}"

            HistoryViewModel.getInstance().addHistory(
                CalculationHistory(
                    type = CalculationType.LOAN,
                    expression = expression,
                    result = result
                )
            )
        }
    }

    private fun formatCurrency(number: Double): String {
        val formatter = DecimalFormat("#,###.##")
        return formatter.format(number)
    }
}
