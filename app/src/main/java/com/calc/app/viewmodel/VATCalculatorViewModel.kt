package com.calc.app.viewmodel

import androidx.lifecycle.ViewModel
import com.calc.app.data.CalculationHistory
import com.calc.app.data.CalculationType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.text.DecimalFormat

class VATCalculatorViewModel : ViewModel() {

    data class VATUiState(
        val amount: String = "",
        val vatRate: String = "11", // Default VAT
        val calculationType: VATCalculationType = VATCalculationType.EXCLUSIVE,
        val vatAmount: String = "",
        val totalAmount: String = "",
        val netAmount: String = ""
    )

    enum class VATCalculationType(val displayName: String) {
        EXCLUSIVE("Add VAT (Price does not include VAT)"),
        INCLUSIVE("Extract VAT (Price includes VAT)")
    }

    private val _uiState = MutableStateFlow(VATUiState())
    val uiState = _uiState.asStateFlow()

    fun onAmountChange(value: String) {
        _uiState.update { it.copy(amount = value) }
        calculate()
    }

    fun onVatRateChange(value: String) {
        _uiState.update { it.copy(vatRate = value) }
        calculate()
    }

    fun onCalculationTypeChange(type: VATCalculationType) {
        _uiState.update { it.copy(calculationType = type) }
        calculate()
    }

    fun reset() {
        _uiState.value = VATUiState()
    }

    private fun calculate() {
        val state = _uiState.value

        val amount = state.amount.replace(",", ".").toDoubleOrNull() ?: return
        val vatRate = state.vatRate.replace(",", ".").toDoubleOrNull() ?: return

        if (amount <= 0 || vatRate < 0) return

        when (state.calculationType) {
            VATCalculationType.EXCLUSIVE -> {
                val vatAmount = amount * vatRate / 100
                val totalAmount = amount + vatAmount

                _uiState.update {
                    it.copy(
                        netAmount = formatCurrency(amount),
                        vatAmount = formatCurrency(vatAmount),
                        totalAmount = formatCurrency(totalAmount)
                    )
                }
            }
            VATCalculationType.INCLUSIVE -> {
                val netAmount = amount / (1 + vatRate / 100)
                val vatAmount = amount - netAmount

                _uiState.update {
                    it.copy(
                        netAmount = formatCurrency(netAmount),
                        vatAmount = formatCurrency(vatAmount),
                        totalAmount = formatCurrency(amount)
                    )
                }
            }
        }
    }

    fun saveToHistory() {
        val state = _uiState.value
        if (state.vatAmount.isNotEmpty()) {
            val expression = "Amount: ${state.amount}\n" +
                    "VAT Rate: ${state.vatRate}%\n" +
                    "Type: ${state.calculationType.displayName}"
            val result = "Net Amount: ${state.netAmount}\n" +
                    "VAT: ${state.vatAmount}\n" +
                    "Total: ${state.totalAmount}"

            HistoryViewModel.getInstance().addHistory(
                CalculationHistory(
                    type = CalculationType.VAT,
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
