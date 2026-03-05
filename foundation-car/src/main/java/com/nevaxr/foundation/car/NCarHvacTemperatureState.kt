package com.nevaxr.foundation.car

data class NCarHvacTemperatureState(
    val driver: Measurement<UnitTemperature>,
    val passenger: Measurement<UnitTemperature>,
)
