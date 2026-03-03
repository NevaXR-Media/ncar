package com.nevaxr.foundation.car.demo

import androidx.compose.runtime.Stable
import com.nevaxr.foundation.car.NCar
import com.nevaxr.foundation.car.NCarSpecGeneric
import com.nevaxr.foundation.car.NSensorRate
import com.nevaxr.foundation.car.NCarGear
import com.nevaxr.foundation.car.NCarSpecTogg
import kotlinx.coroutines.async

@Stable
class CarState(private val car: NCar<NCarSpecTogg>) {
    val speed = car.stateOf(car.spec.speed, NSensorRate.Fastest)
    val gear = car.stateOf(car.spec.gear, NSensorRate.OnChange)

    val deviceId = car.scope.async { car.read(car.spec.deviceId) }
    val brand = car.scope.async { car.read(car.spec.brand) }
    val model = car.scope.async { car.read(car.spec.model) }

    val deviceInfo = car.scope.async {
        val deviceId = deviceId.await()
        val brand = brand.await()
        val model = model.await()

        CarInfo(
            deviceId,
            brand,
            model
        )
    }
}

data class CarInfo(
    val id: String,
    val brand: String?,
    val model: String?,
)

