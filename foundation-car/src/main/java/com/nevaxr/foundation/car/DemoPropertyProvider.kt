package com.nevaxr.foundation.car

import com.nevaxr.foundation.car.device.NCarDoorState

class DemoPropertyProvider : NCarPropertyProvider {

  private var isRunning = false

  private var speedMetersPerSecond = 0f
  private var gear = NCarGear.Park
  private var doorState = NCarDoorState()
  private var windowState = NCarWindowState()

  private val speedHandlers = mutableSetOf<suspend (Float) -> Unit>()
  private val gearHandlers = mutableSetOf<suspend (NCarGear) -> Unit>()
  private val doorHandlers = mutableSetOf<suspend (NCarDoorState) -> Unit>()
  private val windowHandlers = mutableSetOf<suspend (NCarWindowState) -> Unit>()

  fun currentSpeedMetersPerSecond(): Float = speedMetersPerSecond
  fun currentGear(): NCarGear = gear
  fun currentDoorState(): NCarDoorState = doorState
  fun currentWindowState(): NCarWindowState = windowState

  fun subscribeSpeed(handler: suspend (Float) -> Unit) {
    speedHandlers += handler
  }

  fun subscribeGear(handler: suspend (NCarGear) -> Unit) {
    gearHandlers += handler
  }

  fun subscribeDoorState(handler: suspend (NCarDoorState) -> Unit) {
    doorHandlers += handler
  }

  fun subscribeWindowState(handler: suspend (NCarWindowState) -> Unit) {
    windowHandlers += handler
  }

  suspend fun setSpeedMetersPerSecond(value: Float) {
    speedMetersPerSecond = value.coerceIn(MIN_SPEED_MPS, MAX_SPEED_MPS)
    speedHandlers.forEach { it(speedMetersPerSecond) }
  }

  suspend fun setGear(value: NCarGear) {
    gear = value
    gearHandlers.forEach { it(gear) }
  }

  suspend fun setDoorState(value: NCarDoorState) {
    doorState = value
    doorHandlers.forEach { it(doorState) }
  }

  suspend fun setWindowState(value: NCarWindowState) {
    windowState = value.copy(
      frontLeft = value.frontLeft.coerceIn(0f, 100f),
      frontRight = value.frontRight.coerceIn(0f, 100f),
      backLeft = value.backLeft.coerceIn(0f, 100f),
      backRight = value.backRight.coerceIn(0f, 100f),
    )
    windowHandlers.forEach { it(windowState) }
  }

  override suspend fun start() {
    if (isRunning) return
    isRunning = true

    speedHandlers.forEach { it(speedMetersPerSecond) }
    gearHandlers.forEach { it(gear) }
    doorHandlers.forEach { it(doorState) }
    windowHandlers.forEach { it(windowState) }
  }

  override fun stop() {
    isRunning = false
  }

  override fun release() {
    stop()
    speedHandlers.clear()
    gearHandlers.clear()
    doorHandlers.clear()
    windowHandlers.clear()
  }

  companion object {
    private const val MIN_SPEED_MPS = 0f
    private const val MAX_SPEED_MPS = 51.3889f // 185 km/h
  }
}
