package com.nevaxr.foundation.car.unity

import android.content.Context
import com.nevaxr.foundation.car.Measurement
import com.nevaxr.foundation.car.MeasurementRanged
import com.nevaxr.foundation.car.MeasurementUnit
import com.nevaxr.foundation.car.NCar
import com.nevaxr.foundation.car.NCarGear
import com.nevaxr.foundation.car.NCarHvacTemperatureState
import com.nevaxr.foundation.car.NCarSeatOccupancyState
import com.nevaxr.foundation.car.NCarService
import com.nevaxr.foundation.car.NCarSpecTogg
import com.nevaxr.foundation.car.NCarWindowState
import com.nevaxr.foundation.car.NSensorRate
import com.nevaxr.foundation.car.TruIdAuthResult
import com.nevaxr.foundation.car.UnitAngle
import com.nevaxr.foundation.car.UnitEnergy
import com.nevaxr.foundation.car.UnitPower
import com.nevaxr.foundation.car.UnitRpm
import com.nevaxr.foundation.car.UnitSpeed
import com.nevaxr.foundation.car.UnitTemperature
import com.nevaxr.foundation.car.device.NCarDoorState
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import timber.log.Timber
import java.util.concurrent.atomic.AtomicReference

fun interface UnityNCarListener {
  fun onCarData(json: String)
}

/**
 * Java-friendly bridge for Unity.
 *
 * Unity can construct this class from C# with an Android Activity or Context, call [initialize],
 * then either poll [getLatestJson] or receive JSON updates through [setUnityCallback].
 */
class UnityNCarBridge(context: Context) {

  private val appContext = context.applicationContext
  private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
    publishStatus("error", throwable.message ?: throwable::class.java.simpleName)
    Timber.e(throwable, "Unity NCar bridge failed")
  }
  private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate + exceptionHandler)
  private val service = NCarService.buildTogg(appContext, scope, ::UnityCarState)
  private val latestJson = AtomicReference(statusJson("idle"))

  @Volatile
  private var unityGameObjectName: String? = null

  @Volatile
  private var unityCallbackMethodName: String? = null

  @Volatile
  private var listener: UnityNCarListener? = null

  private var collectionJob: Job? = null

  fun initialize() {
    if (collectionJob?.isActive == true) return

    publishStatus("loading")
    service.loadCar()

    collectionJob = scope.launch {
      val carResult = service.awaitReady()
      val car = carResult.getOrElse { error ->
        publishStatus("unavailable", error.message ?: "Car service is not available")
        return@launch
      }

      service.start()
      car.state.snapshots().collect(::publishJson)
    }
  }

  fun start() {
    initialize()
  }

  fun stop() {
    collectionJob?.cancel()
    collectionJob = null
    service.stop()
    publishStatus("stopped")
  }

  fun release() {
    stop()
    service.releaseCar()
    scope.cancel()
    publishStatus("released")
  }

  fun isReady(): Boolean = service.isReady

  fun getLatestJson(): String = latestJson.get()

  fun getRequiredPermissions(): Array<String> {
    return service.car?.requiredPermissions.orEmpty().sorted().toTypedArray()
  }

  fun setUnityCallback(gameObjectName: String?, methodName: String?) {
    unityGameObjectName = gameObjectName?.takeIf { it.isNotBlank() }
    unityCallbackMethodName = methodName?.takeIf { it.isNotBlank() }
  }

  fun setListener(listener: UnityNCarListener?) {
    this.listener = listener
  }

  fun clearListener() {
    listener = null
  }

  fun setAmbientLight(hex: String) {
    scope.launch {
      runCatching {
        service.awaitReady().getOrThrow().state.setAmbientLight(hex)
      }.onFailure { error ->
        publishStatus("error", "Ambient light write failed: ${error.message}")
      }
    }
  }

  fun setDemoSpeedKmh(kmh: Float) {
    scope.launch {
      runCatching {
        service.awaitReady().getOrThrow().state.setDemoSpeedKmh(kmh)
      }.onFailure { error ->
        publishStatus("error", "Demo speed write failed: ${error.message}")
      }
    }
  }

  fun setDemoGear(gearName: String) {
    val gear = runCatching { NCarGear.valueOf(gearName) }.getOrNull() ?: return
    scope.launch {
      runCatching {
        service.awaitReady().getOrThrow().state.setDemoGear(gear)
      }.onFailure { error ->
        publishStatus("error", "Demo gear write failed: ${error.message}")
      }
    }
  }

  private fun publishStatus(status: String, message: String? = null) {
    publishJson(statusJson(status, message))
  }

  private fun publishJson(json: String) {
    latestJson.set(json)
    listener?.onCarData(json)
    sendUnityMessage(json)
  }

  private fun sendUnityMessage(json: String) {
    val gameObjectName = unityGameObjectName ?: return
    val methodName = unityCallbackMethodName ?: return

    runCatching {
      val unityPlayer = Class.forName("com.unity3d.player.UnityPlayer")
      val unitySendMessage = unityPlayer.getMethod(
        "UnitySendMessage",
        String::class.java,
        String::class.java,
        String::class.java
      )
      unitySendMessage.invoke(null, gameObjectName, methodName, json)
    }.onFailure { error ->
      Timber.e(error, "UnitySendMessage failed")
    }
  }

  companion object {
    @JvmStatic
    fun create(context: Context): UnityNCarBridge = UnityNCarBridge(context)
  }
}

