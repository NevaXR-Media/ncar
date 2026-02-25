package com.nevaxr.foundation.car

import android.car.hardware.property.CarPropertyManager

enum class NSensorRate(val raw: Float) {
    OnChange(CarPropertyManager.SENSOR_RATE_ONCHANGE),
    Normal(CarPropertyManager.SENSOR_RATE_NORMAL),
    UI(CarPropertyManager.SENSOR_RATE_UI),
    Fast(CarPropertyManager.SENSOR_RATE_FAST),
    Fastest(CarPropertyManager.SENSOR_RATE_FASTEST),
}
