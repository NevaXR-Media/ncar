package com.nevaxr.foundation.car.device

import com.nevaxr.foundation.car.device.VehicleType.TOGG
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

@Serializable
sealed interface IData {
    val type: String
    val enabled: Boolean

    @SerialName("timestamp")
    val timestamp: Long
}

@Serializable
data class DataContainer(
    @SerialName("values")
    val values: List<IData>
)

object DataType {

    object Limit {

        @Serializable
        @SerialName("LIMIT") // Discriminator value for SENSOR category
        sealed class LimitData(@SerialName("type") override val type: String = "LIMIT") : IData


        @Serializable
        enum class ReduceMotionValue {
            LEVEL_NONE,
            LEVEL_1,
            LEVEL_2,
            LEVEL_3
        }

        @Serializable
        @SerialName("LIMIT") // Discriminator value
        data class ReduceMotion(
            @SerialName("value")
            val value: ReduceMotionValue,

            @SerialName("enabled")
            override val enabled: Boolean,

            override val timestamp: Long

        ) : LimitData() {
            // Optional: Mapping function
            fun getMappedValue(): Int {
                return when (value) {
                    ReduceMotionValue.LEVEL_NONE -> 0
                    ReduceMotionValue.LEVEL_1 -> 30
                    ReduceMotionValue.LEVEL_2 -> 60
                    ReduceMotionValue.LEVEL_3 -> 90
                }
            }
        }
    }

    object SensorType {
        // TODO: Seperate sensor types
    }

    // SENSOR related classes and enums
    object Sensor {

        // region SensorUnitTypes
        @Serializable
        enum class SpeedUnit {
            KMH, MPH
        }

        @Serializable
        enum class DrivingMode {
            ECO, COMFORT, SPORT
        }

        @Serializable
        enum class HvacArea {
            LEFT,
            RIGHT
        }

        @Serializable
        enum class Gear {
            PARK,
            DRIVE,
            NEUTRAL,
            REVERSE,
        }
        // endregion SensorUnitTypes

        @Serializable
        @SerialName("SENSOR") // Discriminator value for SENSOR category
        sealed class SensorData(@SerialName("type") override val type: String = "SENSOR") : IData

        @Serializable
        @SerialName("SPEED")
        data class Speed(
            @SerialName("value")
            val value: Float,

            @SerialName("unit")
            val unit: SpeedUnit,

            @SerialName("enabled")
            override val enabled: Boolean,

            override val timestamp: Long

        ) : SensorData()

        @Serializable
        @SerialName("GYROSCOPE")
        data class Gyroscope(
            @SerialName("value")
            val value: Float,

            @SerialName("enabled")
            override val enabled: Boolean,

            override val timestamp: Long

        ) : SensorData()

        @Serializable
        @SerialName("THROTTLE")
        data class Throttle(
            @SerialName("value")
            val value: Float,

            @SerialName("enabled")
            override val enabled: Boolean,

            override val timestamp: Long
        ) : SensorData()

        @Serializable
        @SerialName("BRAKE")
        data class Brake(
            @SerialName("value")
            val value: Float,

            @SerialName("enabled")
            override val enabled: Boolean,

            override val timestamp: Long
        ) : SensorData()

        @Serializable
        @SerialName("INTERNAL_TEMPERATURE")
        data class InternalTemperature(
            @SerialName("value")
            val value: Float,

            @SerialName("enabled")
            override val enabled: Boolean,

            override val timestamp: Long

        ) : SensorData()

        //TODO: Implement to nevacarsdk & add as sensor
        @Serializable
        @SerialName("EV_BATTERY_INSTANTANEOUS_CHARGE_RATE")
        data class EvBatteryInstantaneousChargeRate(
            @SerialName("value")
            val value: Float,

            @SerialName("enabled")
            override val enabled: Boolean,

            override val timestamp: Long

        ) : SensorData()

        // region hvac
        @Serializable
        @SerialName("HVAC_AC_ON")
        data class HvacAcOn(
            @SerialName("value")
            val value: Boolean,

            @SerialName("enabled")
            override val enabled: Boolean,

            override val timestamp: Long

        ) : SensorData()

        @Serializable
        @SerialName("HVAC_DUAL_ON")
        data class HvacDualOn(
            @SerialName("value")
            val value: Boolean,

            @SerialName("enabled")
            override val enabled: Boolean,

            override val timestamp: Long

        ) : SensorData()

        @Serializable
        @SerialName("HVAC_MAX_AC_ON")
        data class HvacMaxAcOn(
            @SerialName("value")
            val value: Boolean,

            @SerialName("enabled")
            override val enabled: Boolean,

            override val timestamp: Long

        ) : SensorData()

