package com.calc.app.viewmodel

import androidx.lifecycle.ViewModel
import com.calc.app.data.CalculationHistory
import com.calc.app.data.CalculationType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.math.pow

class BMICalculatorViewModel : ViewModel() {

    enum class BMIUnitSystem {
        METRIC, US_STANDARD
    }

    data class BMIUiState(
        val weight: String = "",
        val height: String = "",
        val heightInches: String = "",
        val bmiValue: String = "",
        val bmiCategory: BMICategory = BMICategory.NORMAL,
        val healthTip: String = "",
        val unitSystem: BMIUnitSystem = BMIUnitSystem.METRIC
    )

    enum class BMICategory(
        val displayName: String,
        val color: Long,
        val range: String
    ) {
        UNDERWEIGHT("Underweight", 0xFF64B5F6, "< 18.5"),
        NORMAL("Normal", 0xFF66BB6A, "18.5 - 24.9"),
        OVERWEIGHT("Overweight", 0xFFFFB74D, "25.0 - 29.9"),
        OBESE("Obese", 0xFFEF5350, "≥ 30.0")
    }

    private val _uiState = MutableStateFlow(BMIUiState())
    val uiState = _uiState.asStateFlow()

    fun onWeightChange(value: String) {
        _uiState.update { it.copy(weight = value) }
        calculate()
    }

    fun onHeightChange(value: String) {
        _uiState.update { it.copy(height = value) }
        calculate()
    }

    fun onHeightInchesChange(value: String) {
        _uiState.update { it.copy(heightInches = value) }
        calculate()
    }

    fun onUnitSystemChange(unitSystem: BMIUnitSystem) {
        _uiState.update { it.copy(unitSystem = unitSystem) }
        calculate()
    }

    fun reset() {
        _uiState.value = BMIUiState()
    }

    private fun calculate() {
        val state = _uiState.value

        val weight = state.weight.replace(",", ".").toDoubleOrNull() ?: return
        val height = state.height.replace(",", ".").toDoubleOrNull() ?: return
        val heightInches = state.heightInches.replace(",", ".").toDoubleOrNull() ?: 0.0

        if (weight <= 0 || height <= 0) return

        val bmi = if (state.unitSystem == BMIUnitSystem.METRIC) {
            val heightInMeters = height / 100
            weight / heightInMeters.pow(2)
        } else {
            val totalHeightInInches = height * 12 + heightInches
            (weight / totalHeightInInches.pow(2)) * 703
        }

        val category = when {
            bmi < 18.5 -> BMICategory.UNDERWEIGHT
            bmi < 25.0 -> BMICategory.NORMAL
            bmi < 30.0 -> BMICategory.OVERWEIGHT
            else -> BMICategory.OBESE
        }

        val tip = when (category) {
            BMICategory.UNDERWEIGHT -> "Consider increasing calorie intake and balanced nutrition."
            BMICategory.NORMAL -> "Maintain a healthy lifestyle with balanced diet and regular exercise."
            BMICategory.OVERWEIGHT -> "Reduce calorie intake and increase physical activity."
            BMICategory.OBESE -> "Consult a doctor for a safe weight loss program."
        }

        _uiState.update {
            it.copy(
                bmiValue = String.format("%.1f", bmi),
                bmiCategory = category,
                healthTip = tip
            )
        }
    }

    fun saveToHistory() {
        val state = _uiState.value
        if (state.bmiValue.isNotEmpty()) {
            val expression = if (state.unitSystem == BMIUnitSystem.METRIC) {
                "Weight: ${state.weight} kg\n" +
                "Height: ${state.height} cm"
            } else {
                "Weight: ${state.weight} lbs\n" +
                "Height: ${state.height} ft ${state.heightInches} in"
            }
            val result = "BMI: ${state.bmiValue}\n" +
                    "Category: ${state.bmiCategory.displayName}\n" +
                    "Range: ${state.bmiCategory.range}"

            HistoryViewModel.getInstance().addHistory(
                CalculationHistory(
                    type = CalculationType.BMI,
                    expression = expression,
                    result = result
                )
            )
        }
    }
}
