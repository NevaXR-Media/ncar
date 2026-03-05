package com.nevaxr.foundation.car.device

import android.car.hardware.property.CarPropertyManager.SENSOR_RATE_ONCHANGE
import com.nevaxr.foundation.car.device.deviceData.DataPoint
import com.nevaxr.foundation.car.device.DeviceProperty
import kotlinx.coroutines.flow.Flow
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

interface DeviceManager : CarManager {

    fun isDeviceAvailable(): Boolean

    fun createDevice()

    fun release()

    fun deviceIdentifier(): String?

    fun getDeviceInfo(): Device

    fun ambientLightState(): Flow<DeviceProperty<Int>>

    fun ambientLight(): DeviceProperty<Int>?

    fun setAmbientLight(ambientColors: AmbientColor)

    fun setCustomIntProperty(propertyId: Int, areaId: Int, value: Int)

    fun <T> getCustomProperty(propertyId: Int, areaId: Int): DeviceProperty<T?>

    fun fetchAllProperties(): Flow<List<DeviceProperty<String>>>

    fun direction(): Flow<DeviceProperty<GyroscopeData?>>
    fun formattedDirection(): Flow<List<DataPoint<Float>>>

    fun location(updateInterval: Duration = 30.minutes): Flow<LocationState>
    fun formattedLocation(): Flow<DataPoint<List<Float>>?>

    fun altitude(): Flow<Float>
    fun formattedAltitude(): Flow<DataPoint<Float>?>
}

interface CarManager {
    fun speed(): Flow<DeviceProperty<Float?>>
    fun formattedSpeed(): Flow<List<DataPoint<Float>>?>

    fun gearState(sensorRate: Float =  SENSOR_RATE_ONCHANGE): Flow<DeviceProperty<Int?>>
    fun formattedGearState(sensorRate: Float =  SENSOR_RATE_ONCHANGE): Flow<List<DataPoint<String?>>>
    fun getFormattedGearState(): List<DataPoint<String?>>?

    fun drivingMode(): Flow<DeviceProperty<Int?>>
    fun formattedDrivingMode(): Flow<List<DataPoint<String?>>?>

    fun evChargingRate(): Flow<DeviceProperty<Float?>>
    fun formattedEvChargingRate(): Flow<List<DataPoint<Float>>>

    fun throttle(): Flow<DeviceProperty<Float?>>
    fun formattedThrottle(): Flow<List<DataPoint<Float>>>

    fun brake(): Flow<DeviceProperty<Float?>>
    fun formattedBrake(): Flow<List<DataPoint<Float>>>

    fun hvacAcStatus(): Flow<DeviceProperty<Boolean?>>
    fun formattedAcStatus(): Flow<List<DataPoint<Boolean>>>

    fun hvacDualStatus(): Flow<DeviceProperty<Boolean?>>
    fun formattedHvacDualStatus(): Flow<List<DataPoint<Boolean>>>

    fun hvacMaxStatus(): Flow<DeviceProperty<Boolean?>>
    fun formattedHvacMaxStatus(): Flow<List<DataPoint<Boolean>>>

    fun hvacSpeed(): Flow<DeviceProperty<Int?>>
    fun formattedHvacSpeed(): Flow<List<DataPoint<Int>>>

    fun hvacPassengerSpeed(): Flow<DeviceProperty<Int?>>
    fun formattedHvacPassengerSpeed(): Flow<List<DataPoint<Int>>>

    fun hvacTemperature(): Flow<List<DeviceProperty<Float?>>>
    fun formattedHvacTemperature(): Flow<List<List<DataPoint<Float>>>>

    fun hvacInteriorTemperature(): Flow<DeviceProperty<Float?>>
    fun formattedHvacInteriorTemperature(): Flow<List<DataPoint<Float>>>

    fun hvacExteriorTemperature(): Flow<DeviceProperty<Float?>>
    fun formattedHvacExteriorTemperature(): Flow<List<DataPoint<Float>>>

    fun batteryCapacity(): Flow<DeviceProperty<Float?>>
    fun formattedBatteryCapacity(): Flow<List<DataPoint<Float>>>

    fun battery(): Flow<DeviceProperty<Float?>>
    fun formattedBatteryData(): Flow<List<DataPoint<Float>>>

    fun engine(): Flow<DeviceProperty<Float?>>
    fun formattedEngine(): Flow<List<DataPoint<Float>>>

    fun acceleration(): Flow<DeviceProperty<Float?>>
    fun formattedAcceleration(): Flow<List<DataPoint<Float>>>

    fun seatOccupancy(): Flow<DeviceProperty<Int?>>
    fun formattedSeatOccupancyData(): Flow<List<DataPoint<Int>>>

    fun steeringWheelAngleData(): Flow<DeviceProperty<Float?>>
    fun formattedSteeringWheelAngleData(): Flow<List<DataPoint<Float>>>

    fun doorState(): Flow<List<DeviceProperty<Int?>>>
    fun formattedDoorState(): Flow<List<DataPoint<List<Boolean>>>>

    fun trunkState(): Flow<List<DeviceProperty<Int?>>>
    fun formattedTrunkState(): Flow<List<DataPoint<Boolean>>>

    fun frunkState(): Flow<List<DeviceProperty<Int?>>>
    fun formattedFrunkState(): Flow<List<DataPoint<Boolean>>>

    fun formattedTrunkAngle(): Flow<List<DataPoint<Float>>>

    fun formattedFrunkAngle(): Flow<List<DataPoint<Float>>>

    fun windowsState(): Flow<List<DeviceProperty<Int?>>>
    fun formattedWindowsState(): Flow<List<DataPoint<List<Int>>>>


    fun formattedAllData(): Flow<List<DataPoint<out Any?>>?>
}