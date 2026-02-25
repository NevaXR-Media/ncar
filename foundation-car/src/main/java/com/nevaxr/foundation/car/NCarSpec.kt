package com.nevaxr.foundation.car

import com.nevaxr.device.NCarPropertyId

abstract class NCarSpec {
    companion object  {
        typealias Property = NCarPropertyId
        inline fun <Raw> raw(property: Property) = NCarProperty.Raw<Raw>(property)
        inline fun <Raw, T> raw(property: Property, noinline transform: (Raw) -> T) = NCarProperty.RawTransform(property, transform)
        inline fun <Raw> rawProperty(property: Property, areaId: Int) = NCarProperty.RawArea<Raw>(property, areaId)
        inline fun <U : MeasurementUnit> measurable(property: Property, unit: U) = NCarProperty.Measurable(property, unit)
        inline fun <U : MeasurementUnit> measurableProperty(property: Property, areaId: Int, unit: U) = NCarProperty.MeasurableArea(property, areaId, unit)
        inline fun <U : MeasurementUnit> measurable(property: Property, unit: U, range: ClosedFloatingPointRange<Float>) = NCarProperty.MeasurableRanged(property, unit, range)
        inline fun <U : MeasurementUnit> measurableProperty(property: Property, areaId: Int, unit: U, range: ClosedFloatingPointRange<Float>) = NCarProperty.MeasurableRangedArea(property, areaId, unit, range)
    }
}
