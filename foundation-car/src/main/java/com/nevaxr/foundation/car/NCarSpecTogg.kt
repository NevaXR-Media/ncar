package com.nevaxr.foundation.car

import com.nevaxr.foundation.car.device.NCarDoorState
import timber.log.Timber

object NCarSpecTogg : NCarSpecGeneric {
    override val specName = "Togg"

    override fun providers(carService: NCarServiceBase) = listOf(
        carService.propertyProviderOf(NVhalProvider::class)
    )

    override suspend fun identify(carService: NCarServiceBase): Boolean {
        val brand = brand.getProperty(carService)
        val model = model.getProperty(carService)
        Timber.d("Checking if the car is Togg, brand: ${brand ?: "null"}, model: ${model ?: "null"}")
        return true
    }

    override val deviceId = NVhalProperty.string(NVhalKey.INFO_VIN)
    override val model = NVhalProperty.string(NVhalKey.INFO_MODEL).optional()
    override val brand = NVhalProperty.string(NVhalKey.INFO_MAKE).optional()

    override val speedRange = MeasurementUnitRange(0f, 51.3889f, UnitSpeed.metersPerSecond)
    override val speed = NVhalProperty.measurement(NVhalKey.PERF_VEHICLE_SPEED, UnitSpeed.metersPerSecond, speedRange)
    override val gear = NVhalProperty.int(NVhalKey.GEAR_SELECTION, NCarGear.Park) { rawGear: Int ->
        when (rawGear) {
            1 -> NCarGear.Neutral
            2 -> NCarGear.Reverse
            4 -> NCarGear.Park
            8 -> NCarGear.Drive
            else -> NCarGear.Park
        }
    }

    enum class DrivingMode { ECO, COMFORT, SPORT, UNKNOWN }
    val drivingMode = NVhalProperty.int(NVhalKey.VENDOR_DRIVE_MODE_PROPERTY, DrivingMode.UNKNOWN) { raw: Int ->
        when (raw) {
            0 -> DrivingMode.ECO
            1 -> DrivingMode.COMFORT
            2 -> DrivingMode.SPORT
            else -> DrivingMode.UNKNOWN
        }
    }

    override val evChargingRate = NVhalProperty.measurement(NVhalKey.EV_BATTERY_INSTANTANEOUS_CHARGE_RATE, UnitPower.megawatts, MeasurementUnitRange(0f, 175000000f, UnitPower.megawatts), 0f)
    override val hvacStatus = NVhalProperty.boolean(NVhalKey.HVAC_AC_ON)
    override val hvacDualStatus = NVhalProperty.boolean(NVhalKey.HVAC_DUAL_ON)
    override val hvacMaxStatus = NVhalProperty.boolean(NVhalKey.HVAC_MAX_AC_ON)
    override val hvacFanSpeed = NVhalProperty.measurement(NVhalKey.HVAC_FAN_SPEED, UnitRpm, MeasurementUnitRange(0f, 7f, UnitRpm))
    override val hvacPassengerSpeed = NVhalProperty.measurement(NVhalKey.HVAC_FAN_SPEED, UnitRpm, MeasurementUnitRange(0f, 7f, UnitRpm))
    override val hvacTemperature = NVhalProperty.measurement(NVhalKey.HVAC_TEMPERATURE_CURRENT, UnitTemperature.celsius)
    override val hvacInteriorTemperature = NVhalProperty.measurement(NVhalKey.VENDOR_CABIN_CURRENT_TEMP_DEG_PROPERTY, UnitTemperature.celsius)
    override val hvacExteriorTemperature = NVhalProperty.measurement(NVhalKey.ENV_OUTSIDE_TEMPERATURE, UnitTemperature.celsius)
    override val batteryCapacity = NVhalProperty.measurement(NVhalKey.INFO_EV_BATTERY_CAPACITY, UnitEnergy.kilowattHours, MeasurementUnitRange(0f, 88.5f, UnitEnergy.kilowattHours))
    override val battery = NVhalProperty.float(NVhalKey.EV_BATTERY_LEVEL)
    override val engine = NVhalProperty.measurement(NVhalKey.ENGINE_RPM, UnitPower.kilowatts, MeasurementUnitRange(0f, 15_000f, UnitPower.kilowatts))
    override val acceleration = NVhalProperty.constant(0f)
    override val seatOccupancy = NVhalProperty.array(NVhalKey.SEAT_OCCUPANCY) { raw: Int -> raw == 1 }
    override val steeringWheelAngle = NVhalProperty.constant(Measurement(0f, UnitAngle.degrees))

    private const val DOOR_FRONT_LEFT_AREA_ID = 1
    private const val DOOR_FRONT_RIGHT_AREA_ID = 4
    private const val DOOR_BACK_LEFT_AREA_ID = 16
    private const val DOOR_BACK_RIGHT_AREA_ID = 64
    override val doorState = NVhalProperty.rawReducer<Int, _>(NVhalKey.DOOR_POS, NCarDoorState()) { state, property ->
        when (property.areaId) {
            DOOR_FRONT_LEFT_AREA_ID -> state.copy(frontLeft = property.value == 1)
            DOOR_FRONT_RIGHT_AREA_ID -> state.copy(frontRight = property.value == 1)
            DOOR_BACK_LEFT_AREA_ID -> state.copy(backLeft = property.value == 1)
            DOOR_BACK_RIGHT_AREA_ID -> state.copy(backRight = property.value == 1)
            else -> state
        }
    }

