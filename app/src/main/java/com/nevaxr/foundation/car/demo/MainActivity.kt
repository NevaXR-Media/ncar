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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.nevaxr.device.Device
import com.nevaxr.foundation.car.CarService
import com.nevaxr.foundation.car.GenericCarSpec
import com.nevaxr.foundation.car.NCarPermission
import com.nevaxr.foundation.car.NCarService
import com.nevaxr.foundation.car.ToggSpec
import com.nevaxr.foundation.car.UnitSpeed
import com.nevaxr.foundation.car.VhalProvider
import com.nevaxr.foundation.car.convert
import com.nevaxr.foundation.car.demo.ui.theme.CarDemoTheme
import com.nevaxr.foundation.car.format
import com.nevaxr.foundation.car.normalized
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.launch
import kotlin.Unit
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    lateinit var carService: CarService<GenericCarSpec>
    lateinit var permissionDeferred: CompletableDeferred<Unit>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        carService = CarService.Builder<GenericCarSpec>()
            .addProvider(VhalProvider())
            .addCarSpec(ToggSpec)
            .build()

        val carState = CarState(carService)

        permissionDeferred = CompletableDeferred()
        requestPermissions(
            arrayOf(
                NCarPermission.SPEED.permission,
                NCarPermission.IDENTIFICATION.permission,
                NCarPermission.CAR_INFO.permission,
            ),
            1
        )

        lifecycleScope.launch {
            permissionDeferred.await()

            repeatOnLifecycle(Lifecycle.State.STARTED) {
                Log.wtf("Lifecycle", "CarService start listening")
                carService.startListening()
            }

            Log.wtf("Lifecycle", "CarService stop listening")
            carService.stopListening()
        }

        enableEdgeToEdge()
        setContent {
            CarDemoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val context = LocalContext.current
                    val speed by carState.speed.collectAsState()
                    val gear by carState.gear.collectAsState()
                    var deviceInfo by remember { mutableStateOf<Device?>(null) }

                    LaunchedEffect(Unit) {
                        permissionDeferred.await()
                        deviceInfo = runCatching { carState.deviceInfo() }.getOrNull()
                    }

                    Column(Modifier.padding(innerPadding).safeContentPadding()) {
                        Text("Speed: ${speed.format(context, UnitSpeed.kilometersPerHour)}")
                        Text("Speed normalized: ${(speed.normalized() * 100).roundToInt()}%")
                        Text("Gear: ${gear.name}")
                        deviceInfo?.let { deviceInfo ->
                            Text("Device: $deviceInfo")
                        }
                    }
                }
            }
        }
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
