package com.nevaxr.foundation.car

import com.nevaxr.foundation.car.device.NCarDoorState
import timber.log.Timber

const val NCAR_TOGG_BRAND = "Togg"

object NCarSpecTogg : NCarSpecGeneric {
  private object VendorKeys {

    // TODO(umur): These are TOGG specific vendor properties???
    private val VENDOR_PERMISSION = setOf("android.car.permission.CAR_VENDOR_EXTENSION")
    val DRIVE_MODE_PROPERTY = NVhalKey(557842693, 0, "Vendor Drive Mode Property", VENDOR_PERMISSION)
    val CABIN_CURRENT_TEMP_DEG_PROPERTY =
      NVhalKey(559939846, 0, "Vendor Cabin Current Temp Deg Property", VENDOR_PERMISSION)
    val SOC_BATTERY_LEVEL_PROPERTY = NVhalKey(559939847, 0, "Vendor Soc Battery Level Property", VENDOR_PERMISSION)
    val CRUISE_CONTROL_STATUS = NVhalKey(557842696, 0, "Vendor Cruise Control Status", VENDOR_PERMISSION)
    val AMBIENT_LIGHT_READ =
      NVhalKey(557842697, 0, "Vendor Ambient Light Property", VENDOR_PERMISSION) // VENDOR_AMBIENT_LIGH
    val AMBIENT_LIGHT_WRITE =
      NVhalKey(557842961, 0, "Vendor Ambient Light Property", VENDOR_PERMISSION) // VENDOR_AMBIENT_LIGHT_REQ
  }

  override val specName = "Togg"
  override fun providers(carService: NCarServiceBase) = listOf(
    carService.propertyProviderOf(NVhalProvider::class)
  )

  override suspend fun identify(carService: NCarServiceBase): Boolean {
    val brand = brand.getProperty(carService)
    val model = model.getProperty(carService)
    Timber.d("Checking if the car is Togg, brand: ${brand ?: "null"}, model: ${model ?: "null"}")
    return brand == NCAR_TOGG_BRAND
  }

  override val deviceId = NVhalProperty.string(NVhalKey.INFO_VIN).optional().withInitial(null)
  override val model = NVhalProperty.string(NVhalKey.INFO_MODEL).optional().withInitial(null)
  override val brand = NVhalProperty.string(NVhalKey.INFO_MAKE).optional().withInitial(null)

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

  val drivingMode = NVhalProperty.int(VendorKeys.DRIVE_MODE_PROPERTY, DrivingMode.UNKNOWN) { raw: Int ->
    when (raw) {
      0 -> DrivingMode.ECO
      1 -> DrivingMode.COMFORT
      2 -> DrivingMode.SPORT
      else -> DrivingMode.UNKNOWN
    }
  }