private class UnityCarState(private val car: NCar<NCarSpecTogg, UnityCarState>) {
  private val deviceId = car.stateFlowOf(car.spec.deviceId)
  private val brand = car.stateFlowOf(car.spec.brand)
  private val model = car.stateFlowOf(car.spec.model)
  private val speed = car.stateFlowOf(car.spec.speed, NSensorRate.UI)
  private val gear = car.stateFlowOf(car.spec.gear)
  private val drivingMode = car.stateFlowOf(car.spec.drivingMode)
  private val evChargingRate = car.stateFlowOf(car.spec.evChargingRate)
  private val hvacStatus = car.stateFlowOf(car.spec.hvacStatus)
  private val hvacDualStatus = car.stateFlowOf(car.spec.hvacDualStatus)
  private val hvacMaxStatus = car.stateFlowOf(car.spec.hvacMaxStatus)
  private val hvacFanSpeed = car.stateFlowOf(car.spec.hvacFanSpeed)
  private val hvacPassengerSpeed = car.stateFlowOf(car.spec.hvacPassengerSpeed)
  private val hvacTemperature = car.stateFlowOf(car.spec.hvacTemperature)
  private val hvacInteriorTemperature = car.stateFlowOf(car.spec.hvacInteriorTemperature)
  private val hvacExteriorTemperature = car.stateFlowOf(car.spec.hvacExteriorTemperature)
  private val batteryCapacity = car.stateFlowOf(car.spec.batteryCapacity)
  private val battery = car.stateFlowOf(car.spec.battery)
  private val engine = car.stateFlowOf(car.spec.engine)
  private val acceleration = car.stateFlowOf(car.spec.acceleration)
  private val seatOccupancy = car.stateFlowOf(car.spec.seatOccupancy)
  private val steeringWheelAngle = car.stateFlowOf(car.spec.steeringWheelAngle)
  private val doorState = car.stateFlowOf(car.spec.doorState)
  private val trunkState = car.stateFlowOf(car.spec.trunkState)
  private val trunkAngle = car.stateFlowOf(car.spec.trunkAngle)
  private val frunkState = car.stateFlowOf(car.spec.frunkState)
  private val frunkAngle = car.stateFlowOf(car.spec.frunkAngle)
  private val windowState = car.stateFlowOf(car.spec.windowState)
  private val ambientLight = car.stateFlowOf(car.spec.ambientLight)
  private val currentAccountToken = car.stateFlowOf(car.spec.currentAccountToken)

