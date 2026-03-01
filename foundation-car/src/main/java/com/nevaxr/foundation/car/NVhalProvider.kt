package com.nevaxr.foundation.car

import android.car.hardware.CarPropertyValue
import android.car.hardware.property.CarPropertyManager
import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NVhalProvider(context: Context, private val scope: CoroutineScope) : PropertyProvider {
    private val car = android.car.Car.createCar(context)
    private val propertyManager = car.getCarManager(android.car.Car.PROPERTY_SERVICE) as CarPropertyManager

    private val subscriptions = mutableMapOf<Int, VhalPropertySubscription>()

    fun <Raw> subscribe(id: Int, rate: NSensorRate, handler: suspend (Raw) -> Unit) {
        val subscription = subscriptions.getOrPut(id) { VhalPropertySubscription(id, scope) }
        subscription.addHandler(rate) { propValueResult ->
            propValueResult.onSuccess { handler(it.value as Raw) }
        }
    }

    suspend fun <Raw> getProperty(id: Int, areaId: Int) = withContext(Dispatchers.IO) {
        propertyManager.getProperty<Raw>(id, areaId)
    }

    override fun start() {
        subscriptions.values.forEach { it.start(propertyManager) }
    }

    override fun stop() {
        subscriptions.values.forEach { it.stop(propertyManager) }
    }
}

private class VhalPropertySubscription(val id: Int, val scope: CoroutineScope) {
    var rate: NSensorRate = NSensorRate.OnChange
    val handlers = mutableListOf<suspend (Result<CarPropertyValue<*>>) -> Unit>()
    val callback = object : CarPropertyManager.CarPropertyEventCallback {
        override fun onChangeEvent(propValue: CarPropertyValue<*>?) {
            propValue?.let { propValue ->
                scope.launch {
                    handlers.forEach { it.invoke(Result.success(propValue)) }
                }
            }
        }
        override fun onErrorEvent(p0: Int, p1: Int) {
            scope.launch {
                handlers.forEach {
                    it.invoke(Result.failure(Exception("VHAL Property callback error: id=$id")))
                }
            }
        }
    }

    fun addHandler(rate: NSensorRate, block: suspend (Result<CarPropertyValue<*>>) -> Unit) {
        handlers.add(block)
        if (rate > this.rate) {
            this.rate = rate
        }
    }

    fun start(carPropertyManager: CarPropertyManager) {
        carPropertyManager.subscribePropertyEvents(
            id,
            rate.raw,
            callback
        )
    }

    fun stop(carPropertyManager: CarPropertyManager) {
        carPropertyManager.unsubscribePropertyEvents(id, callback)
    }
}

data class VhalProperty<Raw, T>(val id: Int, val transform: (Raw) -> T) : CarProperty<T> {
    override fun subscribe(carService: CarServiceBase): StateFlow<T> {
        val provider = carService.propertyProviderOf(VhalProvider::class)
        return provider.subscribe<Raw>(id) { raw ->
            mutableFlow.emit(transform(raw))
        }.also(carService.subscriptions::add)
    }

    override suspend fun getProperty(carService: CarServiceBase): T {
        val provider = carService.propertyProviderOf(VhalProvider::class)
        val rawValue = provider.getProperty<Raw>(id)
        return transform(rawValue)
    }

    fun <U> map(block: (T) -> U) = VhalProperty<Raw, U>(id) { block(transform(it)) }
    fun optional() = VhalProperty<Raw?, T?>(id) { it?.let(transform) }

    companion object {
        private fun <T> notrasnform(value: T): T = value
        fun float(id: Int) = VhalProperty<Float, Float>(id, ::notrasnform)
        fun <T> float(id: Int, transform: (Float) -> T) = VhalProperty(id, transform)
        fun string(id: Int) = VhalProperty<String, String>(id, ::notrasnform)
        fun <T> string(id: Int, transform: (String) -> T) = VhalProperty(id, transform)
        fun <BaseUnit: MeasurementUnit> measurement(id: Int, unit: BaseUnit) = VhalProperty(id) { raw: Float -> Measurement(raw, unit) }
        fun <BaseUnit: MeasurementUnit> measurement(id: Int, unit: BaseUnit, range: MeasurementUnitRange<BaseUnit>) = VhalProperty(id) { raw: Float -> MeasurementRanged(raw, unit, range) }
    }
}
