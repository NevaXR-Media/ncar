package com.nevaxr.device.deviceData

import com.nevaxr.device.DeviceProperty
import com.nevaxr.device.GyroscopeData
import com.nevaxr.device.LocationState
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonPrimitive
import timber.log.Timber
import java.util.UUID
import kotlin.math.absoluteValue

@OptIn(ExperimentalSerializationApi::class)
class DeviceDataParser(
    private val deviceType: DeviceType,
    jsonString: String = DEFAULT_DEVICE_DATA,
) {

    private var featureConfig: DeviceConfig? = null
    private var featureMap: Map<String, Feature>? = null

    var device: Device? = null

    init {
        convertJsonToFeature(jsonString)
    }

    private fun convertJsonToFeature(jsonString: String) {
        val json = Json {
            ignoreUnknownKeys = true
            allowTrailingComma = true
        }
        val featureConfig = json.decodeFromString<DeviceConfig>(jsonString)
        this.featureConfig = featureConfig
        featureMap = buildFeatureMap(featureConfig)
        device = featureConfig.devices.find { it.key.contains(deviceType.name) }
    }

    /**
     * Build a map from key -> Feature definition for fast lookups.
     */
    private fun buildFeatureMap(config: DeviceConfig): Map<String, Feature> {
        val map = mutableMapOf<String, Feature>()
        for (feat in config.features) {
            map[feat.key] = feat
        }
        for (dev in config.devices) {
            for (feat in dev.features) {
                map[feat.key] = feat
            }
        }
        return map
    }

    private fun convertRawSpeed(
        value: Float,
        rawFeature: Feature?,
        feature: Feature?
    ): Float {
        val deviceUnit = device?.features?.find { it.name.contentEquals("Speed") }?.unit ?: ""
        val deviceRange = device?.features?.find { it.name.contentEquals("Speed") }?.range?.takeIf { it.size == 2 }?.let { it[0] to it[1] }

        val range = feature?.range?.takeIf { it.size == 2 }?.let { it[0] to it[1] }

        val rawUnit = rawFeature?.unit?.lowercase() ?: ""
        val unit = feature?.unit?.lowercase() ?: ""


        var convertedValue = if (unit.isNotEmpty()) {
            val deviceValueToRawValue = convertUnit(value, deviceUnit, rawUnit)
            convertUnit(deviceValueToRawValue, rawUnit, unit)
        } else value

        return if (deviceRange != null && range != null) {
            changeValueRange(
                convertedValue,
                oldMin = deviceRange.first.toFloat(),
                oldMax = deviceRange.second.toFloat(),
                newMin = range.first.toFloat(),
                newMax = range.second.toFloat()
            )
        } else {
            convertedValue
        }
    }


    private fun <T> buildSpeedDataPoint(
        sensorData: DeviceProperty<T>,
        sensorKey: String,
    ): DataPoint<Float> {
        val feature = featureMap?.get(sensorKey)
        val splitSensorKey = sensorKey.split(":")
        val rawFeature = featureMap?.get(splitSensorKey[0] + ":" + splitSensorKey[1] + ":RAW")

        val range = feature?.range?.takeIf { it.size == 2 }?.let { (min, max) ->
            min.toFloat()..max.toFloat()
        }
        var convertedRange = range

        val numValue = (sensorData.value as Number).toFloat()
        val scaledOrRawValue = when {

            sensorKey.contains("RAW", ignoreCase = true) -> {
                val unit = feature?.unit?.lowercase() ?: ""
                val deviceUnit = device?.features?.find {
                    it.name.contentEquals("Speed")
                }?.unit ?: ""
                range?.endInclusive?.let {
                    val maxRange = convertUnit(it, deviceUnit, unit)
                    convertedRange = 0f..maxRange
                }
                convertUnit(numValue, deviceUnit, unit)
            }

            !sensorKey.contains("RAW", ignoreCase = true) -> {
                convertRawSpeed(numValue, rawFeature, feature)
            }

            else -> numValue
        }

        return DataPoint(
            key = sensorKey,
            value = scaledOrRawValue.absoluteValue,
            timestamp = sensorData.timestamp,
            range = convertedRange
        )
    }

    private fun <T> buildGearStateDataPoint(
        sensorData: DeviceProperty<T>,
        sensorKey: String,
    ): DataPoint<String?> {
        val valueAsJson = sensorData.value as Number
        val value = device?.features?.find { it.key.contains("GEAR") }?.values?.find {
            it.value?.jsonPrimitive?.intOrNull == valueAsJson
        }?.name
        Timber.d("Building Gear State data point for valueAsJson: $valueAsJson")
        return DataPoint(
            key = sensorKey,
            value = value,
            timestamp = sensorData.timestamp,
        )
    }


    private fun <T> buildDrivingModeDataPoint(
        sensorData: DeviceProperty<T>,
        sensorKey: String,
    ): DataPoint<String?> {
        val valueAsJson = sensorData.value as Number
        val raw = device?.features?.find { it.key.contains("DRIVING_MODE") }?.values?.find {
            it.value?.jsonPrimitive?.intOrNull == valueAsJson
        }?.value

        val deviceData = device?.features?.find { it.key.contains("DRIVING_MODE") }?.values

        val value = when {

            sensorKey.contains("GENERIC") -> {
                //TODO:Find out how to convert to generic data and should we need it?
                deviceData?.find {
                    it.value?.jsonPrimitive?.intOrNull == valueAsJson
                }?.name ?: raw?.jsonPrimitive?.content
            }

            sensorKey.contains("NORMALIZED") -> {
                deviceData?.find {
                    it.value?.jsonPrimitive?.intOrNull == valueAsJson
                }?.name ?: raw?.jsonPrimitive?.content
            }

            else -> raw?.jsonPrimitive?.content
        }
        return DataPoint(
            key = sensorKey,
            value = value,
            timestamp = sensorData.timestamp,
        )
    }


    private fun <T> buildThrottleDataPoint(
        sensorData: DeviceProperty<T>,
        sensorKey: String,
    ): DataPoint<Float> {
        val feature = featureMap?.get(sensorKey)
        val splitSensorKey = sensorKey.split(":")
        val rawFeature = featureMap?.get(splitSensorKey[0] + ":" + splitSensorKey[1] + ":RAW")

        val numValue = (sensorData.value as Number).toFloat()

        val range = feature?.range?.takeIf { it.size == 2 }?.let { (min, max) ->
            min.toFloat()..max.toFloat()
        }

        val rawRange = rawFeature?.range?.takeIf { it.size == 2 }?.let { (min, max) ->
            min.toFloat()..max.toFloat()
        }

        val value = when {
            sensorKey.contains("NORMALIZED") -> {
                if (range != null) {
                    changeValueRange(
                        numValue,
                        oldMin = rawRange?.start ?: 0.0f,
                        oldMax = rawRange?.endInclusive ?: 1.0f,
                        newMin = range.start,
                        newMax = range.endInclusive
                    )
                } else numValue
            }

            else -> numValue
        }


        return DataPoint(
            key = sensorKey,
            value = value,
            timestamp = sensorData.timestamp,
        )
    }

    private fun <T> buildBrakeDataPoint(
        sensorData: DeviceProperty<T>,
        sensorKey: String,
    ): DataPoint<Float> {
        val feature = featureMap?.get(sensorKey)
        val splitSensorKey = sensorKey.split(":")
        val rawFeature = featureMap?.get(splitSensorKey[0] + ":" + splitSensorKey[1] + ":RAW")

        val numValue = (sensorData.value as Number).toFloat()

        val range = feature?.range?.takeIf { it.size == 2 }?.let { (min, max) ->
            min.toFloat()..max.toFloat()
        }

        val rawRange = rawFeature?.range?.takeIf { it.size == 2 }?.let { (min, max) ->
            min.toFloat()..max.toFloat()
        }

        val value = when {
            sensorKey.contains("NORMALIZED") -> {
                if (range != null) {
                    changeValueRange(
                        numValue,
                        oldMin = rawRange?.start ?: 0.0f,
                        oldMax = rawRange?.endInclusive ?: 1.0f,
                        newMin = range.start,
                        newMax = range.endInclusive
                    )
                } else numValue
            }

            else -> numValue
        }
        return DataPoint(
            key = sensorKey,
            value = value,
            timestamp = sensorData.timestamp,
        )
    }

    private fun <T> buildEvChargingRateDataPoint(
        sensorData: DeviceProperty<T>,
        sensorKey: String,
    ): DataPoint<Float> {
        val numValue = (sensorData.value as Number).toFloat()
        return DataPoint(
            key = sensorKey,
            value = numValue,
            timestamp = sensorData.timestamp,
        )
    }

    private fun <T> buildGenericIntDataPoint(
        sensorData: DeviceProperty<T>,
        sensorKey: String,
        sensorRange: ClosedFloatingPointRange<Float>? = null
    ): DataPoint<Int> {
        val feature = featureMap?.get(sensorKey)
        var numValue = (sensorData.value as? Number)?.toInt() ?: 0

        val range = feature?.range?.takeIf { it.size == 2 }?.let { (min, max) ->
            min.toFloat()..max.toFloat()
        }

        if (range != null && sensorRange != null) {
            numValue = changeValueRange(
                numValue.toFloat(),
                sensorRange.start,
                sensorRange.endInclusive,
                range.start,
                range.endInclusive
            ).toInt()
        }

        return DataPoint(
            key = sensorKey,
            value = numValue,
            timestamp = sensorData.timestamp,
            range = range ?: sensorRange
        )
    }

    private fun <T> buildGenericFloatDataPoint(
        sensorData: DeviceProperty<T>,
        sensorKey: String,
        sensorRange: ClosedFloatingPointRange<Float>? = null
    ): DataPoint<Float> {
        val feature = featureMap?.get(sensorKey)
        var numValue = (sensorData.value as? Number)?.toFloat() ?: 0f

        val range = feature?.range?.takeIf { it.size == 2 }?.let { (min, max) ->
            min.toFloat()..max.toFloat()
        }

        if (range != null && sensorRange != null) {
            numValue = changeValueRange(
                numValue.toFloat(),
                sensorRange.start,
                sensorRange.endInclusive,
                range.start,
                range.endInclusive
            )
        }

        return DataPoint(
            key = sensorKey,
            value = numValue,
            timestamp = sensorData.timestamp,
            range = range
        )
    }

    private fun <T> buildGenericStringDataPoint(
        sensorData: DeviceProperty<T>,
        sensorKey: String,
    ): DataPoint<String> {
        val value = when (sensorData.value) {
            is String -> sensorData.value as String
            is Number -> sensorData.value.toString()
            else -> ""
        }

        return DataPoint(
            key = sensorKey,
            value = value,
            timestamp = sensorData.timestamp,
        )
    }

    private fun <T> buildGenericBooleanDataPoint(
        sensorData: DeviceProperty<T>,
        sensorKey: String,
    ): DataPoint<Boolean> {
        val value = when (sensorData.value) {
            is Boolean -> sensorData.value as Boolean
            is Number -> (sensorData.value as Number).toInt() != 0
            else -> false
        }

        return DataPoint(
            key = sensorKey,
            value = value,
            timestamp = sensorData.timestamp,
        )
    }

    private fun <T : Number> buildAverageDataPoint(
        firstValue: DeviceProperty<T?>,
        secondValue: DeviceProperty<T?>,
        oldRange: ClosedRange<Float>? = null,
        sensorKey: String,
    ): DataPoint<Float> {
        val feature = featureMap?.get(sensorKey)

        val firstNum = firstValue.value?.toFloat() ?: 0f
        val secondNum = secondValue.value?.toFloat() ?: 0f

        val average = (firstNum + secondNum) / 2f

        val range = feature?.range?.takeIf { it.size == 2 }?.let { (min, max) ->
            min.toFloat()..max.toFloat()
        }

        val normalizedValue = if (range != null && oldRange != null) {
            changeValueRange(
                value = average,
                oldMin = oldRange.start,
                oldMax = oldRange.endInclusive,
                newMin = range.start,
                newMax = range.endInclusive
            )
        } else {
            average
        }

        val timestamp = maxOf(firstValue.timestamp, secondValue.timestamp)

        return DataPoint(
            key = sensorKey,
            value = normalizedValue,
            timestamp = timestamp,
            range = range
        )
    }


    fun updateJsonData(jsonString: String) {
        convertJsonToFeature(jsonString)
    }

    /** Speed: raw, generic, normalized. (No static speed) */
    fun getSpeedData(sensorData: DeviceProperty<Float?>): List<DataPoint<Float>> {
        val speed = sensorData.value

        if (speed == null) return emptyList()

        val keys = if (speed >= 0.0f) {
            listOf("SENSOR:SPEED:RAW", "SENSOR:SPEED:GENERIC", "SENSOR:SPEED:NORMALIZED")
        } else {
            listOf(
                "SENSOR:REVERSE_SPEED:RAW",
                "SENSOR:SPEED:GENERIC",
                "SENSOR:REVERSE_SPEED:NORMALIZED"
            )
        }

        return keys.map {
            buildSpeedDataPoint(sensorData, it)
        }
    }

    /** Driving Mode: raw vs. normalized (string). */
    fun getDrivingModeData(sensorData: DeviceProperty<Int?>): List<DataPoint<String?>> {
        val keys = listOf("SENSOR:DRIVING_MODE:RAW", "SENSOR:DRIVING_MODE:NORMALIZED")
        return keys.map {
            buildDrivingModeDataPoint(sensorData, it)
        }
    }

    fun <T> getGearData(sensorData: DeviceProperty<T>): List<DataPoint<String?>> {
        val keys = listOf("SENSOR:GEAR:NORMALIZED")
        return keys.map { buildGearStateDataPoint(sensorData, it) }
    }

    fun throttleData(sensorData: DeviceProperty<Float?>): List<DataPoint<Float>> {
        val keys = listOf("SENSOR:THROTTLE:RAW", "SENSOR:THROTTLE:NORMALIZED")
        return keys.map { buildThrottleDataPoint(sensorData, it) }
    }

    fun breakData(sensorData: DeviceProperty<Float?>): List<DataPoint<Float>> {
        val keys = listOf("SENSOR:BRAKE:RAW", "SENSOR:BRAKE:NORMALIZED")
        return keys.map { buildBrakeDataPoint(sensorData, it) }
    }

    fun evChargingRateData(sensorData: DeviceProperty<Float?>): List<DataPoint<Float>> {
        val rate = sensorData.value ?: 0f
        Timber.d("EV Charging Rate: $rate")
        val keys = if (rate >= 0.0f) {
            listOf("SENSOR:BATTERY_CHARGE_RATE:RAW")
        } else {
            listOf("SENSOR:REVERSE_BATTERY_CHARGE_RATE:RAW")
        }
        return keys.map { buildEvChargingRateDataPoint(sensorData, it) }
    }

    fun batteryData(
        maxBatteryLevel: Float,
        sensorData: DeviceProperty<Float?>
    ): List<DataPoint<Float>> {
        val keys = listOf("SENSOR:BATTERY:RAW")
        val currentLevel = sensorData.value ?: 0f
        val percentage = if (maxBatteryLevel > 0) {
            (currentLevel / maxBatteryLevel) * 100f
        } else {
            0f
        }
        return keys.map {
            DataPoint(
                key = it,
                value = percentage.coerceIn(0f, 100f),
                timestamp = sensorData.timestamp,
                range = 0f..100f
            )
        }
    }

    fun batteryCapacityData(sensorData: DeviceProperty<Float?>): List<DataPoint<Float>> {
        val keys = listOf("SENSOR:BATTERY_CAPACITY:STATIC")
        return keys.map { buildGenericFloatDataPoint(sensorData, it) }
    }

    fun engineData(sensorData: DeviceProperty<Float?>): List<DataPoint<Float>> {
        val keys = listOf("SENSOR:ENGINE:STATIC")
        return keys.map { buildGenericFloatDataPoint(sensorData, it) }
    }

    fun accelerationData(sensorData: DeviceProperty<Float?>): List<DataPoint<Float>> {
        val sensorValue = sensorData.value ?: 0f
        val keys = if (sensorValue >= 0f) {
            listOf("SENSOR:ACCELERATION:GENERIC")
        } else listOf("SENSOR:REVERSE_ACCELERATION:GENERIC")
        return keys.map { buildGenericFloatDataPoint(sensorData, it) }
    }

    fun hvacStatusData(sensorData: DeviceProperty<Boolean?>): List<DataPoint<Boolean>> {
        val keys = listOf("SENSOR:HVAC_STATUS:RAW")
        return keys.map { buildGenericBooleanDataPoint(sensorData, it) }
    }

    fun hvacDualStatusData(sensorData: DeviceProperty<Boolean?>): List<DataPoint<Boolean>> {
        val keys = listOf("SENSOR:HVAC_DUAL_STATUS:RAW")
        return keys.map { buildGenericBooleanDataPoint(sensorData, it) }
    }

    fun hvacTemperatureData(sensorDataList: List<DeviceProperty<Float?>>): List<List<DataPoint<Float>>> {
        val driverTemperatureAreaId = device?.features?.find {
            it.key.contains("HVAC_TEMPERATURE")
        }?.values?.find { it.name?.uppercase() == "AREA_ID" }?.value?.jsonPrimitive?.intOrNull
        return sensorDataList.map { sensorData ->
            val keys = if (sensorData.areaId == driverTemperatureAreaId) {
                listOf("SENSOR:HVAC_TEMPERATURE:RAW")
            } else listOf("HVAC_PASSENGER_TEMPERATURE")
            keys.map { buildGenericFloatDataPoint(sensorData, it) }
        }
    }

    fun hvacFanSpeedData(sensorData: DeviceProperty<Int?>): List<DataPoint<Int>> {
        val keys = listOf("SENSOR:HVAC_FAN_SPEED:RAW")
        val sensorRange = device?.features?.find {
            it.key.contains("HVAC_FAN_SPEED")
        }?.range?.takeIf { it.size == 2 }?.let { (min, max) ->
            min.toFloat()..max.toFloat()
        }
        return keys.map { buildGenericIntDataPoint(sensorData, it, sensorRange) }
    }

    fun hvacPassengerFanSpeedData(sensorData: DeviceProperty<Int?>): List<DataPoint<Int>> {
        val keys = listOf("SENSOR:HVAC_PASSENGER_FAN_SPEED:RAW")
        val sensorRange = device?.features?.find {
            it.key.contains("HVAC_PASSENGER_FAN_SPEED")
        }?.range?.takeIf { it.size == 2 }?.let { (min, max) ->
            min.toFloat()..max.toFloat()
        }
        return keys.map { buildGenericIntDataPoint(sensorData, it, sensorRange) }
    }

    fun hvacMaxData(sensorData: DeviceProperty<Boolean?>): List<DataPoint<Boolean>> {
        val keys = listOf("SENSOR:HVAC_MAX:RAW")
        return keys.map { buildGenericBooleanDataPoint(sensorData, it) }
    }

    fun hvacInteriorTemperatureData(sensorData: DeviceProperty<Float?>): List<DataPoint<Float>> {
        val keys = listOf("SENSOR:HVAC_INTERIOR_TEMPERATURE:RAW")
        return keys.map { buildGenericFloatDataPoint(sensorData, it) }
    }

    fun hvacExteriorTemperatureData(sensorData: DeviceProperty<Float?>): List<DataPoint<Float>> {
        val keys = listOf("SENSOR:HVAC_EXTERIOR_TEMPERATURE:RAW")
        return keys.map { buildGenericFloatDataPoint(sensorData, it) }
    }

    fun seatOccupancyData(sensorData: DeviceProperty<Int?>): List<DataPoint<Int>> {
        val keys = listOf("SENSOR:SEAT_OCCUPANCY:RAW")
        return keys.map { key ->
            buildGenericIntDataPoint(sensorData, key)
        }
    }

    fun directionData(sensorData: DeviceProperty<GyroscopeData?>): List<DataPoint<Float>> {
        val gyroData = sensorData.value
        if (gyroData == null) return emptyList()

        val timestamp = sensorData.timestamp

        return listOf(
            DataPoint(
                key = "SENSOR:DIRECTION_X:GENERIC",
                value = gyroData.x,
                timestamp = timestamp
            ),
            DataPoint(
                key = "SENSOR:DIRECTION_Y:GENERIC",
                value = gyroData.y,
                timestamp = timestamp
            ),
            DataPoint(
                key = "SENSOR:DIRECTION_Z:GENERIC",
                value = gyroData.z,
                timestamp = timestamp
            )
        )
    }

    fun locationData(locationState: LocationState?): DataPoint<List<Float>>? {
        if (locationState == null) return null
        return DataPoint(
            key = "SENSOR:LOCATION:GENERIC",
            value = listOf(locationState.latitude.toFloat(), locationState.longitude.toFloat()),
            timestamp = locationState.timestamp
        )
    }

    fun altitudeData(altitude: Float?): DataPoint<Float>? {
        if (altitude == null) return null
        return DataPoint(
            key = "SENSOR:ALTITUDE:GENERIC",
            value = altitude,
            timestamp = System.currentTimeMillis()
        )

    }

    fun steeringWheelAngleData(sensorData: DeviceProperty<Float?>): List<DataPoint<Float>> {
        val keys = listOf("SENSOR:STEERING_WHEEL_ANGLE:RAW")
        return keys.map { buildGenericFloatDataPoint(sensorData, it) }
    }

    fun doorsStateData(sensorData: List<DeviceProperty<Int?>>): List<DataPoint<List<Boolean>>> {
        val doorsKey = "SENSOR:DOORS_STATE:RAW"
        val doorConfigs = device?.features?.firstOrNull { it.key.contains("DOORS_STATE") }?.values
        val orderedDoors = doorConfigs
            ?.associate { it.name to it.value?.jsonPrimitive?.intOrNull }
            ?.let { configMap ->
                sensorData.sortedBy { data ->
                    when (data.areaId) {
                        configMap["LEFT_TOP_AREA_ID"] -> 0
                        configMap["RIGHT_TOP_AREA_ID"] -> 1
                        configMap["LEFT_BACK_AREA_ID"] -> 2
                        configMap["RIGHT_BACK_AREA_ID"] -> 3
                        else -> 4
                    }
                }
            } ?: sensorData
        return listOf(
            DataPoint(
                key = doorsKey,
                value = orderedDoors.map { it.value == 1 },
                timestamp = sensorData.maxOfOrNull { it.timestamp } ?: System.currentTimeMillis()
            )
        )
    }

    fun windowsStateData(sensorData: List<DeviceProperty<Int?>>): List<DataPoint<List<Int>>> {
        val widowsKey = "SENSOR:WINDOWS_STATE:RAW"
        val rawFeature = featureMap?.get(widowsKey)
        val windowConfigs = device?.features?.first { it.key.contains("WINDOWS_STATE") }?.values
        val orderedWindows = windowConfigs
            ?.associate { it.name to it.value?.jsonPrimitive?.intOrNull }
            ?.let { configMap ->
                sensorData.sortedBy { data ->
                    when (data.areaId) {
                        configMap["LEFT_TOP_AREA_ID"] -> 0
                        configMap["RIGHT_TOP_AREA_ID"] -> 1
                        configMap["LEFT_BACK_AREA_ID"] -> 2
                        configMap["RIGHT_BACK_AREA_ID"] -> 3
                        else -> 4
                    }
                }
            } ?: sensorData

        return listOf(
            DataPoint<List<Int>>(
                key = widowsKey,
                value = orderedWindows.mapNotNull { it.value },
                timestamp = sensorData.maxOfOrNull { it.timestamp } ?: System.currentTimeMillis(),
                range = rawFeature?.range?.takeIf { it.size == 2 }?.let {
                    it[0].toFloat()..it[1].toFloat()
                }
            )
        )
    }

    fun trunkStateData(sensorData: DeviceProperty<Int?>): List<DataPoint<Boolean>> {
        val keys = listOf("SENSOR:TRUNK_STATE:RAW")
        return keys.map { buildGenericBooleanDataPoint(sensorData, it) }
    }

    fun frunkStateData(sensorData: DeviceProperty<Int?>): List<DataPoint<Boolean>> {
        val keys = listOf("SENSOR:FRUNK_STATE:RAW")
        return keys.map { buildGenericBooleanDataPoint(sensorData, it) }
    }

    fun trunkOpenAngleData(sensorData: DeviceProperty<Float?>): List<DataPoint<Float>> {
        val keys = listOf("SENSOR:TRUNK_OPEN_ANGLE:RAW")
        return keys.map { buildGenericFloatDataPoint(sensorData, it) }
    }

    fun frunkOpenAngleData(sensorData: DeviceProperty<Float?>): List<DataPoint<Float>> {
        val keys = listOf("SENSOR:FRUNK_OPEN_ANGLE:RAW")
        return keys.map { buildGenericFloatDataPoint(sensorData, it) }
    }

    fun bedCoverPositionData(sensorData: DeviceProperty<Float?>): List<DataPoint<Float>> {
        val keys = listOf("SENSOR:BED_COVER_POSITION:RAW")
        return keys.map { buildGenericFloatDataPoint(sensorData, it) }
    }

    fun beamStateData(sensorData: DeviceProperty<Boolean?>): List<DataPoint<Boolean>> {
        val keys = listOf("SENSOR:LIGHTS_BEAM:RAW")
        return keys.map { buildGenericBooleanDataPoint(sensorData, it) }
    }

    fun headlightsStateData(sensorData: DeviceProperty<Boolean?>): List<DataPoint<Boolean>> {
        val keys = listOf("SENSOR:LIGHTS_HEADLIGHTS:RAW")
        return keys.map { buildGenericBooleanDataPoint(sensorData, it) }
    }

    fun brakeLightsStateData(sensorData: DeviceProperty<Boolean?>): List<DataPoint<Boolean>> {
        val keys = listOf("SENSOR:LIGHTS_BRAKE:RAW")
        return keys.map { buildGenericBooleanDataPoint(sensorData, it) }
    }

    fun turnSignalsStateData(sensorData: DeviceProperty<Boolean?>): List<DataPoint<Boolean>> {
        val keys = listOf("SENSOR:LIGHTS_TURNSIGNALS:RAW")
        return keys.map { buildGenericBooleanDataPoint(sensorData, it) }
    }

// Weather

    fun weatherLowTemperatureData(sensorData: DeviceProperty<Float?>): List<DataPoint<Float>> {
        val keys = listOf("SENSOR:WEATHER_LOW_TEMPERATURE:GENERIC")
        return keys.map { buildGenericFloatDataPoint(sensorData, it) }
    }

    fun weatherHighTemperatureData(sensorData: DeviceProperty<Float?>): List<DataPoint<Float>> {
        val keys = listOf("SENSOR:WEATHER_HIGH_TEMPERATURE:GENERIC")
        return keys.map { buildGenericFloatDataPoint(sensorData, it) }
    }

    fun weatherHumidityData(sensorData: DeviceProperty<Float?>): List<DataPoint<Float>> {
        val keys = listOf("SENSOR:WEATHER_HUMIDITY:GENERIC")
        return keys.map { buildGenericFloatDataPoint(sensorData, it) }
    }

    fun weatherWindSpeedData(sensorData: DeviceProperty<Float?>): List<DataPoint<Float>> {
        val keys = listOf("SENSOR:WEATHER_WIND_SPEED:GENERIC")
        return keys.map { buildGenericFloatDataPoint(sensorData, it) }
    }

    fun weatherWindDirectionData(sensorData: DeviceProperty<Float?>): List<DataPoint<Float>> {
        val keys = listOf("SENSOR:WEATHER_WIND_DIRECTION:GENERIC")
        return keys.map { buildGenericFloatDataPoint(sensorData, it) }
    }

    fun weatherPrecipitationData(sensorData: DeviceProperty<Float?>): List<DataPoint<Float>> {
        val keys = listOf("SENSOR:WEATHER_PRECIPITATION:GENERIC")
        return keys.map { buildGenericFloatDataPoint(sensorData, it) }
    }

    fun weatherPrecipitationTypeData(sensorData: DeviceProperty<String?>): List<DataPoint<String>> {
        val keys = listOf("SENSOR:WEATHER_PRECIPITATION_TYPE:GENERIC")
        return keys.map { buildGenericStringDataPoint(sensorData, it) }
    }

    fun weatherUvIndexData(sensorData: DeviceProperty<Float?>): List<DataPoint<Float>> {
        val keys = listOf("SENSOR:WEATHER_UV_INDEX:GENERIC")
        return keys.map { buildGenericFloatDataPoint(sensorData, it) }
    }

    fun weatherData(sensorData: DeviceProperty<String?>): List<DataPoint<String>> {
        val keys = listOf("SENSOR:WEATHER_CONDITION:GENERIC")
        return keys.map { buildGenericStringDataPoint(sensorData, it) }
    }

    fun weatherTemperatureData(sensorData: DeviceProperty<Float?>): List<DataPoint<Float>> {
        val keys = listOf("SENSOR:WEATHER_TEMPERATURE:GENERIC")
        return keys.map { buildGenericFloatDataPoint(sensorData, it) }
    }

    fun nearbyTerrainData(sensorData: DeviceProperty<List<String?>>): List<DataPoint<List<String?>?>> {
        val keys = listOf("SENSOR:NEARBY_TERRAIN:GENERIC")
        return keys.map {
            DataPoint(
                key = it,
                value = sensorData.value,
                timestamp = sensorData.timestamp,
            )
        }
    }
}