  override val evChargingRate = NVhalProperty.measurement(
    NVhalKey.EV_BATTERY_INSTANTANEOUS_CHARGE_RATE,
    UnitPower.megawatts,
    MeasurementUnitRange(0f, 175000000f, UnitPower.megawatts),
    0f
  )
  override val hvacStatus = NVhalProperty.boolean(NVhalKey.HVAC_AC_ON)
  override val hvacDualStatus = NVhalProperty.boolean(NVhalKey.HVAC_DUAL_ON)
  override val hvacMaxStatus = NVhalProperty.boolean(NVhalKey.HVAC_MAX_AC_ON)
  override val hvacFanSpeed =
    NVhalProperty.measurement(NVhalKey.HVAC_FAN_SPEED, UnitRpm, MeasurementUnitRange(0f, 7f, UnitRpm))
  override val hvacPassengerSpeed =
    NVhalProperty.measurement(NVhalKey.HVAC_FAN_SPEED, UnitRpm, MeasurementUnitRange(0f, 7f, UnitRpm))
  override val hvacTemperature = NVhalProperty.rawReducer<Float, Array<Measurement<UnitTemperature>>>(
    NVhalKey.HVAC_TEMPERATURE_SET,
    arrayOf(
      Measurement(0f, UnitTemperature.celsius),
      Measurement(0f, UnitTemperature.celsius)
    )
  ) { state, property ->
    val index = when (property.areaId) {
      HVAC_TEMPERATURE_DRIVER_AREA_ID -> HVAC_TEMPERATURE_DRIVER_INDEX
      HVAC_TEMPERATURE_PASSENGER_AREA_ID -> HVAC_TEMPERATURE_PASSENGER_INDEX
      else -> null
    }

    index?.let {
      state.copyOf().also { updated ->
        updated[it] = Measurement(property.value, UnitTemperature.celsius)
      }
    } ?: state
  }
  override val hvacInteriorTemperature =
    NVhalProperty.measurement(VendorKeys.CABIN_CURRENT_TEMP_DEG_PROPERTY, UnitTemperature.celsius)
  override val hvacExteriorTemperature =
    NVhalProperty.measurement(NVhalKey.ENV_OUTSIDE_TEMPERATURE, UnitTemperature.celsius)
  override val batteryCapacity = NVhalProperty.measurement(
    NVhalKey.INFO_EV_BATTERY_CAPACITY,
    UnitEnergy.wattHours,
    MeasurementUnitRange(0f, 88.5f, UnitEnergy.kilowattHours)
  )
  override val battery = NVhalProperty.float(NVhalKey.EV_BATTERY_LEVEL)
  override val engine = NVhalProperty.measurement(
    NVhalKey.ENGINE_RPM,
    UnitPower.kilowatts,
    MeasurementUnitRange(0f, 15_000f, UnitPower.kilowatts)
  )
  override val acceleration = NVhalProperty.constant(0f)
  override val seatOccupancy = NVhalProperty.rawReducer<Int, Array<Boolean>>(
    NVhalKey.SEAT_OCCUPANCY,
    arrayOf(false, false, false, false)
  ) { state, property ->
    val seatIndex = when (property.areaId) {
      SEAT_FRONT_LEFT_AREA_ID -> 0
      SEAT_FRONT_RIGHT_AREA_ID -> 1
      SEAT_BACK_LEFT_AREA_ID -> 2
      SEAT_BACK_RIGHT_AREA_ID -> 3
      else -> null
    }

    seatIndex?.let { index -> state.copyOf().also { updated -> updated[index] = property.value == 1 } } ?: state
  }
  override val steeringWheelAngle = NVhalProperty.constant(Measurement(0f, UnitAngle.degrees))

  private const val HVAC_TEMPERATURE_DRIVER_AREA_ID = 49
  private const val HVAC_TEMPERATURE_PASSENGER_AREA_ID = 68
  private const val HVAC_TEMPERATURE_DRIVER_INDEX = 0
  private const val HVAC_TEMPERATURE_PASSENGER_INDEX = 1

  private const val SEAT_FRONT_LEFT_AREA_ID = 1
  private const val SEAT_FRONT_RIGHT_AREA_ID = 4
  private const val SEAT_BACK_LEFT_AREA_ID = 16
  private const val SEAT_BACK_RIGHT_AREA_ID = 64

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

  override val trunkAngle =
    NVhalProperty.rawReducer<Int, _>(NVhalKey.DOOR_POS, Measurement(0f, UnitAngle.degrees)) { state, property ->
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

  override val frunkAngle = NVhalProperty.rawReducer<Int, Measurement<UnitAngle>>(
    NVhalKey.DOOR_POS,
    Measurement(0f, UnitAngle.degrees)
  ) { state, property ->
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
  override val windowState =
    NVhalProperty.rawReducer<Float, _>(NVhalKey.WINDOW_POS, NCarWindowState()) { state, property ->
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
  override val ambientLight = NVhalProperty.int(VendorKeys.AMBIENT_LIGHT_READ, NCarAmbientColor.NONE) { raw ->
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

  override val ambientLightControl =
    NVhalProperty.intOutput<NCarAmbientColor>(VendorKeys.AMBIENT_LIGHT_WRITE) { ambientColor ->
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
