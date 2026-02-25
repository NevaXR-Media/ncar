package com.nevaxr.foundation.car

const val SecondsPerHour = 60f * 60f

interface MeasurementUnit {
    /// Unit's name as resource id
    val displayNameRes: Int
}

interface SensorUnitConvertible<T : MeasurementUnit> {
    fun convert(value: Float, targetUnit: T): Float
}

object RpmUnit : MeasurementUnit, SensorUnitConvertible<RpmUnit> {
    override val displayNameRes: Int = R.string.unit_rpm
    override fun convert(value: Float, targetUnit: RpmUnit) = value
}

sealed interface EnergyUnit : MeasurementUnit, SensorUnitConvertible<EnergyUnit> {
    object Kw : EnergyUnit {
        override val displayNameRes = R.string.unit_kw
    }

    override fun convert(value: Float, targetUnit: EnergyUnit): Float {
        return when (this) {
            Kw -> value
        }
    }
}

sealed interface TemperatureUnit : MeasurementUnit, SensorUnitConvertible<TemperatureUnit> {
    object Celsius : TemperatureUnit {
        override val displayNameRes: Int = R.string.unit_celsius
    }

    object Fahrenheit : TemperatureUnit {
        override val displayNameRes: Int = R.string.unit_fahrenheit
    }

    override fun convert(value: Float, targetUnit: TemperatureUnit): Float {
        return when (Pair(this, targetUnit)) {
            Pair(Celsius, Fahrenheit) -> (value * (9f / 5f)) + 32f
            Pair(Fahrenheit, Celsius) -> (value - 32f) * (9f / 5f)
            else -> value
        }
    }
}

sealed interface SpeedUnit : MeasurementUnit, SensorUnitConvertible<SpeedUnit> {
    val timeFactor: Float
    val distanceFactor: Float

    // Kilometers per hour
    object KMph : SpeedUnit {
        override val timeFactor: Float = 1f
        override val distanceFactor: Float = 1f
        override val displayNameRes: Int = R.string.unit_kmph
    }

    // Meters per hour
    object Mph : SpeedUnit {
        override val timeFactor: Float = 1f
        override val distanceFactor: Float = 1000f
        override val displayNameRes: Int = R.string.unit_mph
    }

    // Meters per second
    object Mps : SpeedUnit {
        override val timeFactor: Float = SecondsPerHour
        override val distanceFactor: Float = 1000f
        override val displayNameRes: Int = R.string.unit_mps
    }

    // Miles per hour
    object Miph : SpeedUnit {
        override val timeFactor: Float = SecondsPerHour
        override val distanceFactor: Float = 0.6213712f
        override val displayNameRes: Int = R.string.unit_miph
    }

    override fun convert(value: Float, targetUnit: SpeedUnit): Float {
        val baseValue = (value / distanceFactor) * targetUnit.distanceFactor
        return (baseValue / timeFactor) * targetUnit.timeFactor
    }
}

data class Measurable<U : MeasurementUnit>(val value: Float, val unit: U)
data class ClosedMeasurableRange<U: MeasurementUnit>(val start: Float, val endInclusive: Float, val unit: U)
data class MeasurableRanged<U: MeasurementUnit>(val value: Float, val unit: U, val range: ClosedMeasurableRange<U>)

fun <BaseUnit> Measurable<BaseUnit>.convert(targetUnit: BaseUnit): Measurable<BaseUnit> where BaseUnit : MeasurementUnit, BaseUnit : SensorUnitConvertible<BaseUnit> {
    return if (unit == targetUnit) {
        this
    } else {
        Measurable(
            value = unit.convert(value, targetUnit),
            unit = targetUnit
        )
    }
}
fun <BaseUnit> ClosedMeasurableRange<BaseUnit>.convert(targetUnit: BaseUnit): ClosedMeasurableRange<BaseUnit> where BaseUnit : MeasurementUnit, BaseUnit : SensorUnitConvertible<BaseUnit> {
    return if (unit == targetUnit) {
        this
    } else {
        ClosedMeasurableRange(
            start = unit.convert(start, targetUnit),
            endInclusive = unit.convert(endInclusive, targetUnit),
            unit = targetUnit,
        )
    }
}

fun <BaseUnit> Measurable<BaseUnit>.normalized(range: ClosedMeasurableRange<BaseUnit>): Float where BaseUnit : MeasurementUnit, BaseUnit : SensorUnitConvertible<BaseUnit> {
    val value = convert(range.unit).value.coerceAtLeast(range.start)
    return (value - range.start) / range.endInclusive
}

fun <BaseUnit> MeasurableRanged<BaseUnit>.convert(targetUnit: BaseUnit): MeasurableRanged<BaseUnit> where BaseUnit: MeasurementUnit, BaseUnit : SensorUnitConvertible<BaseUnit> {
    return MeasurableRanged(value = unit.convert(value, targetUnit), targetUnit, range)
}