  fun snapshots(): Flow<String> {
    val flows = listOf(
      deviceId,
      brand,
      model,
      speed,
      gear,
      drivingMode,
      evChargingRate,
      hvacStatus,
      hvacDualStatus,
      hvacMaxStatus,
      hvacFanSpeed,
      hvacPassengerSpeed,
      hvacTemperature,
      hvacInteriorTemperature,
      hvacExteriorTemperature,
      batteryCapacity,
      battery,
      engine,
      acceleration,
      seatOccupancy,
      steeringWheelAngle,
      doorState,
      trunkState,
      trunkAngle,
      frunkState,
      frunkAngle,
      windowState,
      ambientLight,
      currentAccountToken
    )

    return channelFlow {
      send(snapshotJson())
      flows.forEach { flow ->
        launch {
          flow.drop(1).collect {
            send(snapshotJson())
          }
        }
      }
      awaitClose()
    }.distinctUntilChanged()
  }

  suspend fun setAmbientLight(hex: String) {
    car.setProperty(car.spec.ambientLightControl, hex)
  }

  suspend fun setDemoSpeedKmh(kmh: Float) {
    val speedMps = UnitSpeed.kilometersPerHour.convert(kmh, UnitSpeed.metersPerSecond)
    car.setProperty(car.spec.demoSpeedControl, speedMps)
  }

  suspend fun setDemoGear(gear: NCarGear) {
    car.setProperty(car.spec.demoGearControl, gear)
  }

  private fun snapshotJson(): String {
    return JSONObject()
      .put("status", "ready")
      .put("timestampMs", System.currentTimeMillis())
      .put("specName", car.spec.specName)
      .put(
        "vehicle",
        JSONObject()
          .putNullable("deviceId", deviceId.value)
          .putNullable("brand", brand.value)
          .putNullable("model", model.value)
      )
      .put(
        "motion",
        JSONObject()
          .put("speed", measurementRangedJson(speed.value))
          .put("speedKmh", UnitSpeed.metersPerSecond.convert(speed.value.value, UnitSpeed.kilometersPerHour))
          .put("gear", gear.value.name)
          .put("drivingMode", drivingMode.value.name)
          .put("acceleration", acceleration.value)
          .put("steeringWheelAngle", measurementJson(steeringWheelAngle.value))
      )
      .put(
        "energy",
        JSONObject()
          .put("battery", battery.value)
          .put("batteryCapacity", measurementRangedJson(batteryCapacity.value))
          .put("evChargingRate", measurementRangedJson(evChargingRate.value))
          .put("engine", measurementRangedJson(engine.value))
      )
      .put(
        "climate",
        JSONObject()
          .put("hvacStatus", hvacStatus.value)
          .put("hvacDualStatus", hvacDualStatus.value)
          .put("hvacMaxStatus", hvacMaxStatus.value)
          .put("hvacFanSpeed", measurementRangedJson(hvacFanSpeed.value))
          .put("hvacPassengerSpeed", measurementRangedJson(hvacPassengerSpeed.value))
          .put("hvacTemperature", hvacTemperatureJson(hvacTemperature.value))
          .put("hvacInteriorTemperature", measurementJson(hvacInteriorTemperature.value))
          .put("hvacExteriorTemperature", measurementJson(hvacExteriorTemperature.value))
      )
      .put(
        "seats",
        JSONObject()
          .put("occupancy", seatOccupancyJson(seatOccupancy.value))
      )
      .put(
        "closures",
        JSONObject()
          .put("doors", doorStateJson(doorState.value))
          .put("trunk", openableJson(trunkState.value, trunkAngle.value))
          .put("frunk", openableJson(frunkState.value, frunkAngle.value))
      )
      .put("windows", windowStateJson(windowState.value))
      .put(
        "lighting",
        JSONObject()
          .put("ambientLight", ambientLight.value.name)
          .put("supportedAmbientLightHexColors", JSONArray(car.spec.ambientLightSupportedHexColors))
      )
      .put("account", accountJson(currentAccountToken.value))
      .put("requiredPermissions", JSONArray(car.requiredPermissions.sorted()))
      .toString()
  }
}

