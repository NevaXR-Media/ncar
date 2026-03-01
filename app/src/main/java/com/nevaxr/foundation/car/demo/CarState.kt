package com.nevaxr.foundation.car.demo

import androidx.compose.runtime.Stable
import com.nevaxr.foundation.car.Car
import com.nevaxr.foundation.car.CarService
import com.nevaxr.foundation.car.GenericCarSpec
import com.nevaxr.foundation.car.NSensorRate
import com.nevaxr.foundation.car.NVehicleGear
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async

@Stable
class CarState(private val car: Car<GenericCarSpec>) {
    val speed = car.stateOf(car.spec.speed, NSensorRate.Fastest)
    val gear = car.stateOf(car.spec.gear, NSensorRate.OnChange, NVehicleGear.Park)

    val deviceId = coroutineScope.async { car.read(spec.deviceId) }
    val brand = coroutineScope.async { car.read(spec.brand) }
    val model = coroutineScope.async { car.read(spec.model) }
}