    private const val TRUNK_AREA_ID = 536870912
    override val trunkState = NVhalProperty.rawReducer<Int, Boolean>(NVhalKey.DOOR_POS, false) { state, property ->
        if (property.areaId == TRUNK_AREA_ID) {
            property.value == 1
        } else {
            state
        }
    }

    override val trunkAngle = NVhalProperty.rawReducer<Int, _>(NVhalKey.DOOR_POS, Measurement(0f, UnitAngle.degrees)) { state, property ->
        if (property.areaId == TRUNK_AREA_ID) {
            if (property.value == 1) {
                Measurement(180f, UnitAngle.degrees)
            } else {
                Measurement(0f, UnitAngle.degrees)
            }
        } else {
            state
        }
    }

    private const val FRUNK_AREA_ID = 268435456
    override val frunkState = NVhalProperty.rawReducer<Int, Boolean>(NVhalKey.DOOR_POS, false) { state, property ->
        if (property.areaId == FRUNK_AREA_ID) {
            property.value == 1
        } else {
            state
        }
    }

    override val frunkAngle = NVhalProperty.rawReducer<Int, Measurement<UnitAngle>>(NVhalKey.DOOR_POS, Measurement(0f, UnitAngle.degrees)) { state, property ->
        if (property.areaId == FRUNK_AREA_ID) {
            if (property.value == 1) {
                Measurement(180f, UnitAngle.degrees)
            } else {
                Measurement(0f, UnitAngle.degrees)
            }
        } else {
            state
        }
    }

    private const val WINDOW_FRONT_LEFT_AREA_ID = 16
    private const val WINDOW_FRONT_RIGHT_AREA_ID = 64
    private const val WINDOW_BACK_LEFT_AREA_ID = 256
    private const val WINDOW_BACK_RIGHT_AREA_ID = 1024
    override val windowState = NVhalProperty.rawReducer<Float, _>(NVhalKey.WINDOW_POS, NCarWindowState()) { state, property ->
        when (property.areaId) {
            WINDOW_FRONT_LEFT_AREA_ID -> state.copy(frontLeft = property.value)
            WINDOW_FRONT_RIGHT_AREA_ID -> state.copy(frontRight = property.value)
            WINDOW_BACK_LEFT_AREA_ID -> state.copy(backLeft = property.value)
            WINDOW_BACK_RIGHT_AREA_ID -> state.copy(backRight = property.value)
            else -> state
        }
    }

    private const val AMBIENT_COLOR_NONE = 0
    private const val AMBIENT_COLOR_TURQUOISE = 1
    private const val AMBIENT_COLOR_ORANGE = 2
    private const val AMBIENT_COLOR_YELLOW = 3
    private const val AMBIENT_COLOR_PURPLE = 4
    private const val AMBIENT_COLOR_RED = 5
    private const val AMBIENT_COLOR_BLUE = 6
    private const val AMBIENT_COLOR_GREEN = 7
    private const val AMBIENT_COLOR_WHITE = 8
    override val ambientLight = NVhalProperty.int(NVhalKey.VENDOR_AMBIENT_LIGHT_READ, NCarAmbientColor.NONE) { raw ->
        when (raw) {
            AMBIENT_COLOR_NONE -> NCarAmbientColor.NONE
            AMBIENT_COLOR_TURQUOISE -> NCarAmbientColor.TURQUOISE
            AMBIENT_COLOR_ORANGE -> NCarAmbientColor.ORANGE
            AMBIENT_COLOR_YELLOW -> NCarAmbientColor.YELLOW
            AMBIENT_COLOR_PURPLE -> NCarAmbientColor.PURPLE
            AMBIENT_COLOR_RED -> NCarAmbientColor.RED
            AMBIENT_COLOR_BLUE -> NCarAmbientColor.BLUE
            AMBIENT_COLOR_GREEN -> NCarAmbientColor.GREEN
            AMBIENT_COLOR_WHITE -> NCarAmbientColor.WHITE
            else -> NCarAmbientColor.NONE
        }
    }

    override val ambientLightControl = NVhalProperty.intOutput<NCarAmbientColor>(NVhalKey.VENDOR_AMBIENT_LIGHT_WRITE) { ambientColor ->
        when (ambientColor) {
            NCarAmbientColor.NONE -> AMBIENT_COLOR_NONE
            NCarAmbientColor.TURQUOISE -> AMBIENT_COLOR_TURQUOISE
            NCarAmbientColor.ORANGE -> AMBIENT_COLOR_ORANGE
            NCarAmbientColor.YELLOW -> AMBIENT_COLOR_YELLOW
            NCarAmbientColor.PURPLE -> AMBIENT_COLOR_PURPLE
            NCarAmbientColor.RED -> AMBIENT_COLOR_RED
            NCarAmbientColor.BLUE -> AMBIENT_COLOR_BLUE
            NCarAmbientColor.GREEN -> AMBIENT_COLOR_GREEN
            NCarAmbientColor.WHITE -> AMBIENT_COLOR_WHITE
        }
    }
}