        @Serializable
        @SerialName("HVAC_FAN_SPEED")
        data class HvacFanSpeed(
            @SerialName("value")
            val value: Int,

            @SerialName("enabled")
            override val enabled: Boolean,

            override val timestamp: Long

        ) : SensorData()

        @OptIn(InternalSerializationApi::class)
        @Serializable
        data class HvacProperty(
            @SerialName("values")
            val values: Float,
            @SerialName("area")
            val area: HvacArea,
            @SerialName("timestamp")
            val timestamp: Long
        )

        @Serializable
        @SerialName("HVAC_AC_TEMPERATURE_SET")
        data class HvacTemperatureSet(
            @SerialName("value")
            val value: List<HvacProperty>,

            @SerialName("enabled")
            override val enabled: Boolean,

            override val timestamp: Long

        ) : SensorData()


        // endregion

        @Serializable
        @SerialName("BATTERY")
        data class Battery(
            @SerialName("value")
            val value: Float,

            @SerialName("enabled")
            override val enabled: Boolean,

            override val timestamp: Long

        ) : SensorData()

        @Serializable
        @SerialName("DEVICE_TYPE")
        data class DeviceType(
            @SerialName("value")
            val value: Float,

            @SerialName("enabled")
            override val enabled: Boolean,

            override val timestamp: Long
        ) : SensorData()

        @Serializable
        @SerialName("DRIVING_MODE")
        data class DrivingModeData(
            @SerialName("value")
            val value: DrivingMode,

            @SerialName("enabled")
            override val enabled: Boolean,

            override val timestamp: Long

        ) : SensorData()

        @Serializable
        @SerialName("GEAR_STATE")
        data class GearState(
            @SerialName("value")
            val value: Gear,

            @SerialName("enabled")
            override val enabled: Boolean,

            override val timestamp: Long

        ) : SensorData()
    }

    // REMOTE_SENSOR related classes and enums
    object RemoteSensor {

        @Serializable
        enum class WeatherConditionValue {
            BLOWINGDUST,
            CLEAR,
            CLOUDY,
            FOGGY,
            HAZE,
            MOSTLYCLEAR,
            MOSTLYCLOUDY,
            PARTLYCLOUDY,
            SMOKY,
            BREEZY,
            WINDY,
            DRIZZLE,
            HEAVYRAIN,
            ISOLATEDTHUNDERSTORMS,
            RAIN,
            SUNSHOWERS,
            SCATTEREDTHUNDERSTORMS,
            STRONGSTORMS,
            THUNDERSTORMS,
            FRIGID,
            HAIL,
            HOT,
            FLURRIES,
            SLEET,
            SNOW,
            SUNFLURRIES,
            WINTRYMIX,
            BLIZZARD,
            BLOWINGSNOW,
            FREEZINGDRIZZLE,
            FREEZINGRAIN,
            HEAVYSNOW,
            HURRICANE,
            TROPICALSTORM
        }

        @Serializable
        @SerialName("REMOTE_SENSOR") // Discriminator value for REMOTE_SENSOR category
        sealed class RemoteSensorData(
            @SerialName("type")
            override val type: String = "REMOTE_SENSOR"
        ) : IData

        @Serializable
        @SerialName("WEATHER_CONDITION")
        data class WeatherCondition(
            @SerialName("value")
            val value: WeatherConditionValue,

            @SerialName("enabled")
            override val enabled: Boolean,

            override val timestamp: Long

        ) : RemoteSensorData()

        @Serializable
        enum class WeatherConditionSimplifiedValue {
            SUNNY,
            CLOUDY,
            RAINY,
            SNOWY,
            WINDY,
            FOGGY,
            HAZY,
            STORMY,
            HOT,
            COLD
        }

        @Serializable
        @SerialName("WEATHER_CONDITION_SIMPLIFIED")
        data class WeatherConditionSimplified(
            @SerialName("value")
            val value: WeatherConditionSimplifiedValue,

            @SerialName("enabled")
            override val enabled: Boolean,

            override val timestamp: Long

        ) : RemoteSensorData()

        @Serializable
        @SerialName("GPS")
        data class Gps(
            @SerialName("longitude")
            val longitude: Float,
            @SerialName("latitude")
            val latitude: Float,
            @SerialName("bearing")
            val bearing: Float,
            @SerialName("altitude")
            val altitude: Float,
            @SerialName("accuracy")
            val accuracy: Float,
            @SerialName("speed")
            val speed: Float,

            @SerialName("enabled")
            override val enabled: Boolean,

            override val timestamp: Long

        ) : RemoteSensorData()
    }
}

