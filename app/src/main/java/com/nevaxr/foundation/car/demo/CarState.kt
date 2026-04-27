package com.nevaxr.foundation.car.demo

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import com.nevaxr.foundation.car.NCar
import com.nevaxr.foundation.car.NCarGear
import com.nevaxr.foundation.car.NCarSpecTogg
import com.nevaxr.foundation.car.NCarWindowState
import com.nevaxr.foundation.car.NSensorRate
import com.nevaxr.foundation.car.UnitSpeed
import com.nevaxr.foundation.car.device.NCarDoorState

@Stable
class CarState(private val car: NCar<NCarSpecTogg, CarState>) {
  val speed by car.stateOf(car.spec.speed, NSensorRate.Fastest)
  val gear by car.stateOf(car.spec.gear)
  val drivingMode by car.stateOf(car.spec.drivingMode)
  val evChargingRate by car.stateOf(car.spec.evChargingRate)
  val hvacStatus by car.stateOf(car.spec.hvacStatus)
  val hvacDualStatus by car.stateOf(car.spec.hvacDualStatus)
  val hvacMaxStatus by car.stateOf(car.spec.hvacMaxStatus)
  val hvacFanSpeed by car.stateOf(car.spec.hvacFanSpeed)
  val hvacPassengerSpeed by car.stateOf(car.spec.hvacPassengerSpeed)
  val hvacTemperature by car.stateOf(car.spec.hvacTemperature)
  val hvacInteriorTemperature by car.stateOf(car.spec.hvacInteriorTemperature)
  val hvacExteriorTemperature by car.stateOf(car.spec.hvacExteriorTemperature)
  val batteryCapacity by car.stateOf(car.spec.batteryCapacity)
  val battery by car.stateOf(car.spec.battery)
  val engine by car.stateOf(car.spec.engine)
  val acceleration by car.stateOf(car.spec.acceleration)
  val seatOccupancy by car.stateOf(car.spec.seatOccupancy)
  val steeringWheelAngle by car.stateOf(car.spec.steeringWheelAngle)
  val doorState by car.stateOf(car.spec.doorState)
  val trunkState by car.stateOf(car.spec.trunkState)
  val trunkAngle by car.stateOf(car.spec.trunkAngle)
  val frunkState by car.stateOf(car.spec.frunkState)
  val frunkAngle by car.stateOf(car.spec.frunkAngle)
  val windowState by car.stateOf(car.spec.windowState)
  val ambientLight by car.stateOf(car.spec.ambientLight)

  suspend fun setAmbientLight(hex: String) {
    car.setProperty(car.spec.ambientLightControl, hex)
  }

  val demoSpeedMaxKmh: Float
    get() = car.spec.speedRange.unit.convert(
      car.spec.speedRange.endInclusive,
      UnitSpeed.kilometersPerHour
    )

  suspend fun setDemoSpeedKmh(kmh: Float) {
    val speedMps = UnitSpeed.kilometersPerHour.convert(kmh, UnitSpeed.metersPerSecond)
    car.setProperty(car.spec.demoSpeedControl, speedMps)
  }

  suspend fun setDemoGear(gear: NCarGear) {
    car.setProperty(car.spec.demoGearControl, gear)
  }

  suspend fun setDemoDoorState(state: NCarDoorState) {
    car.setProperty(car.spec.demoDoorControl, state)
  }

  suspend fun setDemoWindowState(state: NCarWindowState) {
    car.setProperty(car.spec.demoWindowControl, state)
  }

  private val deviceId by car.reader(car.spec.deviceId)
  private val brand by car.reader(car.spec.brand)
  private val model by car.reader(car.spec.model)

  val deviceInfo
    get() = deviceId?.let { deviceId ->
      CarInfo(
        deviceId,
        brand,
        model
      )
    }
}

data class CarInfo(
  val id: String,
  val brand: String?,
  val model: String?,
)
