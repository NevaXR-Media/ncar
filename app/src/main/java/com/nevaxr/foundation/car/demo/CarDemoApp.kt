package com.nevaxr.foundation.car.demo

import android.app.Application
import android.content.Context
import com.nevaxr.foundation.car.NCarService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

val Application.carService get() = (this as CarDemoApp).carService
val Application.scope get() = (this as CarDemoApp).applicationScope

class CarDemoApp : Application() {
    val applicationScope by lazy { CoroutineScope(Dispatchers.Main + SupervisorJob()) }
    val carService by lazy { NCarService.buildTogg(this, applicationScope, ::CarState) }

    override fun onCreate() {
        super.onCreate()
        carService.loadCar()
    }

    override fun onTerminate() {
        super.onTerminate()
        carService.releaseCar()
    }
}