val module = SerializersModule {
    polymorphic(IData::class) {
        // LIMIT subclasses
        subclass(DataType.Limit.ReduceMotion::class)
        // SENSOR subclasses
        subclass(DataType.Sensor.HvacAcOn::class)
        subclass(DataType.Sensor.HvacDualOn::class)
        subclass(DataType.Sensor.HvacMaxAcOn::class)
        subclass(DataType.Sensor.HvacFanSpeed::class)
        subclass(DataType.Sensor.HvacTemperatureSet::class)
        subclass(DataType.Sensor.Speed::class)
        subclass(DataType.Sensor.Gyroscope::class)
        subclass(DataType.Sensor.Throttle::class)
        subclass(DataType.Sensor.Brake::class)
        subclass(DataType.Sensor.InternalTemperature::class)
        subclass(DataType.Sensor.Battery::class)
        subclass(DataType.Sensor.DeviceType::class)
        subclass(DataType.Sensor.DrivingModeData::class)
        // REMOTE_SENSOR subclasses
        subclass(DataType.RemoteSensor.WeatherCondition::class)
        subclass(DataType.RemoteSensor.WeatherConditionSimplified::class)
    }
}

val communicationDataJson = Json {
    serializersModule = module
    classDiscriminator = "key" // Use "type" as the discriminator
    encodeDefaults = true // Ensure default values are encoded
}

fun main() {
    val drivingMode = DataContainer(
        values = listOf(
            DataType.Sensor.DrivingModeData(
                value = DataType.Sensor.DrivingMode.SPORT,
                enabled = true,
                timestamp = System.currentTimeMillis()
            )
        )
    )
    val speed = DataContainer(
        values = listOf(
            DataType.Sensor.Speed(
                value = 100f,
                unit = DataType.Sensor.SpeedUnit.KMH,
                enabled = true,
                timestamp = System.currentTimeMillis()
            )
        )
    )
    val weatherCondition = DataContainer(
        values = listOf(
            DataType.RemoteSensor.WeatherCondition(
                value = DataType.RemoteSensor.WeatherConditionValue.CLOUDY,
                enabled = true,
                timestamp = System.currentTimeMillis()
            )
        )
    )
    val hvacAcOn = DataContainer(
        values = listOf(
            DataType.Sensor.HvacAcOn(
                value = true,
                enabled = true,
                timestamp = System.currentTimeMillis()
            )
        )
    )

    val hvacMaxAcOn = DataContainer(
        values = listOf(
            DataType.Sensor.HvacMaxAcOn(
                value = true,
                enabled = true,
                timestamp = System.currentTimeMillis()
            )
        )
    )
    val hvacDualOn = DataContainer(
        values = listOf(
            DataType.Sensor.HvacDualOn(
                value = true,
                enabled = true,
                timestamp = System.currentTimeMillis()
            )
        )
    )

    val hvacFanSpeed = DataContainer(
        values = listOf(
            DataType.Sensor.HvacFanSpeed(
                value = 0,
                enabled = true,
                timestamp = System.currentTimeMillis()
            )
        )
    )

    val hvacTemperatureSet = DataContainer(
        values = listOf(
            DataType.Sensor.HvacTemperatureSet(
                value = listOf(
                    DataType.Sensor.HvacProperty(
                        values = 22f,
                        area = DataType.Sensor.HvacArea.LEFT,
                        timestamp = System.currentTimeMillis()
                    ),
                    DataType.Sensor.HvacProperty(
                        values = 23f,
                        area = DataType.Sensor.HvacArea.RIGHT,
                        timestamp = System.currentTimeMillis()
                    )
                ),
                enabled = true,
                timestamp = System.currentTimeMillis()
            )
        )
    )


    println(communicationDataJson.encodeToJsonElement(drivingMode))
    println(communicationDataJson.encodeToJsonElement(speed))
    println(communicationDataJson.encodeToJsonElement(weatherCondition))

    println(communicationDataJson.encodeToJsonElement(hvacAcOn))
    println(communicationDataJson.encodeToJsonElement(hvacMaxAcOn))
    println(communicationDataJson.encodeToJsonElement(hvacDualOn))
    println(communicationDataJson.encodeToJsonElement(hvacFanSpeed))
    println(communicationDataJson.encodeToJsonElement(hvacTemperatureSet))

    //"""{"values":[{"value":"$mode", "enabled":true,"type":"SENSOR","key":"DRIVING_MODE"}]}"""
}


fun Int.toVehicleGearState(vehicleType: VehicleType): DataType.Sensor.Gear? {
    return when (vehicleType) {
        TOGG -> toggVehicleState()
    }
}

fun Int.toggVehicleState() = when (this) {
    5 -> DataType.Sensor.Gear.DRIVE
    6 -> DataType.Sensor.Gear.NEUTRAL
    7 -> DataType.Sensor.Gear.REVERSE
    8 -> DataType.Sensor.Gear.PARK
    else -> null
}
