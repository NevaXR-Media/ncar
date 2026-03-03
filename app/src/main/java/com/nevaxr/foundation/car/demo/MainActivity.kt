package com.nevaxr.foundation.car.demo

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.nevaxr.foundation.car.NCar
import com.nevaxr.foundation.car.NCarService
import com.nevaxr.foundation.car.NCarSpecTogg
import com.nevaxr.foundation.car.UnitSpeed
import com.nevaxr.foundation.car.demo.ui.theme.CarDemoTheme
import com.nevaxr.foundation.car.format
import com.nevaxr.foundation.car.normalized
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.math.roundToInt

typealias CarSpec = NCarSpecTogg

class MainActivity : ComponentActivity() {
    lateinit var carService: NCarService<CarSpec, CarState>
    lateinit var permissionDeferred: CompletableDeferred<Unit>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Timber.plant(Timber.DebugTree())

        permissionDeferred = CompletableDeferred()
        carService = NCarService.buildTogg(this, lifecycleScope, ::CarState)

        lifecycleScope.launch {
            carService.loadCar()
            carService.awaitReady().getOrNull()?.let(::onCarCreated)
        }

        lifecycleScope.launch {
            permissionDeferred.await()

            repeatOnLifecycle(Lifecycle.State.STARTED) {
                Log.d("Lifecycle", "CarService start listening")
                carService.start()
            }

            Log.d("Lifecycle", "CarService stop listening")
            carService.stop()
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

    fun onCarCreated(car: NCar<CarSpec, CarState>) {
        val permissions = car.requiredPermissions.toTypedArray()
        if (permissions.isNotEmpty()) {
            Log.d("Car", "Car instance requires permissions: ${permissions.toList()}")
            requestPermissions(permissions, 1)
        } else {
            Log.w("Car", "Car instance's required permissions are empty")
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String?>,
        grantResults: IntArray,
        deviceId: Int
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId)
        permissions.forEachIndexed { index, permission ->
            Log.d("Permission", "Permission $permission grant result: ${grantResults[index]}")
        }

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
        Text("hvacTemperature: ${state.hvacTemperature.format()}")
        Text("hvacInteriorTemperature: ${state.hvacInteriorTemperature.format()}")
        Text("hvacExteriorTemperature: ${state.hvacExteriorTemperature.format()}")
        Text("batteryCapacity: ${state.batteryCapacity.format()}")
        Text("battery: ${state.battery}")
        Text("engine: ${state.engine.format()}")
        Text("acceleration: ${state.acceleration}")
        Text("seatOccupancy: ${state.seatOccupancy.toList()}")
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
