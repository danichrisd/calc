package com.calc.app.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ConverterViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ConverterUiState())
    val uiState = _uiState.asStateFlow()

    fun onAction(action: ConverterAction) {
        when (action) {
            is ConverterAction.CategoryChange -> {
                _uiState.update {
                    it.copy(
                        category = action.category,
                        fromUnit = action.category.units.first(),
                        toUnit = action.category.units.first(),
                        fromValue = "",
                        toValue = ""
                    )
                }
            }
            is ConverterAction.FromUnitChange -> {
                _uiState.update { it.copy(fromUnit = action.unit) }
                convert()
            }
            is ConverterAction.ToUnitChange -> {
                _uiState.update { it.copy(toUnit = action.unit) }
                convert()
            }
            is ConverterAction.FromValueChange -> {
                _uiState.update { it.copy(fromValue = action.value) }
                convert()
            }
            ConverterAction.SwapUnits -> {
                _uiState.update {
                    it.copy(
                        fromUnit = it.toUnit,
                        toUnit = it.fromUnit,
                        fromValue = it.toValue,
                        toValue = it.fromValue
                    )
                }
            }
        }
    }

    private fun convert() {
        _uiState.update {
            val fromValueDouble = it.fromValue.toDoubleOrNull() ?: return@update it.copy(toValue = "")
            val result = it.category.convert(fromValueDouble, it.fromUnit, it.toUnit)
            it.copy(toValue = result.toString())
        }
    }
}

data class ConverterUiState(
    val category: ConversionCategory = ConversionCategory.Area,
    val fromUnit: ConversionUnit = ConversionCategory.Area.units.first(),
    val toUnit: ConversionUnit = ConversionCategory.Area.units.first(),
    val fromValue: String = "",
    val toValue: String = ""
)

sealed class ConverterAction {
    data class CategoryChange(val category: ConversionCategory) : ConverterAction()
    data class FromUnitChange(val unit: ConversionUnit) : ConverterAction()
    data class ToUnitChange(val unit: ConversionUnit) : ConverterAction()
    data class FromValueChange(val value: String) : ConverterAction()
    object SwapUnits : ConverterAction()
}

interface ConversionUnit {
    val displayName: String
    val symbol: String
}

sealed class ConversionCategory(val name: String, val units: List<ConversionUnit>) {
    abstract fun convert(value: Double, from: ConversionUnit, to: ConversionUnit): Double

    object Area : ConversionCategory("Area", AreaUnit.values().toList()) {
        override fun convert(value: Double, from: ConversionUnit, to: ConversionUnit): Double {
            val fromUnit = from as AreaUnit
            val toUnit = to as AreaUnit

            // Convert to square meters first, then to target unit
            val squareMeters = value * fromUnit.toSquareMeters
            return squareMeters / toUnit.toSquareMeters
        }
    }

    object Length : ConversionCategory("Length", LengthUnit.values().toList()) {
        override fun convert(value: Double, from: ConversionUnit, to: ConversionUnit): Double {
            val fromUnit = from as LengthUnit
            val toUnit = to as LengthUnit

            // Convert to meters first, then to target unit
            val meters = value * fromUnit.toMeters
            return meters / toUnit.toMeters
        }
    }

    object Temperature : ConversionCategory("Temperature", TemperatureUnit.values().toList()) {
        override fun convert(value: Double, from: ConversionUnit, to: ConversionUnit): Double {
            val fromUnit = from as TemperatureUnit
            val toUnit = to as TemperatureUnit

            // Convert to Celsius first, then to target unit
            val celsius = when (fromUnit) {
                TemperatureUnit.Celsius -> value
                TemperatureUnit.Fahrenheit -> (value - 32) * 5/9
                TemperatureUnit.Kelvin -> value - 273.15
            }

            return when (toUnit) {
                TemperatureUnit.Celsius -> celsius
                TemperatureUnit.Fahrenheit -> celsius * 9/5 + 32
                TemperatureUnit.Kelvin -> celsius + 273.15
            }
        }
    }

