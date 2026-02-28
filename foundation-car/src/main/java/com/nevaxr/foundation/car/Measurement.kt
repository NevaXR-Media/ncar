package com.nevaxr.foundation.car

data class Measurement<U : MeasurementUnit>(val value: Float, val unit: U)
data class MeasurementUnitRange<U : MeasurementUnit>(val start: Float, val endInclusive: Float, val unit: U)
data class MeasurementRanged<U : MeasurementUnit>(val value: Float, val unit: U, val range: MeasurementUnitRange<U>)

fun <BaseUnit> Measurement<BaseUnit>.convert(targetUnit: BaseUnit): Measurement<BaseUnit> where BaseUnit : Dimension<BaseUnit> {
    return if (unit == targetUnit) {
        this
    } else {
        Measurement(
            value = unit.convert(value, targetUnit),
            unit = targetUnit
        )
    }
}

fun <BaseUnit> MeasurementUnitRange<BaseUnit>.convert(targetUnit: BaseUnit): MeasurementUnitRange<BaseUnit> where BaseUnit : Dimension<BaseUnit> {
    return if (unit == targetUnit) {
        this
    } else {
        MeasurementUnitRange(
            start = unit.convert(start, targetUnit),
            endInclusive = unit.convert(endInclusive, targetUnit),
            unit = targetUnit,
        )
    }
}

fun <BaseUnit> Measurement<BaseUnit>.normalized(range: MeasurementUnitRange<BaseUnit>): Float where BaseUnit : Dimension<BaseUnit> {
    val value = convert(range.unit).value.coerceAtLeast(range.start)
    return (value - range.start) / range.endInclusive
}

fun <BaseUnit> MeasurementRanged<BaseUnit>.convert(targetUnit: BaseUnit): MeasurementRanged<BaseUnit> where BaseUnit : Dimension<BaseUnit> {
    return MeasurementRanged(value = unit.convert(value, targetUnit), targetUnit, range)
}

fun <BaseUnit> MeasurementRanged<BaseUnit>.normalized(): Float where BaseUnit : Dimension<BaseUnit> {
    val value = convert(range.unit).value.coerceAtLeast(range.start)
    return (value - range.start) / range.endInclusive
}
