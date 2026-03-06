package com.nevaxr.foundation.car.demo

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.nevaxr.foundation.car.NCar
import com.nevaxr.foundation.car.NCarService
import com.nevaxr.foundation.car.NCarSpecTogg
import com.nevaxr.foundation.car.demo.ui.theme.CarDemoTheme
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.launch
import timber.log.Timber

typealias CarSpec = NCarSpecTogg

class MainActivity : ComponentActivity() {

  val carService get() = application.carService

  lateinit var permissionDeferred: CompletableDeferred<Unit>

  val requestPermissionLauncher = registerForActivityResult(
    ActivityResultContracts.RequestMultiplePermissions()
  ) { results ->
    val deniedPermissions = results.filterValues { granted -> !granted }.keys
    if (deniedPermissions.isNotEmpty()) {
      Timber.tag("Lifecycle").w(
        "Some car permissions denied; continuing with available providers: %s",
        deniedPermissions.toList().toString()
      )
    }
    if (!permissionDeferred.isCompleted) {
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
            is NCarService.Ready<CarSpec, CarState> -> CarDashboardScreen(
              state = state.car.state,
              modifier = Modifier.padding(innerPadding),
            )
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
}
