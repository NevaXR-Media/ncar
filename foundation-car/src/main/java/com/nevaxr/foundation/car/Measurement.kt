package com.nevaxr.foundation.car

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.nevaxr.device.deviceData.convertUnit
import java.util.Locale

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

fun <BaseUnit> Measurement<BaseUnit>.format(context: Context, fractionDigits: Int = 2): String where BaseUnit : MeasurementUnit {
    val symbol = context.resources.getString(unit.symbolRes)
    return "%.${fractionDigits}f %s".format(value, symbol)
}

fun <BaseUnit> Measurement<BaseUnit>.format(context: Context, targetUnit: BaseUnit, fractionDigits: Int = 2): String where BaseUnit: Dimension<BaseUnit> {
    val symbol = context.resources.getString(targetUnit.symbolRes)
    val value = unit.convert(value, targetUnit)
    return "%.${fractionDigits}f %s".format(value, symbol)
}

fun <BaseUnit> MeasurementRanged<BaseUnit>.format(context: Context, fractionDigits: Int = 2): String where BaseUnit : MeasurementUnit {
    val symbol = context.resources.getString(unit.symbolRes)
    return "%.${fractionDigits}f %s".format(value, symbol)
}

fun <BaseUnit> MeasurementRanged<BaseUnit>.format(context: Context, targetUnit: BaseUnit, fractionDigits: Int = 2): String where BaseUnit : Dimension<BaseUnit> {
    val symbol = context.resources.getString(targetUnit.symbolRes)
    val value = unit.convert(value, targetUnit)
    return "%.${fractionDigits}f %s".format(value, symbol)
}

@Composable
fun <BaseUnit> Measurement<BaseUnit>.format(fractionDigits: Int = 2): String where BaseUnit : MeasurementUnit {
    val context = LocalContext.current
    return format(context, fractionDigits)
}

@Composable
fun <BaseUnit> Measurement<BaseUnit>.format(unit: BaseUnit, fractionDigits: Int = 2): String where BaseUnit : Dimension<BaseUnit> {
    val context = LocalContext.current
    return format(context, unit, fractionDigits)
}

@Composable
fun <BaseUnit> MeasurementRanged<BaseUnit>.format(fractionDigits: Int = 2): String where BaseUnit : MeasurementUnit {
    val context = LocalContext.current
    return format(context, fractionDigits)
}

@Composable
fun <BaseUnit> MeasurementRanged<BaseUnit>.format(unit: BaseUnit, fractionDigits: Int = 2): String where BaseUnit : Dimension<BaseUnit> {
    val context = LocalContext.current
    return format(context, unit, fractionDigits)
}
