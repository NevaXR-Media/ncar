package com.nevaxr.device

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

fun Context.getGyroscopeFlow(): Flow<SensorEvent> = callbackFlow {

    val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
    val gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

    if (gyroscope == null) {
        close()
        return@callbackFlow
    }

    val sensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            event?.let {
                trySend(it)
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
            // Handle accuracy changes if needed
        }
    }

    sensorManager.registerListener(
        sensorEventListener,
        gyroscope,
        SensorManager.SENSOR_DELAY_NORMAL
    )

    awaitClose {
        sensorManager.unregisterListener(sensorEventListener)
    }
}

data class GyroscopeData(
    val x: Float,
    val y: Float,
    val z: Float,
)