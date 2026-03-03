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
import kotlin.math.roundToInt

typealias CarSpec = NCarSpecTogg

class MainActivity : ComponentActivity() {
    lateinit var carService: NCarService<CarSpec>
    lateinit var permissionDeferred: CompletableDeferred<Unit>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        permissionDeferred = CompletableDeferred()
        carService = NCarService.buildTogg(this, lifecycleScope)

        lifecycleScope.launch {
            carService.loadCar().onSuccess { car ->
                onCarCreated(car)
            }
        }


        lifecycleScope.launch {
            permissionDeferred.await()

            repeatOnLifecycle(Lifecycle.State.STARTED) {
                Log.wtf("Lifecycle", "CarService start listening")
                carService.start()
            }

            Log.wtf("Lifecycle", "CarService stop listening")
            carService.stop()
        }

        enableEdgeToEdge()
        setContent {
            CarDemoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    when (val state = carService.state) {
                        is NCarService.Loading<CarSpec> -> CircularProgressIndicator(Modifier.padding(innerPadding))
                        is NCarService.Unavailable<CarSpec> -> Text("Car is not available", Modifier.padding(innerPadding))
                        is NCarService.Ready<CarSpec> -> CarInfo(state.car, Modifier.padding(innerPadding))
                    }
                }
            }
        }
    }

    fun onCarCreated(car: NCar<CarSpec>) {
        val permissions = car.requiredPermissions.toTypedArray()
        requestPermissions(permissions, 1)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String?>,
        grantResults: IntArray,
        deviceId: Int
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId)
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
fun CarInfo(car: NCar<CarSpec>, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val carState = remember(car) { CarState(car) }

    val speed by carState.speed.collectAsState()
    val gear by carState.gear.collectAsState()

    var deviceInfo by remember { mutableStateOf<CarInfo?>(null) }
    LaunchedEffect(Unit) {
        deviceInfo = carState.deviceInfo.await()
    }

    Column(modifier.safeContentPadding()) {
        Text("Speed: ${speed.format(context, UnitSpeed.kilometersPerHour)}")
        Text("Speed normalized: ${(speed.normalized() * 100).roundToInt()}%")
        Text("Gear: ${gear.name}")
        deviceInfo?.let { info ->
            Text("Device id: ${info.id}")
            Text("Device model: ${info.model}")
            Text("Device brand: ${info.brand}")
        }
    }
}
