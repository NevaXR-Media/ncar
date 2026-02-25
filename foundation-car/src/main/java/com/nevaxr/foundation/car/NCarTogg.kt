package com.nevaxr.foundation.car

import com.nevaxr.device.Device
import com.nevaxr.device.NCarPropertyId

object Togg : NCarSpec() {
    val deviceId = rawProperty<String>(NCarPropertyId.INFO_VIN, 0)
    val model = rawProperty<String>(NCarPropertyId.INFO_MODEL, 0)
    val brand = rawProperty<String>(NCarPropertyId.INFO_MAKE, 0)

    fun deviceInfo(carService: NCarService): Device {
        val deviceId by carService.propertyValue(deviceId)
        val model by carService.propertyValue(model)
        val brand by carService.propertyValue(brand)

        return Device(
            identification = deviceId,
            model = model,
            brand = brand,
            title = model,
        )
    }

    val speed = measurable<SpeedUnit>(NCarPropertyId.PERF_VEHICLE_SPEED, SpeedUnit.Mps, 0f..51.3889f)
    val engine = measurable(NCarPropertyId.ENGINE_RPM, RpmUnit)
    val gear = raw(NCarPropertyId.GEAR_SELECTION) { rawGear: Int ->
        when (rawGear) {
            1 -> NVehicleGear.Neutral
            2 -> NVehicleGear.Reverse
            4 -> NVehicleGear.Park
            8 -> NVehicleGear.Drive
            else -> NVehicleGear.Park
        }
    }
}
