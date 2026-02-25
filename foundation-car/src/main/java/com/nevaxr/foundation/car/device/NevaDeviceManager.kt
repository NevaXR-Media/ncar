package com.nevaxr.device

import android.Manifest
import android.car.Car
import android.car.hardware.CarPropertyValue
import android.car.hardware.property.CarPropertyManager
import android.car.hardware.property.CarPropertyManager.SENSOR_RATE_ONCHANGE
import android.content.Context
import androidx.annotation.RequiresPermission
import com.nevaxr.device.deviceData.DEFAULT_DEVICE_DATA
import com.nevaxr.device.deviceData.DataPoint
import com.nevaxr.device.deviceData.DeviceDataParser
import com.nevaxr.device.deviceData.DeviceType
import getLocationFlow
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonPrimitive
import timber.log.Timber
import java.util.*
import kotlin.math.absoluteValue
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.time.Duration

/**
 * This class provides access to the car API features. It handles car connection, property retrieval, and event callbacks.
 * @param context The application context.
 */
class NevaDeviceManager(
    private val context: Context,
    private val deviceType: DeviceType,
    deviceDataJson: String? = DEFAULT_DEVICE_DATA
) : DeviceManager {

    private var car: Car? = null
    private var carPropertyManager: CarPropertyManager? = null
    private var carApiAvailable = false

    val deviceDataParser by lazy {
        deviceDataJson?.let {
            DeviceDataParser(
                jsonString = deviceDataJson,
                deviceType = deviceType,
            )
        }
    }

    init {
        try {
            createDevice()
            carPropertyManager = car?.getCarManager(Car.PROPERTY_SERVICE) as CarPropertyManager
        } catch (e: Exception) {
            Timber.e(e.toString())
        }
    }

    /**
     * Converts Timestamp to ms
     *
     * @return Long
     */
    fun convertTimestampAsMs(value: Long): Long {
        return (value / (1000L * 1000L))
    }

    /**
     * Checks if the Android Car API is available on the device.
     *
     * @return True if the Car API is available, false otherwise.
     */
    override fun isDeviceAvailable(): Boolean {
        if (carApiAvailable) return true
        return try {
            Class.forName("android.car.Car")
            carApiAvailable = true
            true
        } catch (e: ClassNotFoundException) {
            Timber.e("Car API is not available.")
            false
        }
    }

    /**
     * Creates a new Car instance if the current car is null or not connected.
     *
     * @return A new Car instance.
     */
    override fun createDevice() {
        if (isDeviceAvailable()) {
            car?.disconnect()
            car = Car.createCar(context)
        } else return
    }

    /**
     * Releases the car connection.
     *
     * Disconnects the car, if it's connected, and sets the car reference to null.
     */
    override fun release() {
        car?.disconnect()
        car = null
    }

    fun registerPropertyCallBack(
        propertyId: Int,
        callback: CarPropertyManager.CarPropertyEventCallback,
        sensorRate: Float = SENSOR_RATE_ONCHANGE
    ) {
        carPropertyManager?.registerCallback(callback, propertyId, sensorRate)
    }

    fun unRegisterPropertyCallBack(
        propertyId: Int,
        callback: CarPropertyManager.CarPropertyEventCallback
    ) {
        carPropertyManager?.unregisterCallback(callback, propertyId)
    }

    /**
     * Returns a list of all available property IDs.
     *
     * @return A list of property IDs.
     */
    fun getAllAvailableProperties() = carPropertyManager?.propertyList?.map {
        it.propertyId
    } ?: listOf()

    /**
     * Creates a callback flow that emits the specified vehicle property.
     *
     * @param vehicleProperty The vehicle property to observe, including its ID and name.
     * @param sensorRate The rate at which sensor events should be reported.
     * @return A callback flow that emits the vehicle property value as the specified type, optionally grouped by area.
     */
    private inline fun <reified T> observeVehicleProperty(
        vehicleProperty: NCarPropertyId,
        sensorRate: Float,
    ): Flow<DeviceProperty<T?>> = callbackFlow {
        if (isDeviceAvailable()) {
            val callback = object : CarPropertyManager.CarPropertyEventCallback {
                override fun onChangeEvent(event: CarPropertyValue<*>?) {
                    Timber.d("Vehicle property changed: ${event?.propertyId} - ${event?.value}")
                    event?.value?.let { value ->
                        try {
                            val castedValue = value as? T?
                            // Handle case where T is a List
                            val property = DeviceProperty<T?>(
                                timestamp = convertTimestampAsMs(event.timestamp),
                                id = event.propertyId,
                                name = vehicleProperty.property,
                                value = castedValue,
                                areaId = event.areaId
                            )

                            trySend(property)

                        } catch (e: ClassCastException) {
                            Timber.e(
                                e,
                                "Failed to cast value: $value for ${vehicleProperty.property}"
                            )
                        }
                    }
                }

                override fun onErrorEvent(errorCode: Int, propertyId: Int) {
                    Timber.e("Error in getting ${vehicleProperty.property} with error code $errorCode")
                }
            }

            registerPropertyCallBack(
                propertyId = vehicleProperty.id,
                callback = callback,
                sensorRate = sensorRate
            )

            awaitClose {
                if (isDeviceAvailable()) {
                    unRegisterPropertyCallBack(propertyId = vehicleProperty.id, callback = callback)
                }
            }
        } else {
            awaitClose { cancel() }
        }
    }

    private inline fun <reified T> observeWithAreaVehicleProperty(
        vehicleProperty: NCarPropertyId,
        sensorRate: Float,
    ): Flow<List<DeviceProperty<T?>>> = callbackFlow {
        if (isDeviceAvailable()) {
            val properties = mutableMapOf<Int, DeviceProperty<T?>>()
            val callback = object : CarPropertyManager.CarPropertyEventCallback {
                override fun onChangeEvent(event: CarPropertyValue<*>?) {
                    event?.value?.let { value ->
                        try {
                            val castedValue = value as? T?
                            val areaId = event.areaId
                            // Handle case where T is a List
                            val property = DeviceProperty<T?>(
                                timestamp = convertTimestampAsMs(event.timestamp),
                                id = event.propertyId,
                                name = vehicleProperty.property,
                                value = castedValue,
                                areaId = event.areaId
                            )
                            properties[areaId] = property

                            trySend(properties.values.toList())

                        } catch (e: ClassCastException) {
                            Timber.e(
                                t = e,
                                message = "Failed to cast value: $value for ${vehicleProperty.property}"
                            )
                        }
                    }
                }

                override fun onErrorEvent(errorCode: Int, propertyId: Int) {
                    Timber.e("Error in getting ${vehicleProperty.property} with error code $errorCode")
                }
            }

            registerPropertyCallBack(
                propertyId = vehicleProperty.id,
                callback = callback,
                sensorRate = sensorRate
            )

            awaitClose {
                if (isDeviceAvailable()) {
                    unRegisterPropertyCallBack(propertyId = vehicleProperty.id, callback = callback)
                }
            }
        } else {
            awaitClose { cancel() }
        }
    }

    private inline fun <reified T> observeVehicleProperty(
        vehiclePropertyName: String,
        vehiclePropertyId: Int,
        sensorRate: Float,
    ): Flow<DeviceProperty<T?>> = callbackFlow {
        if (isDeviceAvailable()) {
            val callback = object : CarPropertyManager.CarPropertyEventCallback {
                override fun onChangeEvent(event: CarPropertyValue<*>?) {
                    Timber.d("Vehicle property changed: ${event?.propertyId} - ${event?.value}")
                    event?.value?.let { value ->
                        try {
                            val castedValue = value as? T?
                            // Handle case where T is a List
                            val property = DeviceProperty<T?>(
                                timestamp = convertTimestampAsMs(event.timestamp),
                                id = event.propertyId,
                                name = vehiclePropertyName,
                                value = castedValue,
                                areaId = event.areaId
                            )

                            trySend(property)

                        } catch (e: ClassCastException) {
                            Timber.e(
                                e,
                                "Failed to cast value: $value for $vehiclePropertyId"
                            )
                        }
                    }
                }

                override fun onErrorEvent(errorCode: Int, propertyId: Int) {
                    Timber.e("Error in getting $vehiclePropertyId with error code $errorCode")
                }
            }

            registerPropertyCallBack(
                propertyId = vehiclePropertyId,
                callback = callback,
                sensorRate = sensorRate
            )

            awaitClose {
                if (isDeviceAvailable()) {
                    unRegisterPropertyCallBack(propertyId = vehiclePropertyId, callback = callback)
                }
            }
        } else {
            awaitClose { cancel() }
        }
    }

    private inline fun <reified T> observeWithAreaVehicleProperty(
        vehiclePropertyName: String,
        vehiclePropertyId: Int,
        sensorRate: Float,
    ): Flow<List<DeviceProperty<T?>>> = callbackFlow {
        if (isDeviceAvailable()) {
            val properties = mutableMapOf<Int, DeviceProperty<T?>>()
            val callback = object : CarPropertyManager.CarPropertyEventCallback {
                override fun onChangeEvent(event: CarPropertyValue<*>?) {
                    event?.value?.let { value ->
                        try {
                            val castedValue = value as? T?
                            val areaId = event.areaId
                            // Handle case where T is a List
                            val property = DeviceProperty<T?>(
                                timestamp = convertTimestampAsMs(event.timestamp),
                                id = event.propertyId,
                                name = vehiclePropertyName,
                                value = castedValue,
                                areaId = event.areaId
                            )
                            properties[areaId] = property

                            trySend(properties.values.toList())

                        } catch (e: ClassCastException) {
                            Timber.e(
                                t = e,
                                message = "Failed to cast value: $value for $vehiclePropertyId"
                            )
                        }
                    }
                }

                override fun onErrorEvent(errorCode: Int, propertyId: Int) {
                    Timber.e("Error in getting ${vehiclePropertyId} with error code $errorCode")
                }
            }

            registerPropertyCallBack(
                propertyId = vehiclePropertyId,
                callback = callback,
                sensorRate = sensorRate
            )

            awaitClose {
                if (isDeviceAvailable()) {
                    unRegisterPropertyCallBack(propertyId = vehiclePropertyId, callback = callback)
                }
            }
        } else {
            awaitClose { cancel() }
        }
    }


    /**
     * Callback flow that fetches all available vehicle properties.
     *
     * @return A callback flow emitting a list of [DeviceProperty] objects.
     */
    override fun fetchAllProperties() = callbackFlow {
        if (isDeviceAvailable()) {
            val properties = mutableListOf<DeviceProperty<String>>()
            val callback = object : CarPropertyManager.CarPropertyEventCallback {
                override fun onChangeEvent(event: CarPropertyValue<*>?) {

                    val value = if (event?.value is Array<*>) {
                        (event.value as Array<*>).joinToString(
                            prefix = "[",
                            postfix = "]",
                            separator = ", "
                        )
                    } else event?.value.toString()
                    event?.let {
                        val index = properties.indexOfFirst { it.id == event.propertyId }
                        if (index != -1) {
                            // Update existing property
                            properties[index] = DeviceProperty(
                                timestamp = event.timestamp,
                                id = event.propertyId,
                                name = event.propertyId.vehiclePropertyById()?.property
                                    ?: "${it.propertyId}",
                                value = value,
                                areaId = event.areaId
                            )
                        } else {
                            // Add new property
                            properties.add(
                                DeviceProperty(
                                    timestamp = event.timestamp,
                                    id = event.propertyId,
                                    name = event.propertyId.vehiclePropertyById()?.property
                                        ?: "${it.propertyId}",
                                    value = value,
                                    areaId = event.areaId
                                )
                            )
                        }
                    }
                    trySend(properties.toList()) // Send a copy of the list to avoid concurrency issues
                }

                override fun onErrorEvent(p0: Int, p1: Int) {
                    Timber.e("Error in getting vehicle property $p0 $p1")
                }
            }

            carPropertyManager?.propertyList?.forEach {
                try {
                    carPropertyManager?.registerCallback(
                        callback,
                        it.propertyId,
                        SENSOR_RATE_ONCHANGE,
                    )
                } catch (ex: Exception) {
                    Timber.e(ex)
                }
            }
            awaitClose {
                release()
                carPropertyManager?.propertyList?.forEach { properties ->
                    carPropertyManager?.unregisterCallback(
                        callback,
                        properties.propertyId
                    )
                }
            }
        } else {
            awaitClose {
                close()
            }
        }
    }

    /**
     * Retrieves the Vehicle Identification Number (VIN) of the vehicle.
     *
     * @return The VIN of the vehicle, or null if it is not available.
     */
    override fun deviceIdentifier(): String? {
        return try {
            carPropertyManager?.getProperty<String>(NCarPropertyId.INFO_VIN.id, 0)?.value
        } catch (e: Exception) {
            Timber.tag("NevaCarManager").e("Failed to get VIN number: $e")
            null
        }
    }

    override fun getDeviceInfo(): Device {
        return Device(
            identification = deviceIdentifier() ?: "",
            model = getCustomProperty<String>(
                propertyId = NCarPropertyId.INFO_MODEL.id,
                areaId = 0
            ).value,
            brand = getCustomProperty<String>(
                propertyId = NCarPropertyId.INFO_MAKE.id,
                areaId = 0
            ).value,
            title = getCustomProperty<String>(
                propertyId = NCarPropertyId.INFO_MAKE.id,
                areaId = 0
            ).value + " " + getCustomProperty<String>(
                propertyId = NCarPropertyId.INFO_MODEL.id,
                areaId = 0
            ).value
        )
    }

    /**
     * Creates a callback flow that emits the vehicle speed.
     *
     * @return A callback flow that emits the vehicle speed in kilometers per hour.
     */
    override fun speed() = observeVehicleProperty<Float>(
        NCarPropertyId.PERF_VEHICLE_SPEED, // Ensure this is defined in your enum
        SENSOR_RATE_ONCHANGE,
    )

    override fun formattedSpeed() = speed().map { speedEvent ->
        deviceDataParser?.getSpeedData(speedEvent)
    }

    override fun gearState(sensorRate: Float) = observeVehicleProperty<Int>(
        vehicleProperty = NCarPropertyId.GEAR_SELECTION,
        sensorRate = sensorRate,
    )

    override fun formattedGearState(sensorRate: Float): Flow<List<DataPoint<String?>>> {
        return gearState(sensorRate).mapNotNull { gearEvent ->
            deviceDataParser?.getGearData(gearEvent)
        }
    }

    override fun getFormattedGearState(): List<DataPoint<String?>>? {
        val property = getCustomProperty<Int>(NCarPropertyId.GEAR_SELECTION.id, 0)
        return deviceDataParser?.getGearData(property)
    }

    /**
     * Retrieves the current vehicle mode (e.g. "Drive", "Park", etc.).
     *
     * @return A callback flow emitting the current vehicle mode, or null if it is not available.
     */
    override fun drivingMode() = observeVehicleProperty<Int>(
        vehicleProperty = NCarPropertyId.VENDOR_DRIVE_MODE_PROPERTY, // Ensure this is defined in your enum
        sensorRate = SENSOR_RATE_ONCHANGE,
    )

    override fun formattedDrivingMode() = drivingMode().map {
        deviceDataParser?.getDrivingModeData(it)
    }

    override fun evChargingRate() = observeVehicleProperty<Float>(
        NCarPropertyId.EV_BATTERY_INSTANTANEOUS_CHARGE_RATE,
        SENSOR_RATE_ONCHANGE,
    )

    override fun formattedEvChargingRate(): Flow<List<DataPoint<Float>>> =
        evChargingRate().mapNotNull { deviceDataParser?.evChargingRateData(it) }

    override fun throttle(): Flow<DeviceProperty<Float?>> = evChargingRate().filter {
        it.value != null
    }.map {
        if (it.value!! < 0) {
            it.copy(value = it.value.absoluteValue)
        } else it.copy(value = 0f)
    }

    override fun formattedThrottle(): Flow<List<DataPoint<Float>>> = throttle().mapNotNull {
        deviceDataParser?.throttleData(it)
    }


    override fun brake(): Flow<DeviceProperty<Float?>> = evChargingRate().filter {
        it.value != null
    }.map {
        if (it.value!! >= 0) {
            it.copy(value = it.value)
        } else it.copy(value = 0f)
    }

    override fun formattedBrake(): Flow<List<DataPoint<Float>>> = brake().mapNotNull {
        deviceDataParser?.breakData(it)
    }

// region HvacProperties
    /**
     * Retrieves the current state of the air conditioning (AC) system in the vehicle (on/off).
     *
     * @return A callback flow emitting the current state of the AC system, or null if it is not available.
     */
    override fun hvacAcStatus() = observeVehicleProperty<Boolean>(
        NCarPropertyId.HVAC_AC_ON, // Ensure this is defined in your enum
        SENSOR_RATE_ONCHANGE,
    )

    override fun formattedAcStatus(): Flow<List<DataPoint<Boolean>>> = hvacAcStatus().mapNotNull {
        deviceDataParser?.hvacStatusData(it)
    }

    override fun hvacDualStatus() = observeVehicleProperty<Boolean>(
        NCarPropertyId.HVAC_DUAL_ON, // Ensure this is defined in your enum
        SENSOR_RATE_ONCHANGE,
    )

    override fun formattedHvacDualStatus(): Flow<List<DataPoint<Boolean>>> {
        return hvacDualStatus().mapNotNull {
            deviceDataParser?.hvacDualStatusData(it)
        }
    }

    override fun hvacMaxStatus() = observeVehicleProperty<Boolean>(
        NCarPropertyId.HVAC_MAX_AC_ON, // Ensure this is defined in your enum
        SENSOR_RATE_ONCHANGE,
    )

    override fun formattedHvacMaxStatus(): Flow<List<DataPoint<Boolean>>> {
        return hvacMaxStatus().mapNotNull {
            deviceDataParser?.hvacMaxData(it)
        }
    }


    /**
     * Retrieves the current fan speed of the HVAC system in the vehicle.
     *
     * @return A callback flow emitting the current fan speed.
     */
    override fun hvacSpeed() = observeVehicleProperty<Int>(
        NCarPropertyId.HVAC_FAN_SPEED,
        SENSOR_RATE_ONCHANGE,
    )

    override fun formattedHvacSpeed(): Flow<List<DataPoint<Int>>> {
        return hvacSpeed().mapNotNull {
            deviceDataParser?.hvacFanSpeedData(it)
        }
    }

    override fun hvacPassengerSpeed(): Flow<DeviceProperty<Int?>> = observeVehicleProperty<Int>(
        NCarPropertyId.HVAC_FAN_SPEED,
        SENSOR_RATE_ONCHANGE,
    )

    override fun formattedHvacPassengerSpeed(): Flow<List<DataPoint<Int>>> {
        return hvacPassengerSpeed().mapNotNull {
            deviceDataParser?.hvacPassengerFanSpeedData(it)
        }
    }

    /**
     * Retrieves the current set temperature of the HVAC system for each area (driver, passenger).
     *
     * @return A callback flow emitting an array of temperatures.
     */
    override fun hvacTemperature() = observeWithAreaVehicleProperty<Float>(
        NCarPropertyId.HVAC_TEMPERATURE_SET, // Ensure this is defined in your enum
        SENSOR_RATE_ONCHANGE,
    )

    override fun formattedHvacTemperature(): Flow<List<List<DataPoint<Float>>>> {
        return hvacTemperature().mapNotNull {
            deviceDataParser?.hvacTemperatureData(it) // We need to send this data with area ID
        }
    }

    override fun hvacInteriorTemperature(): Flow<DeviceProperty<Float?>> {
        val propertyId = deviceDataParser?.device?.features?.find {
            it.key == "SENSOR:HVAC_INTERIOR_TEMPERATURE:RAW"
        }?.readId
        val readMethod = deviceDataParser?.device?.features?.find {
            it.key == "SENSOR:HVAC_INTERIOR_TEMPERATURE:RAW"
        }?.readMethod?.uppercase()
        return if (propertyId?.toIntOrNull() != null && readMethod == "VHAL") {
            observeVehicleProperty(
                vehiclePropertyName = "HVAC Interior Temperature",
                vehiclePropertyId = propertyId.toInt(),
                sensorRate = SENSOR_RATE_ONCHANGE
            )
        } else flowOf()
    }

    override fun formattedHvacInteriorTemperature(): Flow<List<DataPoint<Float>>> {
        return hvacInteriorTemperature().mapNotNull {
            deviceDataParser?.hvacInteriorTemperatureData(it)
        }
    }

    override fun hvacExteriorTemperature(): Flow<DeviceProperty<Float?>> {
        return observeVehicleProperty<Float>(
            NCarPropertyId.ENV_OUTSIDE_TEMPERATURE, // Ensure this is defined in your enum
            SENSOR_RATE_ONCHANGE,
        )
    }

    override fun formattedHvacExteriorTemperature(): Flow<List<DataPoint<Float>>> {
        return hvacExteriorTemperature().mapNotNull {
            deviceDataParser?.hvacExteriorTemperatureData(it)
        }
    }

    override fun batteryCapacity(): Flow<DeviceProperty<Float?>> = observeVehicleProperty<Float>(
        NCarPropertyId.INFO_EV_BATTERY_CAPACITY,
        SENSOR_RATE_ONCHANGE,
    )

    override fun formattedBatteryCapacity(): Flow<List<DataPoint<Float>>> {
        return batteryCapacity().mapNotNull {
            deviceDataParser?.batteryCapacityData(it)
        }
    }

    // endregion HvacProperties
    override fun battery(): Flow<DeviceProperty<Float?>> = observeVehicleProperty<Float>(
        NCarPropertyId.EV_BATTERY_LEVEL,
        SENSOR_RATE_ONCHANGE,
    )


    override fun formattedBatteryData(): Flow<List<DataPoint<Float>>> {
        return battery().mapNotNull {
            val maxBattery =
                getCustomProperty<Float>(NCarPropertyId.INFO_EV_BATTERY_CAPACITY.id, 0)
            maxBattery.value?.let { maxBatteryLevel ->
                deviceDataParser?.batteryData(maxBatteryLevel, it)
            }
        }
    }

    override fun engine(): Flow<DeviceProperty<Float?>> = observeVehicleProperty<Float>(
        NCarPropertyId.ENGINE_RPM,
        SENSOR_RATE_ONCHANGE,
    )

    override fun formattedEngine(): Flow<List<DataPoint<Float>>> {
        return engine().mapNotNull {
            deviceDataParser?.engineData(it)
        }
    }

    override fun acceleration(): Flow<DeviceProperty<Float?>> {
        return flowOf(
            DeviceProperty<Float?>(
                timestamp = System.currentTimeMillis(),
                id = 0,
                name = "Acceleration",
                value = 0f,
                areaId = 0
            )
        )
    }

    override fun formattedAcceleration(): Flow<List<DataPoint<Float>>> = acceleration().mapNotNull {
        deviceDataParser?.accelerationData(it)
    }

    override fun seatOccupancy(): Flow<DeviceProperty<Int?>> = observeVehicleProperty<Int>(
        NCarPropertyId.SEAT_OCCUPANCY,
        SENSOR_RATE_ONCHANGE,
    )

    override fun formattedSeatOccupancyData(): Flow<List<DataPoint<Int>>> {
        return seatOccupancy().mapNotNull {
            deviceDataParser?.seatOccupancyData(it)
        }
    }

    override fun direction(): Flow<DeviceProperty<GyroscopeData?>> {
        return context.getGyroscopeFlow().mapNotNull { event ->
            val (x, y, z) = event.values
            DeviceProperty<GyroscopeData?>(
                timestamp = event.timestamp,
                id = 0,
                name = "Direction",
                value = GyroscopeData(x, y, z),
                areaId = 0
            )
        }
    }

    override fun formattedDirection(): Flow<List<DataPoint<Float>>> {
        return direction().mapNotNull {
            deviceDataParser?.directionData(it)
        }
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    override fun location(updateInterval: Duration): Flow<LocationState> {
        return context.getLocationFlow(updateInterval)
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    override fun formattedLocation(): Flow<DataPoint<List<Float>>?> {
        return location().map {
            deviceDataParser?.locationData(it)
        }
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    override fun altitude(): Flow<Float> {
        return location().map { location ->
            location.altitude.toFloat()
        }
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    override fun formattedAltitude(): Flow<DataPoint<Float>?> {
        return altitude().map {
            deviceDataParser?.altitudeData(it)
        }
    }

    override fun steeringWheelAngleData(): Flow<DeviceProperty<Float?>> {
        return flowOf(
            DeviceProperty<Float?>(
                timestamp = System.currentTimeMillis(),
                id = 0,
                name = "Steering WheelAngle",
                value = 0f,
                areaId = 0
            )
        )
    }

    override fun formattedSteeringWheelAngleData(): Flow<List<DataPoint<Float>>> {
        return steeringWheelAngleData().mapNotNull {
            deviceDataParser?.steeringWheelAngleData(it)
        }
    }

    override fun doorState(): Flow<List<DeviceProperty<Int?>>> {
        return observeWithAreaVehicleProperty<Int>(
            NCarPropertyId.DOOR_POS,
            SENSOR_RATE_ONCHANGE,
        )
    }

    override fun formattedDoorState(): Flow<List<DataPoint<List<Boolean>>>> {
        return doorState().mapNotNull {
            deviceDataParser?.doorsStateData(it)
        }
    }

    override fun trunkState(): Flow<List<DeviceProperty<Int?>>> {
        return observeWithAreaVehicleProperty<Int>(
            NCarPropertyId.DOOR_POS,
            SENSOR_RATE_ONCHANGE,
        )
    }

    override fun formattedTrunkState(): Flow<List<DataPoint<Boolean>>> {
        val trunkKey = "SENSOR:TRUNK_STATE:RAW"
        val trunkConfigs =
            deviceDataParser?.device?.features?.first { it.key.contains("TRUNK_STATE") }?.values
        val trunkAreaId = trunkConfigs?.find {
            it.name == "AREA_ID"
        }?.value?.jsonPrimitive?.intOrNull

        return trunkState().map { it.filter { it.areaId == trunkAreaId } }.mapNotNull { doors ->
            doors.firstOrNull()?.let {
                deviceDataParser?.trunkStateData(it)
            }
        }
    }

    override fun frunkState(): Flow<List<DeviceProperty<Int?>>> {
        return observeWithAreaVehicleProperty<Int>(
            NCarPropertyId.DOOR_POS,
            SENSOR_RATE_ONCHANGE,
        )
    }

    override fun formattedFrunkState(): Flow<List<DataPoint<Boolean>>> {
        val frunkKey = "SENSOR:FRUNK_STATE:RAW"
        val frunkConfigs = deviceDataParser?.device?.features?.first { it.key.contains("FRUNK_STATE") }?.values
        val frunkAreaId = frunkConfigs?.find {
            it.name == "AREA_ID"
        }?.value?.jsonPrimitive?.intOrNull

        return trunkState().map { it.filter { it.areaId == frunkAreaId } }.mapNotNull { doors ->
            doors.firstOrNull()?.let {
                deviceDataParser?.frunkStateData(it)
            }
        }
    }

    override fun formattedTrunkAngle(): Flow<List<DataPoint<Float>>> {
        val frunkKey = "SENSOR:FRUNK_STATE:RAW"
        val frunkConfigs = deviceDataParser?.device?.features?.first { it.key == frunkKey }?.values
        val frunkAreaId = frunkConfigs?.find {
            it.name == "AREA_ID"
        }?.value?.jsonPrimitive?.intOrNull

        return trunkState().map { it.filter { it.areaId == frunkAreaId } }.mapNotNull { doors ->
            doors.firstOrNull()?.let {
                val deviceProperty = DeviceProperty<Float?>(
                    timestamp = it.timestamp,
                    id = it.id,
                    name = it.name,
                    value = 0f,
                    areaId = it.areaId
                )
                if (it.value == 1) {
                    deviceDataParser?.trunkOpenAngleData(deviceProperty.copy(value = 180f))
                } else {
                    deviceDataParser?.trunkOpenAngleData(deviceProperty)
                }
            }
        }
    }

    override fun formattedFrunkAngle(): Flow<List<DataPoint<Float>>> {
        val frunkKey = "SENSOR:FRUNK_STATE:RAW"
        val frunkConfigs = deviceDataParser?.device?.features?.first { it.key == frunkKey }?.values
        val frunkAreaId = frunkConfigs?.find {
            it.name == "AREA_ID"
        }?.value?.jsonPrimitive?.intOrNull

        return trunkState().map { it.filter { it.areaId == frunkAreaId } }.mapNotNull { doors ->
            doors.firstOrNull()?.let {
                val deviceProperty = DeviceProperty<Float?>(
                    timestamp = it.timestamp,
                    id = it.id,
                    name = it.name,
                    value = 0f,
                    areaId = it.areaId
                )
                if (it.value == 1) {
                    deviceDataParser?.frunkOpenAngleData(deviceProperty.copy(value = 180f))
                } else {
                    deviceDataParser?.frunkOpenAngleData(deviceProperty)
                }
            }
        }
    }

    override fun windowsState(): Flow<List<DeviceProperty<Int?>>> {
        return observeWithAreaVehicleProperty<Int>(
            NCarPropertyId.WINDOW_POS,
            SENSOR_RATE_ONCHANGE,
        )
    }

    override fun formattedWindowsState(): Flow<List<DataPoint<List<Int>>>> {
        return windowsState().mapNotNull {
            deviceDataParser?.windowsStateData(it)
        }
    }

    override fun formattedAllData(): Flow<List<DataPoint<out Any?>>?> {
        return merge(
            formattedSpeed(),
            formattedGearState(),
            formattedDrivingMode(),
            formattedEvChargingRate(),
            formattedThrottle(),
            formattedBrake(),
            formattedAcStatus(),
            formattedHvacDualStatus(),
            formattedHvacMaxStatus(),
            formattedHvacSpeed(),
            formattedHvacPassengerSpeed(),
            formattedHvacInteriorTemperature(),
            formattedHvacExteriorTemperature(),
            formattedBatteryCapacity(),
            formattedBatteryData(),
            formattedEngine(),
            formattedAcceleration(),
            formattedSeatOccupancyData(),
            formattedDirection(),
            formattedSteeringWheelAngleData(),
            formattedDoorState(),
            formattedTrunkState(),
            formattedFrunkState(),
            formattedTrunkAngle(),
            formattedFrunkAngle(),
            formattedWindowsState()
        )
    }


    //Custom Properties
    /**
     * Sets a custom integer property for a specific area in the vehicle.
     *
     * @param propertyId The unique identifier for the property to be set.
     * @param areaId The area of the vehicle where the property is to be set.
     * @param value The integer value to set for the specified property.
     */
    override fun setCustomIntProperty(propertyId: Int, areaId: Int, value: Int) {
        // Set custom property
        try {
            carPropertyManager?.setIntProperty(propertyId, areaId, value)
        } catch (e: Exception) {
            Timber.tag("NevaCarManager").e("Failed to set ambient light color: $e")
        }
    }

    /**
     * Retrieves a custom property for a specific area in the vehicle
     * @param propertyId The unique identifier for the property to be retrieved.
     * @param areaId The area of the vehicle where the property is to be retrieved.
     * @return The value of the specified property, or null if it is not available.
     */
    override fun <T> getCustomProperty(propertyId: Int, areaId: Int): DeviceProperty<T?> {
        val property = carPropertyManager?.getProperty<T>(propertyId, areaId)
        return DeviceProperty(
            timestamp = Calendar.getInstance().timeInMillis,
            id = propertyId,
            name = propertyId.vehiclePropertyById()?.property ?: "$propertyId",
            value = property?.value,
            areaId = areaId
        )
    }


    /**
     * Callback flow that fetches the ambient light color.
     *
     * @return A callback flow emitting the ambient light color.
     */
    override fun ambientLightState() = callbackFlow {
        if (isDeviceAvailable()) {
            val callback = object : CarPropertyManager.CarPropertyEventCallback {
                override fun onChangeEvent(event: CarPropertyValue<*>?) {
                    Timber.d("Ambient light changed: ${event?.value}")
                    val colorValue = event?.value as? Int?
                    trySend(
                        DeviceProperty(
                            timestamp = event?.timestamp ?: 0,
                            id = event?.propertyId ?: 0,
                            name = NCarPropertyId.VENDOR_AMBIENT_LIGHT_READ.property,
                            value = colorValue,
                            areaId = event?.areaId ?: 0
                        )
                    )
                }

                override fun onErrorEvent(p0: Int, p1: Int) {
                    Timber.e("Error in getting ambient light $p0 $p1")
                }
            }
            carPropertyManager?.registerCallback(
                callback,
                NCarPropertyId.VENDOR_AMBIENT_LIGHT_READ.id,
                SENSOR_RATE_ONCHANGE
            )
            awaitClose {
                if (isDeviceAvailable()) {
                    carPropertyManager?.unregisterCallback(callback)
                    release()
                }
            }
        } else {
            awaitClose {
                cancel()
            }
        }
    }

    override fun ambientLight(): DeviceProperty<Int>? {
        val property = carPropertyManager?.getProperty<Int>(
            NCarPropertyId.VENDOR_AMBIENT_LIGHT_READ.id,
            0
        )

        return property?.let {
            DeviceProperty(
                timestamp = it.timestamp,
                id = property.propertyId,
                name = NCarPropertyId.VENDOR_AMBIENT_LIGHT_READ.property,
                value = it.value,
                areaId = it.areaId
            )
        }
    }

    /**
     * Sets the ambient light color.
     *
     * @param ambientColors
     */
    override fun setAmbientLight(ambientColors: AmbientColor) {
        if (isDeviceAvailable()) {
            try {
                carPropertyManager?.setIntProperty(
                    NCarPropertyId.VENDOR_AMBIENT_LIGHT_WRITE.id,
                    0,
                    ambientColors.order
                )
            } catch (e: Exception) {
                Timber.tag("NevaCarManager").e("Failed to set ambient light color: $e")
            }
        }
    }

    /**
     * Sets the ambient light color.
     *
     * @param colorNumber The color number to set.
     */
    fun setAmbientLight(colorNumber: Int) {
        if (isDeviceAvailable()) {
            try {
                carPropertyManager?.setIntProperty(
                    NCarPropertyId.VENDOR_AMBIENT_LIGHT_WRITE.id,
                    0,
                    colorNumber
                )
            } catch (e: Exception) {
                Timber.tag("NevaCarManager").e("Failed to set ambient light color: $e")
            }
        }
    }
}

data class AmbientColor(val hex: String, val order: Int)

val ambientColors = ConstantAmbientColor.entries.map { it.ambientColors }.toList()

private fun hexToRgb(hex: String): Triple<Int, Int, Int> {
    val colorInt = hex.removePrefix("#").toInt(16)
    val r = (colorInt shr 16) and 0xFF
    val g = (colorInt shr 8) and 0xFF
    val b = colorInt and 0xFF
    return Triple(r, g, b)
}

private fun colorDistance(c1: Triple<Int, Int, Int>, c2: Triple<Int, Int, Int>): Double {
    val (r1, g1, b1) = c1
    val (r2, g2, b2) = c2
    return sqrt(
        (r1 - r2).toDouble().pow(2.0) + (g1 - g2).toDouble().pow(2.0) + (b1 - b2).toDouble()
            .pow(2.0)
    )
}

fun findNearestColor(hex: String): AmbientColor {
    try {
        val targetColor = hexToRgb(hex)
        return ambientColors.minByOrNull { color ->
            colorDistance(targetColor, hexToRgb(color.hex))
        } ?: ambientColors.first()
    } catch (e: Exception) {
        Timber.e(e)
        return ConstantAmbientColor.NO_COLOR.ambientColors
    }
}

enum class ConstantAmbientColor(val ambientColors: AmbientColor) {
    NO_COLOR(AmbientColor("#000000", 0)),
    TURQUOISE(AmbientColor("#40E0D0", 1)),
    ORANGE(AmbientColor("#FFA500", 2)),
    YELLOW(AmbientColor("#FFFF00", 3)),
    PURPLE(AmbientColor("#800080", 4)),
    RED(AmbientColor("#FF0000", 5)),
    BLUE(AmbientColor("#0000FF", 6)),
    GREEN(AmbientColor("#008000", 7)),
    WHITE(AmbientColor("#FFFFFF", 8))
}

data class Device(
    val identification: String,
    val model: String?,
    val brand: String?,
    val title: String?,
)

enum class VehicleType {
    TOGG
}