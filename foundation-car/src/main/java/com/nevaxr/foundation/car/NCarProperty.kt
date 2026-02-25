package com.nevaxr.foundation.car

import com.nevaxr.device.NCarPropertyId

sealed interface NCarProperty<Output> {
    data class Raw<T>(val id: NCarPropertyId): NCarProperty<T>
    data class RawArea<T>(val id: NCarPropertyId, val areaId: Int): NCarProperty<T>
    data class RawTransform<Raw, T>(val id: NCarPropertyId, val transform: (Raw) -> T): NCarProperty<T>
    data class RawTransformArea<Raw, T>(val id: NCarPropertyId, val areaId: Int, val transform: (Raw) -> T): NCarProperty<T>
    data class Measurable<BaseUnit : MeasurementUnit>(val id: NCarPropertyId, val unit: BaseUnit) : NCarProperty<Float>
    data class MeasurableArea<BaseUnit : MeasurementUnit>(val id: NCarPropertyId, val areaId: Int, val unit: BaseUnit) : NCarProperty<Float>
    data class MeasurableRanged<BaseUnit : MeasurementUnit>(val id: NCarPropertyId, val unit: BaseUnit, val range: ClosedFloatingPointRange<Float>) : NCarProperty<Float>
    data class MeasurableRangedArea<BaseUnit : MeasurementUnit>(val id: NCarPropertyId, val areaId: Int, val unit: BaseUnit, val range: ClosedFloatingPointRange<Float>) : NCarProperty<Float>
}
