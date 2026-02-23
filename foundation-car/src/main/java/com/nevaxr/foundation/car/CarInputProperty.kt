package com.nevaxr.foundation.car

import android.car.hardware.property.CarPropertyManager

data class CarPropertyIdentifier(val raw: Int, val description: String)
sealed interface CarProperty {
    val identifier: CarPropertyIdentifier

    class Generic<Raw>(override val identifier: CarPropertyIdentifier) : CarProperty
    class Transformative<Raw, T>(override val identifier: CarPropertyIdentifier, val mapper: (Raw) -> T) : CarProperty
    class Numeric(override val identifier: CarPropertyIdentifier) : CarProperty
    class NumericRange(override val identifier: CarPropertyIdentifier, val range: ClosedFloatingPointRange<Float>): CarProperty
    class Measurable<BaseUnit: RawFloatUnit>(override val identifier: CarPropertyIdentifier, val unit: BaseUnit, val range: ClosedFloatingPointRange<Float>) : CarProperty
}

//interface CarRawProperty<Identifier, Raw> {
//    val id: Identifier
//}
//
//interface CarFloatProperty<Identifier> : CarRawProperty<Identifier, Float>
//
//interface CarNormalizableProperty<Identifier> : CarFloatProperty<Identifier> {
//    val range: ClosedFloatingPointRange<Float>
//    fun normalize(rawValue: Float): Float {
//        val clamped = rawValue.coerceIn(range.start, range.endInclusive)
//        val len = range.endInclusive - range.start
//        return (clamped - range.start) / len
//    }
//}
//
//interface CarMeasurableProperty<Identifier, ValueUnit: RawFloatUnit> : CarRawProperty<Identifier, Float> {
//    val rawUnit: ValueUnit
//    fun <TargetUnit: UnitConvertibleFrom<ValueUnit>> convertToUnit(rawValue: Float, targetUnit: TargetUnit): Float {
//        return targetUnit.from(rawValue, rawUnit)
//    }
//}
//
//interface CarMeasurableAndNormalizableProperty<Identifier, ValueUnit : RawFloatUnit> : CarNormalizableProperty<Identifier>, CarMeasurableProperty<Identifier, ValueUnit>
//
//data class CarSpeedProperty<Identifier>(override val id: Identifier, override val rawUnit: Speed, override val range: ClosedFloatingPointRange<Float>) : CarMeasurableAndNormalizableProperty<Identifier, Speed>