private fun statusJson(status: String, message: String? = null): String {
  return JSONObject()
    .put("status", status)
    .put("timestampMs", System.currentTimeMillis())
    .putNullable("message", message)
    .toString()
}

private fun JSONObject.putNullable(name: String, value: Any?): JSONObject {
  return put(name, value ?: JSONObject.NULL)
}

private fun measurementJson(measurement: Measurement<out MeasurementUnit>): JSONObject {
  return JSONObject()
    .put("value", measurement.value)
    .put("unit", unitCode(measurement.unit))
}

private fun measurementRangedJson(measurement: MeasurementRanged<out MeasurementUnit>): JSONObject {
  return JSONObject()
    .put("value", measurement.value)
    .put("unit", unitCode(measurement.unit))
    .put(
      "range",
      JSONObject()
        .put("start", measurement.range.start)
        .put("endInclusive", measurement.range.endInclusive)
        .put("unit", unitCode(measurement.range.unit))
    )
}

private fun hvacTemperatureJson(state: NCarHvacTemperatureState): JSONObject {
  return JSONObject()
    .put("driver", measurementJson(state.driver))
    .put("passenger", measurementJson(state.passenger))
}

private fun seatOccupancyJson(state: NCarSeatOccupancyState): JSONObject {
  return JSONObject()
    .put("frontLeft", state.frontLeft)
    .put("frontRight", state.frontRight)
    .put("backLeft", state.backLeft)
    .put("backRight", state.backRight)
}

private fun doorStateJson(state: NCarDoorState): JSONObject {
  return JSONObject()
    .put("frontLeft", state.frontLeft)
    .put("frontRight", state.frontRight)
    .put("backLeft", state.backLeft)
    .put("backRight", state.backRight)
}

private fun windowStateJson(state: NCarWindowState): JSONObject {
  return JSONObject()
    .put("frontLeft", state.frontLeft)
    .put("frontRight", state.frontRight)
    .put("backLeft", state.backLeft)
    .put("backRight", state.backRight)
}

private fun openableJson(open: Boolean, angle: Measurement<out MeasurementUnit>): JSONObject {
  return JSONObject()
    .put("open", open)
    .put("angle", measurementJson(angle))
}

private fun accountJson(result: TruIdAuthResult?): JSONObject {
  return when (result) {
    null -> JSONObject()
      .put("status", "pending")
      .putNullable("token", null)
      .putNullable("message", null)

    is TruIdAuthResult.Success -> JSONObject()
      .put("status", "success")
      .put("token", result.token)
      .putNullable("message", null)

    is TruIdAuthResult.Error -> JSONObject()
      .put("status", "error")
      .putNullable("token", null)
      .put("message", result.message)
  }
}

private fun unitCode(unit: MeasurementUnit): String {
  return when (unit) {
    UnitSpeed.metersPerSecond -> "metersPerSecond"
    UnitSpeed.kilometersPerHour -> "kilometersPerHour"
    UnitSpeed.milesPerHour -> "milesPerHour"
    UnitSpeed.knots -> "knots"
    UnitTemperature.celsius -> "celsius"
    UnitTemperature.kelvin -> "kelvin"
    UnitTemperature.fahrenheit -> "fahrenheit"
    UnitPower.megawatts -> "megawatts"
    UnitPower.kilowatts -> "kilowatts"
    UnitPower.watts -> "watts"
    UnitEnergy.wattHours -> "wattHours"
    UnitEnergy.kilowattHours -> "kilowattHours"
    UnitAngle.degrees -> "degrees"
    UnitAngle.radians -> "radians"
    UnitRpm -> "rpm"
    else -> unit::class.java.simpleName.ifBlank { "unknown" }
  }
}