    object Volume : ConversionCategory("Volume", VolumeUnit.values().toList()) {
        override fun convert(value: Double, from: ConversionUnit, to: ConversionUnit): Double {
            val fromUnit = from as VolumeUnit
            val toUnit = to as VolumeUnit

            // Convert to liters first, then to target unit
            val liters = value * fromUnit.toLiters
            return liters / toUnit.toLiters
        }
    }

    object Mass : ConversionCategory("Mass", MassUnit.values().toList()) {
        override fun convert(value: Double, from: ConversionUnit, to: ConversionUnit): Double {
            val fromUnit = from as MassUnit
            val toUnit = to as MassUnit

            // Convert to grams first, then to target unit
            val grams = value * fromUnit.toGrams
            return grams / toUnit.toGrams
        }
    }

    object Data : ConversionCategory("Data", DataUnit.values().toList()) {
        override fun convert(value: Double, from: ConversionUnit, to: ConversionUnit): Double {
            val fromUnit = from as DataUnit
            val toUnit = to as DataUnit

            // Convert to bytes first, then to target unit
            val bytes = value * fromUnit.toBytes
            return bytes / toUnit.toBytes
        }
    }

    object Speed : ConversionCategory("Speed", SpeedUnit.values().toList()) {
        override fun convert(value: Double, from: ConversionUnit, to: ConversionUnit): Double {
            val fromUnit = from as SpeedUnit
            val toUnit = to as SpeedUnit

            // Convert to m/s first, then to target unit
            val metersPerSecond = value * fromUnit.toMetersPerSecond
            return metersPerSecond / toUnit.toMetersPerSecond
        }
    }

    object Time : ConversionCategory("Time", TimeUnit.values().toList()) {
        override fun convert(value: Double, from: ConversionUnit, to: ConversionUnit): Double {
            val fromUnit = from as TimeUnit
            val toUnit = to as TimeUnit

            // Convert to seconds first, then to target unit
            val seconds = value * fromUnit.toSeconds
            return seconds / toUnit.toSeconds
        }
    }

    object Tip : ConversionCategory("Tip", TipUnit.values().toList()) {
        override fun convert(value: Double, from: ConversionUnit, to: ConversionUnit): Double {
            // Tip calculation is a bit different - it's percentage based
            val percentage = when (from) {
                is TipUnit -> from.percentage
                else -> 0.0
            }
            return value * percentage / 100.0
        }
    }
}

enum class AreaUnit(override val displayName: String, override val symbol: String, val toSquareMeters: Double) : ConversionUnit {
    SquareMillimeter("Square Millimeter", "mm²", 0.000001),
    SquareCentimeter("Square Centimeter", "cm²", 0.0001),
    SquareMeter("Square Meter", "m²", 1.0),
    SquareKilometer("Square Kilometer", "km²", 1000000.0),
    SquareInch("Square Inch", "in²", 0.00064516),
    SquareFoot("Square Foot", "ft²", 0.092903),
    SquareYard("Square Yard", "yd²", 0.836127),
    Acre("Acre", "ac", 4046.86),
    Hectare("Hectare", "ha", 10000.0),
    SquareMile("Square Mile", "mi²", 2589988.0)
}

enum class LengthUnit(override val displayName: String, override val symbol: String, val toMeters: Double) : ConversionUnit {
    Millimeter("Millimeter", "mm", 0.001),
    Centimeter("Centimeter", "cm", 0.01),
    Meter("Meter", "m", 1.0),
    Kilometer("Kilometer", "km", 1000.0),
    Inch("Inch", "in", 0.0254),
    Foot("Foot", "ft", 0.3048),
    Yard("Yard", "yd", 0.9144),
    Mile("Mile", "mi", 1609.344),
    NauticalMile("Nautical Mile", "nmi", 1852.0),
    LightYear("Light Year", "ly", 9.461e15)
}

