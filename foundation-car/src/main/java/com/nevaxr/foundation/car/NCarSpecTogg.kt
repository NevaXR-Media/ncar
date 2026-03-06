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
  override val hvacTemperatureAreaIds = NCarHvacTemperatureAreaIds(driver = 49, passenger = 68)
  override val seatOccupancyAreaIds = NCarQuadAreaIds(
    frontLeft = 1,
    frontRight = 4,
    backLeft = 16,
    backRight = 64
  )
  override val doorAreaIds = NCarQuadAreaIds(
    frontLeft = 1,
    frontRight = 4,
    backLeft = 16,
    backRight = 64
  )

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
  override val hvacTemperature = NVhalProperty.rawReducer<Float, NCarHvacTemperatureState>(
    NVhalKey.HVAC_TEMPERATURE_SET,
    NCarHvacTemperatureState(
      driver = Measurement(0f, UnitTemperature.celsius),
      passenger = Measurement(0f, UnitTemperature.celsius)
    )
  ) { state, property ->
    when (property.areaId) {
      hvacTemperatureAreaIds.driver -> state.copy(driver = Measurement(property.value, UnitTemperature.celsius))
      hvacTemperatureAreaIds.passenger -> state.copy(passenger = Measurement(property.value, UnitTemperature.celsius))
      else -> state
    }
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
  override val seatOccupancy = NVhalProperty.rawReducer<Int, NCarSeatOccupancyState>(
    NVhalKey.SEAT_OCCUPANCY,
    NCarSeatOccupancyState()
  ) { state, property ->
    when (property.areaId) {
      seatOccupancyAreaIds.frontLeft -> state.copy(frontLeft = property.value == 1)
      seatOccupancyAreaIds.frontRight -> state.copy(frontRight = property.value == 1)
      seatOccupancyAreaIds.backLeft -> state.copy(backLeft = property.value == 1)
      seatOccupancyAreaIds.backRight -> state.copy(backRight = property.value == 1)
      else -> state
    }
  }
  override val steeringWheelAngle = NVhalProperty.constant(Measurement(0f, UnitAngle.degrees))

  override val doorState = NVhalProperty.rawReducer<Int, NCarDoorState>(NVhalKey.DOOR_POS, NCarDoorState()) { state, property ->
    when (property.areaId) {
      doorAreaIds.frontLeft -> state.copy(frontLeft = property.value == 1)
      doorAreaIds.frontRight -> state.copy(frontRight = property.value == 1)
      doorAreaIds.backLeft -> state.copy(backLeft = property.value == 1)
      doorAreaIds.backRight -> state.copy(backRight = property.value == 1)
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
  private data class AmbientPaletteEntry(
    val order: Int,
    val color: NCarAmbientColor,
    val hex: String
  )

  private val ambientPalette = listOf(
    AmbientPaletteEntry(AMBIENT_COLOR_NONE, NCarAmbientColor.NONE, "#000000"),
    AmbientPaletteEntry(AMBIENT_COLOR_TURQUOISE, NCarAmbientColor.TURQUOISE, "#40E0D0"),
    AmbientPaletteEntry(AMBIENT_COLOR_ORANGE, NCarAmbientColor.ORANGE, "#FFA500"),
    AmbientPaletteEntry(AMBIENT_COLOR_YELLOW, NCarAmbientColor.YELLOW, "#FFFF00"),
    AmbientPaletteEntry(AMBIENT_COLOR_PURPLE, NCarAmbientColor.PURPLE, "#800080"),
    AmbientPaletteEntry(AMBIENT_COLOR_RED, NCarAmbientColor.RED, "#FF0000"),
    AmbientPaletteEntry(AMBIENT_COLOR_BLUE, NCarAmbientColor.BLUE, "#0000FF"),
    AmbientPaletteEntry(AMBIENT_COLOR_GREEN, NCarAmbientColor.GREEN, "#008000"),
    AmbientPaletteEntry(AMBIENT_COLOR_WHITE, NCarAmbientColor.WHITE, "#FFFFFF"),
  )

  override val ambientLightSupportedHexColors = ambientPalette.map { it.hex }
  override val ambientLight = NVhalProperty.int(VendorKeys.AMBIENT_LIGHT_READ, NCarAmbientColor.NONE) { raw ->
    ambientPalette.firstOrNull { it.order == raw }?.color ?: NCarAmbientColor.NONE
  }

  override val ambientLightControl =
    NVhalProperty.intOutput<String>(VendorKeys.AMBIENT_LIGHT_WRITE) { hex ->
      nearestAmbientColorOrder(hex)
    }

  private fun nearestAmbientColorOrder(hex: String): Int {
    val target = hexToRgbOrNull(hex) ?: return AMBIENT_COLOR_NONE
    return ambientPalette.minByOrNull { entry ->
      colorDistanceSquared(target, hexToRgbOrNull(entry.hex) ?: Triple(0, 0, 0))
    }?.order ?: AMBIENT_COLOR_NONE
  }

  private fun hexToRgbOrNull(hex: String): Triple<Int, Int, Int>? {
    val cleaned = hex.trim().removePrefix("#")
    val normalized = when (cleaned.length) {
      3 -> cleaned.flatMap { listOf(it, it) }.joinToString("")
      4 -> cleaned.substring(1).flatMap { listOf(it, it) }.joinToString("")
      6 -> cleaned
      8 -> cleaned.substring(2)
      else -> return null
    }

    val colorInt = normalized.toIntOrNull(16) ?: return null
    return Triple(
      (colorInt shr 16) and 0xFF,
      (colorInt shr 8) and 0xFF,
      colorInt and 0xFF
    )
  }

  private fun colorDistanceSquared(c1: Triple<Int, Int, Int>, c2: Triple<Int, Int, Int>): Int {
    val dr = c1.first - c2.first
    val dg = c1.second - c2.second
    val db = c1.third - c2.third
    return dr * dr + dg * dg + db * db
  }
}
