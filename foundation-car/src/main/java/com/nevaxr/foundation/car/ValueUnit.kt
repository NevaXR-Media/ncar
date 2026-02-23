package com.nevaxr.foundation.car

interface RawUnit<Value> {
    val cofactor: Value
}

interface RawFloatUnit: RawUnit<Float> {
    override val cofactor get() = 1f
}

interface UnitConvertibleFrom<T: RawFloatUnit>: RawFloatUnit {
    fun from(value: Float, unit: T): Float {
        val base = value / unit.cofactor
        return base * cofactor
    }
}

sealed interface Speed : RawFloatUnit, UnitConvertibleFrom<Speed> {
    object Kph : Speed
    object Mph : Speed { override val cofactor = 1000f }
}

class NothingUnit<T> : RawUnit<T> {
    override val cofactor: T get() = error("NothingUnit")
}