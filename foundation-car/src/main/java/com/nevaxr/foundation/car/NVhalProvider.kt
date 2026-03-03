package com.nevaxr.foundation.car

import android.car.hardware.CarPropertyValue
import android.car.hardware.property.CarPropertyManager
import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class NVhalProvider(context: Context, private val scope: CoroutineScope) : NCarPropertyProvider {
    private val car = android.car.Car.createCar(context)
    private val propertyManager = car.getCarManager(android.car.Car.PROPERTY_SERVICE) as CarPropertyManager
    private val subscriptions = mutableMapOf<NVhalKey, VhalPropertySubscription>()

    private var isRunning = false

    fun <Raw> subscribe(key: NVhalKey, rate: NSensorRate, handler: suspend (CarPropertyValue<Raw>) -> Unit) {
        val subscription = subscriptions.getOrPut(key) {
            Timber.d("Initializing a new subscription for ${key.name} (${key.id})")
            VhalPropertySubscription(key, scope)
        }

        Timber.d("Registering a new property subscription handler for: $key")
        subscription.addHandler(rate) { propValueResult ->
            propValueResult.onSuccess { property ->
                Timber.d("New value ${key.name} (id=${property.propertyId}, areaId=${property.areaId}, status=${property.propertyStatus}): ${property.value as Raw}")
                handler(property as CarPropertyValue<Raw>)
            }.onFailure { err ->
                Timber.e(err, "New error ${key.name} (id=${key.id})")
            }
        }
    }

    suspend fun <Raw> getProperty(key: NVhalKey) = withContext(Dispatchers.IO) {
        propertyManager.getProperty<Raw>(key.id, key.areaId).also { property ->
            Timber.d("Property read ${key.name} (id=${property.propertyId}, areaId=${property.areaId}, status=${property.propertyStatus}): ${property.value as Raw}")
        }
    }

    suspend fun setIntProperty(key: NVhalKey, value: Int) = withContext(Dispatchers.IO) {
        propertyManager.setIntProperty(key.id, key.areaId, value).also {
            Timber.d("Property set ${key.name} (id=${key.id}, areaId=${key.areaId}): $value")
        }
    }

    suspend fun setFloatProperty(key: NVhalKey, value: Float) = withContext(Dispatchers.IO) {
        propertyManager.setFloatProperty(key.id, key.areaId, value).also {
            Timber.d("Property set ${key.name} (id=${key.id}, areaId=${key.areaId}): $value")
        }
    }

    override fun start() {
        if (!isRunning) {
            subscriptions.values.forEach { it.start(propertyManager) }
            isRunning = true
        }
    }

    override fun stop() {
        if (isRunning) {
            subscriptions.values.forEach { it.stop(propertyManager) }
            isRunning = false
        }
    }
}

private class VhalPropertySubscription(val key: NVhalKey, val scope: CoroutineScope) {
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
                    it.invoke(Result.failure(Exception("VHAL Property callback error: id=${key.id}, areaId=${key.areaId}")))
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
        Timber.d("Subscribing to ${key.name} (${key.id})")
        try {
            carPropertyManager.subscribePropertyEvents(
                key.id,
                key.areaId,
                rate.raw,
                callback
            )
        } catch (t: Throwable) {
            Timber.e(t, "Subscription failed to ${key.name} (${key.id})")
        }
    }

    fun stop(carPropertyManager: CarPropertyManager) {
        Timber.d("Unsubscribing from ${key.name} (${key.id})")
        runCatching {
            carPropertyManager.unsubscribePropertyEvents(key.id, callback)
        }
    }
}
