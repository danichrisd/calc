package com.calc.app.data

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class CalculationHistory(
    val id: String = System.currentTimeMillis().toString(),
    val type: CalculationType,
    val expression: String,
    val result: String,
    val timestamp: Long = System.currentTimeMillis()
) {
    fun getFormattedDate(): String {
        val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}

enum class CalculationType(val displayName: String) {
    BASIC("Basic Calculator"),
    SCIENTIFIC("Scientific Calculator"),
    CONVERTER("Unit Converter"),
    LOAN("Loan Calculator"),
    EMI("EMI Calculator"),
    VAT("VAT Calculator"),
    BMI("BMI Calculator")
}

