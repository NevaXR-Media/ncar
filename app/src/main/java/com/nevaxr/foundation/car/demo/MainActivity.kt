package com.nevaxr.foundation.car.demo

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.nevaxr.foundation.car.*
import com.nevaxr.foundation.car.demo.ui.theme.CarDemoTheme
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.math.roundToInt

typealias CarSpec = NCarSpecTogg

class MainActivity : ComponentActivity() {

  val carService get() = application.carService

  lateinit var permissionDeferred: CompletableDeferred<Unit>

  val requestPermissionLauncher = registerForActivityResult(
    ActivityResultContracts.RequestMultiplePermissions()
  ) { results ->
    if (results.all { it.value }) {
      permissionDeferred.complete(Unit)
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    Timber.tag("Lifecycle").d("onCreate")
    Timber.plant(Timber.DebugTree())

    permissionDeferred = CompletableDeferred()
    Timber.tag("Lifecycle").d("MainActivity.onCreate")

    lifecycleScope.launch {
      carService.awaitReady().onSuccess(::onCarCreated)
    }

    enableEdgeToEdge()
    setContent {
      CarDemoTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
          val carServiceState = carService.state.collectAsState()
          when (val state = carServiceState.value) {
            is NCarService.Loading -> CircularProgressIndicator(Modifier.padding(innerPadding))
            is NCarService.Unavailable -> Text("Car is not available", Modifier.padding(innerPadding))
            is NCarService.Ready<CarSpec, CarState> -> CarInfo(state.car.state, Modifier.padding(innerPadding))
          }
        }
      }
    }
  }

  override fun onResume() {
    super.onResume()
    Timber.tag("Lifecycle").d("MainActivity.onResume")
    lifecycleScope.launch {
      permissionDeferred.await()
      carService.start()
    }
  }

  override fun onStop() {
    super.onStop()
    Timber.tag("Lifecycle").d("MainActivity.onStop")
    carService.stop()
  }

  var carPermissionsRequested = false
  fun onCarCreated(car: NCar<CarSpec, CarState>) {
    if (carPermissionsRequested || permissionDeferred.isCompleted) {
      return
    }
    val missingPermissions = car.requiredPermissions.filter {
      ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
    }
    if (missingPermissions.isNotEmpty()) {
      Timber.tag("Lifecycle").d("Requesting permissions for car properties: %s", missingPermissions.toList().toString())
      carPermissionsRequested = true
      requestPermissionLauncher.launch(missingPermissions.toTypedArray())
    } else {
      permissionDeferred.complete(Unit)
    }
  }

  @Composable
  fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
      text = "Hello $name!",
      modifier = modifier
    )
  }

  @Preview(showBackground = true)
  @Composable
  fun GreetingPreview() {
    CarDemoTheme {
      Greeting("Android")
    }
  }

  @Composable
  fun CarInfo(state: CarState, modifier: Modifier = Modifier) {
    val context = LocalContext.current

    Column(modifier.verticalScroll(rememberScrollState()).safeContentPadding()) {
      Text("Speed: ${state.speed.format(context, UnitSpeed.kilometersPerHour)}")
      Text("Speed normalized: ${(state.speed.normalized() * 100).roundToInt()}%")
      Text("Gear: ${state.gear.name}")
      state.deviceInfo?.let { info ->
        Text("Device id: ${info.id}")
        Text("Device model: ${info.model}")
        Text("Device brand: ${info.brand}")
      }

      Text("drivingMode: ${state.drivingMode}")
      Text("evChargingRate: ${state.evChargingRate.format()}")
      Text("hvacStatus: ${state.hvacStatus}")
      Text("hvacDualStatus: ${state.hvacDualStatus}")
      Text("hvacMaxStatus: ${state.hvacMaxStatus}")
      Text("hvacFanSpeed: ${state.hvacFanSpeed.format()}")
      Text("hvacPassengerSpeed: ${state.hvacPassengerSpeed.format()}")
      Text("hvacTemperature: driver=${state.hvacTemperature.driver.format(context)}, passenger=${state.hvacTemperature.passenger.format(context)}")
      Text("hvacInteriorTemperature: ${state.hvacInteriorTemperature.format()}")
      Text("hvacExteriorTemperature: ${state.hvacExteriorTemperature.format()}")
      Text("batteryCapacity: ${state.batteryCapacity.format(UnitEnergy.kilowattHours)}")
      Text("battery: ${state.battery}")
      Text("engine: ${state.engine.format()}")
      Text("acceleration: ${state.acceleration}")
      Text("seatOccupancy: ${state.seatOccupancy}")
      Text("steeringWheelAngle: ${state.steeringWheelAngle.format()}")
      Text("doorState: ${state.doorState}")
      Text("trunkState: ${state.trunkState}")
      Text("trunkAngle: ${state.trunkAngle.format()}")
      Text("frunkState: ${state.frunkState}")
      Text("frunkAngle: ${state.frunkAngle.format()}")
      Text("windowState: ${state.windowState}")
      Text("ambientLight: ${state.ambientLight}")
    }
  }
}