fun changeValueRange(
    value: Float,
    oldMin: Float,
    oldMax: Float,
    newMin: Float = 0.0f,
    newMax: Float = 1.0f
): Float {
    // Handle the case where oldMin equals oldMax to prevent division by zero
    if (oldMin == oldMax) return (newMin + newMax) / 2 // Return middle of target range

    // Calculate how far along the old range the value is (0.0 to 1.0)
    val normalizedValue = (value - oldMin) / (oldMax - oldMin)

    // Map the normalized value to the new range
    val mappedValue = normalizedValue * (newMax - newMin) + newMin

    // Optionally, clamp to ensure it's within newMin..newMax
    return mappedValue.coerceIn(newMin, newMax)
}

fun convertUnit(value: Float, valueUnit: String, targetUnit: String): Float {
    val kmph = listOf("kmph", "kmh", "km/h")
    val mph = listOf("mph")
    val ms = listOf("mps", "m/s", "meters per second")

    return when {
        mph.any { it == valueUnit.lowercase() } && kmph.any { it == targetUnit.lowercase() } -> value * 1.60934f // mph to kmph
        kmph.any { it == valueUnit.lowercase() } && mph.any { it == targetUnit.lowercase() } -> value * 0.621371f // kmph to mph

        mph.any { it == valueUnit.lowercase() } && ms.any { it == targetUnit.lowercase() } -> value * 0.44704f // mph to m/s
        ms.any { it == valueUnit.lowercase() } && mph.any { it == targetUnit.lowercase() } -> value * 2.23694f // m/s to mph

        kmph.any { it == valueUnit.lowercase() } && ms.any { it == targetUnit.lowercase() } -> value * 0.27778f // kmph to m/s
        ms.any { it == valueUnit.lowercase() } && kmph.any { it == targetUnit.lowercase() } -> value * 3.6f    // m/s to kmph

        else -> value
    }
}

fun buildFeatureMap(config: DeviceConfig): Map<String, Feature> {
    val map = mutableMapOf<String, Feature>()
    for (feat in config.features) {
        map[feat.key] = feat
    }
    for (dev in config.devices) {
        for (feat in dev.features) {
            map[feat.key] = feat
        }
    }
    return map
}

inline fun <reified T> createDefaultDataProperty(
    value: T,
    name: String = UUID.randomUUID().toString()
): DeviceProperty<T> {
    return DeviceProperty(
        timestamp = System.currentTimeMillis(),
        id = 0,
        name = name,
        value = value,
        areaId = 0,
    )
}