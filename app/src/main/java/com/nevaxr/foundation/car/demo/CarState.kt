package com.nevaxr.foundation.car.demo

import androidx.compose.runtime.Stable
import com.nevaxr.foundation.car.NCarService
import com.nevaxr.foundation.car.NSensorRate
import com.nevaxr.foundation.car.NVehicleGear
import com.nevaxr.foundation.car.Togg

@Stable
class CarState(private val car: NCarService) {
    val speed = car.stateOf(Togg.speed, NSensorRate.Fastest)
    val gear = car.stateOf(Togg.gear, NSensorRate.OnChange, NVehicleGear.Park)

    fun deviceInfo() = Togg.deviceInfo(car)
}

