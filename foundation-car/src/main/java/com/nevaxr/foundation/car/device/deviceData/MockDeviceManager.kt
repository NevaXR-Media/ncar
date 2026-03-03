package com.nevaxr.device.deviceData

import com.nevaxr.device.*
import com.nevaxr.foundation.car.NVhalKey
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import timber.log.Timber
import kotlin.time.Duration


//private fun <T> createMockDeviceProperty(
//    id: Int,
//    name: String,
//    value: T,
//    areaId: Int = 0
//): DeviceProperty<T> {
//    return DeviceProperty(
//        timestamp = System.currentTimeMillis(),
//        id = id,
//        name = name,
//        value = value,
//        areaId = areaId
//    )
//}

//@OptIn(ExperimentalSerializationApi::class)
//object MockDeviceManager : DeviceManager {
//
//    private val featureConfig: DeviceConfig
//    private val featureMap: Map<String, Feature>
//
//    private val device: Device?
//
//    init {
//        val json = Json {
//            ignoreUnknownKeys = true
//            allowTrailingComma = true
//        }
//        featureConfig = json.decodeFromString(DEFAULT_DEVICE_DATA)
//        featureMap = buildFeatureMap(featureConfig)
//        device = featureConfig.devices.find { it.key == DeviceType.TOGG_T10X_RWD.name }
//    }
//
//    override fun isDeviceAvailable(): Boolean {
//        return true
//    }
//
//    override fun createDevice() {
//        Timber.d("createDevice called")
//    }
//
//    override fun release() {
//        Timber.d("release called")
//    }
//
//    override fun getDeviceInfo(): com.nevaxr.device.Device {
//        TODO("Not yet implemented")
//    }
//
//    override fun deviceIdentifier(): String? {
//        return "mock-device-id"
//    }
//
//    override fun setAmbientLight(ambientColors: AmbientColor) {
//        Timber.d("setAmbientLight called with $ambientColors")
//    }
//
//    override fun setCustomIntProperty(propertyId: Int, areaId: Int, value: Int) {
//        Timber.d("setCustomIntProperty called with propertyId=$propertyId, areaId=$areaId, value=$value")
//    }
//
//    override fun <T> getCustomProperty(propertyId: Int, areaId: Int): DeviceProperty<T?> {
//        return createMockDeviceProperty(
//            id = 0,
//            name = "property",
//            value = null,
//            areaId = 0
//        )
//    }
//
//    override fun fetchAllProperties(): Flow<List<DeviceProperty<String>>> = flow {
//        while (true) {
//            emit(
//                listOf(
//                    createMockDeviceProperty(
//                        id = 0,
//                        name = "property",
//                        value = "mock_property_value",
//                        areaId = 0
//                    )
//                )
//            )
//            delay(500)
//        }
//    }
//
//    override fun ambientLightState(): Flow<DeviceProperty<Int>> = flow {
//        while (true) {
//            emit(
//                createMockDeviceProperty(
//                    id = NVhalKey.VENDOR_AMBIENT_LIGHT_READ.id,
//                    name = NVhalKey.VENDOR_AMBIENT_LIGHT_READ.property,
//                    value = ConstantAmbientColor.BLUE.ambientColors.order
//                )
//            )
//            delay(500)
//        }
//    }
//
//    override fun ambientLight(): DeviceProperty<Int>? {
//        return createMockDeviceProperty(
//            id = NVhalKey.VENDOR_AMBIENT_LIGHT_READ.id,
//            name = NVhalKey.VENDOR_AMBIENT_LIGHT_READ.property,
//            value = ConstantAmbientColor.BLUE.ambientColors.order
//        )
//    }
//
//    override fun speed(): Flow<DeviceProperty<Float?>> = flow {
//        while (true) {
//            emit(
//                createMockDeviceProperty(
//                    id = NVhalKey.PERF_VEHICLE_SPEED.id,
//                    name = NVhalKey.PERF_VEHICLE_SPEED.property,
//                    value = 88.8f
//                )
//            )
//            delay(500)
//        }
//    }
//
//    override fun gearState(sensorRate: Float): Flow<DeviceProperty<Int?>> = flow {
//        while (true) {
//            emit(
//                createMockDeviceProperty(
//                    id = 0,
//                    name = NVhalKey.GEAR_SELECTION.property,
//                    value = 3
//                )
//            )
//            delay(500)
//        }
//    }
//
//    override fun drivingMode(): Flow<DeviceProperty<Int?>> = flow {
//        while (true) {
//            emit(
//                createMockDeviceProperty(
//                    id = 0,
//                    name = NVhalKey.VENDOR_DRIVE_MODE_PROPERTY.property,
//                    value = 1
//                )
//            )
//            delay(500)
//        }
//    }
//
//    override fun evChargingRate(): Flow<DeviceProperty<Float?>> = flow {
//        while (true) {
//            emit(
//                createMockDeviceProperty(
//                    id = 0,
//                    name = NVhalKey.EV_BATTERY_INSTANTANEOUS_CHARGE_RATE.property,
//                    value = 10.5f
//                )
//            )
//            delay(500)
//        }
//    }
//
//    override fun throttle(): Flow<DeviceProperty<Float?>> = flow {
//        while (true) {
//            emit(
//                createMockDeviceProperty(
//                    id = 0,
//                    name = "Throttle",
//                    value = 0.5f
//                )
//            )
//            delay(500)
//        }
//    }
//
//    override fun brake(): Flow<DeviceProperty<Float?>> = flow {
//        while (true) {
//            emit(
//                createMockDeviceProperty(
//                    id = 0,
//                    name = "Brake",
//                    value = 0.1f
//                )
//            )
//            delay(500)
//        }
//    }
//
//    override fun hvacAcStatus(): Flow<DeviceProperty<Boolean?>> = flow {
//        while (true) {
//            emit(
//                createMockDeviceProperty(
//                    id = NVhalKey.HVAC_AC_ON.id,
//                    name = NVhalKey.HVAC_AC_ON.property,
//                    value = true
//                )
//            )
//            delay(500)
//        }
//    }
//
//    override fun formattedSpeed(): Flow<List<DataPoint<Float>>?> = flow {
//        while (true) {
//            // Reuse existing mock speed data
//            createMockDeviceProperty(
//                id = NVhalKey.PERF_VEHICLE_SPEED.id,
//                name = NVhalKey.PERF_VEHICLE_SPEED.property,
//                value = 88.8f
//            )
//
//            delay(500)
//        }
//    }
//
//    override fun formattedGearState(sensorRate: Float): Flow<List<DataPoint<String?>>> = flow {
//        while (true) {
//            // Reuse existing mock gear data
//            val mockGearProperty = createMockDeviceProperty(
//                id = NVhalKey.GEAR_SELECTION.id,
//                name = NVhalKey.GEAR_SELECTION.property,
//                value = 3
//            )
//            // Emit mock formatted data point
//            emit(
//                listOf(
//                    DataPoint(
//                        key = "SENSOR:GEAR:NORMALIZED",
//                        value = "DRIVE", // Mock string value for gear state
//                        timestamp = mockGearProperty.timestamp
//                    )
//                )
//            )
//            delay(500)
//        }
//    }
//
//    override fun formattedDrivingMode(): Flow<List<DataPoint<String?>>?> = flow {
//        while (true) {
//            // Reuse existing mock driving mode data
//            val mockDrivingModeProperty = createMockDeviceProperty(
//                id = NVhalKey.VENDOR_DRIVE_MODE_PROPERTY.id,
//                name = NVhalKey.VENDOR_DRIVE_MODE_PROPERTY.property,
//                value = 1
//            )
//
//            emit(
//                listOf(
//                    DataPoint(
//                        key = "SENSOR:DRIVING_MODE:RAW",
//                        value = "NORMAL",
//                        timestamp = mockDrivingModeProperty.timestamp
//                    )
//                )
//            )
//            delay(500)
//        }
//    }
//
//    override fun formattedEvChargingRate(): Flow<List<DataPoint<Float>>> = flow {
//        while (true) {
//            // Reuse existing mock charging rate data
//            val mockChargingProperty = createMockDeviceProperty(
//                id = NVhalKey.EV_BATTERY_INSTANTANEOUS_CHARGE_RATE.id,
//                name = NVhalKey.EV_BATTERY_INSTANTANEOUS_CHARGE_RATE.property,
//                value = 10.5f
//            )
//            // Emit mock formatted data point
//            emit(
//                listOf(
//                    DataPoint(
//                        key = "SENSOR:BATTERY_CHARGE_RATE:RAW",
//                        value = 10.5f,
//                        timestamp = mockChargingProperty.timestamp
//                    )
//                )
//            )
//            delay(500)
//        }
//    }
//
//    override fun formattedThrottle(): Flow<List<DataPoint<Float>>> = flow {
//        while (true) {
//            // Reuse existing mock throttle data
//            val mockThrottleProperty = createMockDeviceProperty(
//                id = 0,
//                name = "Throttle",
//                value = 0.5f
//            )
//            // Emit mock formatted data points
//            emit(
//                listOf(
//                    DataPoint(
//                        key = "SENSOR:THROTTLE:RAW",
//                        value = 0.5f,
//                        timestamp = mockThrottleProperty.timestamp
//                    ),
//                    DataPoint(
//                        key = "SENSOR:THROTTLE:NORMALIZED",
//                        value = 0.5f,
//                        timestamp = mockThrottleProperty.timestamp
//                    )
//                )
//            )
//            delay(500)
//        }
//    }
//
//    override fun formattedBrake(): Flow<List<DataPoint<Float>>> = flow {
//        while (true) {
//            // Reuse existing mock brake data
//            val mockBrakeProperty = createMockDeviceProperty(
//                id = 0,
//                name = "Brake",
//                value = 0.1f
//            )
//            // Emit mock formatted data points
//            emit(
//                listOf(
//                    DataPoint(
//                        key = "SENSOR:BRAKE:RAW",
//                        value = 0.1f,
//                        timestamp = mockBrakeProperty.timestamp
//                    ),
//                    DataPoint(
//                        key = "SENSOR:BRAKE:NORMALIZED",
//                        value = 0.1f,
//                        timestamp = mockBrakeProperty.timestamp
//                    )
//                )
//            )
//            delay(500)
//        }
//    }
//
//    override fun hvacDualStatus(): Flow<DeviceProperty<Boolean?>> = flow {
//        while (true) {
//            emit(
//                createMockDeviceProperty(
//                    id = NVhalKey.HVAC_DUAL_ON.id,
//                    name = NVhalKey.HVAC_DUAL_ON.property,
//                    value = true
//                )
//            )
//            delay(500)
//        }
//    }
//
//    override fun hvacMaxStatus(): Flow<DeviceProperty<Boolean?>> = flow {
//        while (true) {
//            emit(
//                createMockDeviceProperty(
//                    id = NVhalKey.HVAC_MAX_AC_ON.id,
//                    name = NVhalKey.HVAC_MAX_AC_ON.property,
//                    value = false
//                )
//            )
//            delay(500)
//        }
//    }
//
//    override fun hvacSpeed(): Flow<DeviceProperty<Int?>> = flow {
//        while (true) {
//            emit(
//                createMockDeviceProperty(
//                    id = NVhalKey.HVAC_FAN_SPEED.id,
//                    name = NVhalKey.HVAC_FAN_SPEED.property,
//                    value = 3
//                )
//            )
//            delay(500)
//        }
//    }
//
//    override fun hvacTemperature(): Flow<List<DeviceProperty<Float?>>> = flow {
//        while (true) {
//            emit(
//                listOf(
//                    createMockDeviceProperty(
//                        id = NVhalKey.HVAC_TEMPERATURE_SET.id,
//                        name = NVhalKey.HVAC_TEMPERATURE_SET.property,
//                        value = 22.5f,
//                        areaId = 0 // Driver area
//                    ),
//                    createMockDeviceProperty(
//                        id = NVhalKey.HVAC_TEMPERATURE_SET.id,
//                        name = NVhalKey.HVAC_TEMPERATURE_SET.property,
//                        value = 23.0f,
//                        areaId = 1 // Passenger area
//                    )
//                )
//            )
//            delay(500)
//        }
//    }
//
//    override fun formattedAcStatus(): Flow<List<DataPoint<Boolean>>> {
//        return flowOf(emptyList())
//    }
//
//    override fun formattedHvacDualStatus(): Flow<List<DataPoint<Boolean>>> {
//        return flowOf(emptyList())
//    }
//
//    override fun formattedHvacMaxStatus(): Flow<List<DataPoint<Boolean>>> {
//        return flowOf(emptyList())
//    }
//
//    override fun formattedHvacSpeed(): Flow<List<DataPoint<Int>>> {
//        return flowOf(emptyList())
//    }
//
//    override fun formattedHvacTemperature(): Flow<List<List<DataPoint<Float>>>> {
//        return flowOf(emptyList())
//    }
//
//    override fun batteryCapacity(): Flow<DeviceProperty<Float?>> {
//        return flowOf()
//    }
//
//    override fun formattedBatteryCapacity(): Flow<List<DataPoint<Float>>> {
//        return flowOf()
//    }
//
//    override fun battery(): Flow<DeviceProperty<Float?>> {
//        return flowOf()
//    }
//
//    override fun engine(): Flow<DeviceProperty<Float?>> {
//        return flowOf()
//    }
//
//    override fun formattedEngine(): Flow<List<DataPoint<Float>>> {
//        return flowOf()
//    }
//
//    override fun acceleration(): Flow<DeviceProperty<Float?>> {
//        return flowOf()
//    }
//
//    override fun formattedAcceleration(): Flow<List<DataPoint<Float>>> {
//        return flowOf()
//    }
//
//    override fun formattedBatteryData(): Flow<List<DataPoint<Float>>> {
//        return flowOf()
//    }
//
//    override fun hvacPassengerSpeed(): Flow<DeviceProperty<Int?>> {
//        return flowOf()
//    }
//
//    override fun formattedHvacPassengerSpeed(): Flow<List<DataPoint<Int>>> {
//        return flowOf()
//    }
//
//    override fun hvacInteriorTemperature(): Flow<DeviceProperty<Float?>> {
//        return flowOf()
//    }
//
//    override fun formattedHvacInteriorTemperature(): Flow<List<DataPoint<Float>>> {
//        return flowOf()
//    }
//
//    override fun hvacExteriorTemperature(): Flow<DeviceProperty<Float?>> {
//        return flowOf()
//    }
//
//    override fun formattedHvacExteriorTemperature(): Flow<List<DataPoint<Float>>> {
//        return flowOf()
//    }
//
//    override fun seatOccupancy(): Flow<DeviceProperty<Int?>> {
//        return flowOf()
//    }
//
//    override fun formattedSeatOccupancyData(): Flow<List<DataPoint<Int>>> {
//        return flowOf()
//    }
//
//    override fun direction(): Flow<DeviceProperty<GyroscopeData?>> {
//        return flowOf()
//    }
//
//    override fun formattedDirection(): Flow<List<DataPoint<Float>>> {
//        return flowOf()
//    }
//
//    override fun formattedLocation(): Flow<DataPoint<List<Float>>?> {
//        return flowOf()
//    }
//
//    override fun formattedAltitude(): Flow<DataPoint<Float>?> {
//        return flowOf()
//    }
//
//    override fun location(updateInterval: Duration): Flow<LocationState> {
//        return flowOf()
//    }
//
//    override fun altitude(): Flow<Float> {
//        return flowOf()
//    }
//
//    override fun steeringWheelAngleData(): Flow<DeviceProperty<Float?>> {
//        return flowOf()
//    }
//
//    override fun formattedSteeringWheelAngleData(): Flow<List<DataPoint<Float>>> {
//        return flowOf()
//    }
//
//    override fun doorState(): Flow<List<DeviceProperty<Int?>>> {
//        return flowOf()
//    }
//
//    override fun formattedDoorState(): Flow<List<DataPoint<List<Boolean>>>> {
//        return flowOf()
//    }
//
//    override fun trunkState(): Flow<List<DeviceProperty<Int?>>> {
//        return flowOf()
//    }
//
//    override fun formattedTrunkState(): Flow<List<DataPoint<Boolean>>> {
//        return flowOf()
//    }
//
//    override fun frunkState(): Flow<List<DeviceProperty<Int?>>> {
//        return flowOf()
//    }
//
//    override fun formattedFrunkState(): Flow<List<DataPoint<Boolean>>> {
//        return flowOf()
//    }
//
//    override fun formattedTrunkAngle(): Flow<List<DataPoint<Float>>> {
//        return flowOf()
//    }
//
//    override fun formattedFrunkAngle(): Flow<List<DataPoint<Float>>> {
//        return flowOf()
//    }
//
//    override fun windowsState(): Flow<List<DeviceProperty<Int?>>> {
//        return flowOf()
//    }
//
//    override fun formattedWindowsState(): Flow<List<DataPoint<List<Int>>>> {
//        return flowOf()
//    }
//
//    override fun formattedAllData(): Flow<List<DataPoint<out Any?>>?> {
//        return flowOf()
//    }
//
//    override fun getFormattedGearState(): List<DataPoint<String?>>? {
//        return null
//    }
//}