package com.nevaxr.foundation.car.demo

import android.os.Bundle
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import com.nevaxr.foundation.car.NCarService
import com.nevaxr.foundation.car.demo.ui.theme.CarDemoTheme

class MainActivity : ComponentActivity() {
    lateinit var carService: NCarService
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        carService = NCarService(this, lifecycleScope)
        val carState = CarState(carService)

        enableEdgeToEdge()
        setContent {
            CarDemoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val speed by carState.speed.collectAsState()
                    val gear by carState.gear.collectAsState()

                    Column(Modifier.padding(innerPadding).safeContentPadding()) {
                        Text("Speed: ${speed.value} ${stringResource(speed.unit.displayNameRes)}")
                        Text("Gear: ${gear.name}")
                    }
                }
            }
        }
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