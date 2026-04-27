package com.nevaxr.foundation.car

import com.nevaxr.foundation.car.device.NCarDoorState

interface NCarSpecGeneric : NCarSpec {
    val deviceId: NCarStateProperty<String?>
    val model: NCarStateProperty<String?>
    val brand: NCarStateProperty<String?>

    val hvacTemperatureAreaIds: NCarHvacTemperatureAreaIds
    val seatOccupancyAreaIds: NCarQuadAreaIds
    val doorAreaIds: NCarQuadAreaIds

    val speedRange: MeasurementUnitRange<UnitSpeed>
    val speed: NCarStateProperty<MeasurementRanged<UnitSpeed>>
    val gear: NCarStateProperty<NCarGear>
    val evChargingRate: NCarStateProperty<MeasurementRanged<UnitPower>>
    val hvacStatus: NCarStateProperty<Boolean>
    val hvacDualStatus: NCarStateProperty<Boolean>
    val hvacMaxStatus: NCarStateProperty<Boolean>
    val hvacFanSpeed: NCarStateProperty<MeasurementRanged<UnitRpm>>
    val hvacPassengerSpeed: NCarStateProperty<MeasurementRanged<UnitRpm>>
    val hvacTemperature: NCarStateProperty<NCarHvacTemperatureState>
    val hvacInteriorTemperature: NCarStateProperty<Measurement<UnitTemperature>>
    val hvacExteriorTemperature: NCarStateProperty<Measurement<UnitTemperature>>
    val seatOccupancy: NCarStateProperty<NCarSeatOccupancyState>
    val batteryCapacity: NCarStateProperty<MeasurementRanged<UnitEnergy>>
    val battery: NCarStateProperty<Float>
    val engine: NCarStateProperty<MeasurementRanged<UnitPower>>
    val acceleration: NCarStateProperty<Float>
    val steeringWheelAngle: NCarStateProperty<Measurement<UnitAngle>>
    val doorState: NCarStateProperty<NCarDoorState>
    val trunkState: NCarStateProperty<Boolean>
    val trunkAngle: NCarStateProperty<Measurement<UnitAngle>>
    val frunkState: NCarStateProperty<Boolean>
    val frunkAngle: NCarStateProperty<Measurement<UnitAngle>>
    val windowState: NCarStateProperty<NCarWindowState>
    val ambientLight: NCarStateProperty<NCarAmbientColor>
    val ambientLightSupportedHexColors: List<String>
    val ambientLightControl: NCarPropertyWritable<String>
    val truIdToken: NCarStateProperty<TruIdAuthResult?>
}