enum class TemperatureUnit(override val displayName: String, override val symbol: String) : ConversionUnit {
    Celsius("Celsius", "°C"),
    Fahrenheit("Fahrenheit", "°F"),
    Kelvin("Kelvin", "K")
}

enum class VolumeUnit(override val displayName: String, override val symbol: String, val toLiters: Double) : ConversionUnit {
    Milliliter("Milliliter", "ml", 0.001),
    Liter("Liter", "L", 1.0),
    CubicMeter("Cubic Meter", "m³", 1000.0),
    CubicInch("Cubic Inch", "in³", 0.0163871),
    CubicFoot("Cubic Foot", "ft³", 28.3168),
    CubicYard("Cubic Yard", "yd³", 764.555),
    FluidOunce("Fluid Ounce", "fl oz", 0.0295735),
    Cup("Cup", "cup", 0.236588),
    Pint("Pint", "pt", 0.473176),
    Quart("Quart", "qt", 0.946353),
    Gallon("Gallon", "gal", 3.78541)
}

enum class MassUnit(override val displayName: String, override val symbol: String, val toGrams: Double) : ConversionUnit {
    Milligram("Milligram", "mg", 0.001),
    Gram("Gram", "g", 1.0),
    Kilogram("Kilogram", "kg", 1000.0),
    Tonne("Tonne", "t", 1000000.0),
    Ounce("Ounce", "oz", 28.3495),
    Pound("Pound", "lb", 453.592),
    Stone("Stone", "st", 6350.29),
    Ton("Ton", "ton", 907184.74)
}

enum class DataUnit(override val displayName: String, override val symbol: String, val toBytes: Double) : ConversionUnit {
    Bit("Bit", "bit", 0.125),
    Byte("Byte", "B", 1.0),
    Kilobyte("Kilobyte", "KB", 1000.0),
    Megabyte("Megabyte", "MB", 1000000.0),
    Gigabyte("Gigabyte", "GB", 1000000000.0),
    Terabyte("Terabyte", "TB", 1000000000000.0),
    Kibibyte("Kibibyte", "KiB", 1024.0),
    Mebibyte("Mebibyte", "MiB", 1048576.0),
    Gibibyte("Gibibyte", "GiB", 1073741824.0),
    Tebibyte("Tebibyte", "TiB", 1099511627776.0)
}

enum class SpeedUnit(override val displayName: String, override val symbol: String, val toMetersPerSecond: Double) : ConversionUnit {
    MetersPerSecond("Meters per Second", "m/s", 1.0),
    KilometersPerHour("Kilometers per Hour", "km/h", 0.277778),
    MilesPerHour("Miles per Hour", "mph", 0.44704),
    Knot("Knot", "kn", 0.514444),
    FeetPerSecond("Feet per Second", "ft/s", 0.3048)
}

enum class TimeUnit(override val displayName: String, override val symbol: String, val toSeconds: Double) : ConversionUnit {
    Nanosecond("Nanosecond", "ns", 0.000000001),
    Microsecond("Microsecond", "μs", 0.000001),
    Millisecond("Millisecond", "ms", 0.001),
    Second("Second", "s", 1.0),
    Minute("Minute", "min", 60.0),
    Hour("Hour", "h", 3600.0),
    Day("Day", "d", 86400.0),
    Week("Week", "wk", 604800.0),
    Month("Month", "mo", 2629746.0), // Average month
    Year("Year", "yr", 31556952.0)  // Average year
}

enum class TipUnit(override val displayName: String, override val symbol: String, val percentage: Double) : ConversionUnit {
    FivePercent("5%", "5%", 5.0),
    TenPercent("10%", "10%", 10.0),
    FifteenPercent("15%", "15%", 15.0),
    TwentyPercent("20%", "20%", 20.0),
    TwentyFivePercent("25%", "25%", 25.0),
    ThirtyPercent("30%", "30%", 30.0)
}
