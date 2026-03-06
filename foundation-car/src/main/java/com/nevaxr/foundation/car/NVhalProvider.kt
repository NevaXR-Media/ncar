package com.nevaxr.foundation.car

import android.car.Car
import android.car.hardware.CarPropertyValue
import android.car.hardware.property.CarPropertyManager
import android.content.Context
import kotlinx.coroutines.*
import timber.log.Timber

/**
 *  Car property provider for Android auto VHAL car api
 *  Subscriptions are cold, until this provider is started. Each unique property id is subscribed
 *  only once. But they may have more than one listener.
 *
 *  @param forceInitialRead Runs `readProperty` first when VHAL subscription is added, this is a
 *                          workaround for some car vendors doesn't supply the current value for
 *                          subscriptions with ON_CHANGE sensor read rate (such as togg)
 */
class NVhalProvider(context: Context, private val scope: CoroutineScope, val forceInitialRead: Boolean) :
  NCarPropertyProvider {

  private val car = Car.createCar(context)
  private val propertyManager = car.getCarManager(Car.PROPERTY_SERVICE) as CarPropertyManager
  private val subscriptions = mutableMapOf<NVhalKey, VhalPropertySubscription<*>>()

  private var isRunning = false

  override fun release() {
    stop()
    car.disconnect()
    subscriptions.clear()
  }

  /**
   * Register a new subscription for this specific NVhalKey (property id) if not exists and add
   * `handler` as a listener
   *
   * If one property subscription has more than one listener with different rates, fastest rate is
   * selected for the actual car api subscription.
   */
  fun <Raw> subscribe(key: NVhalKey, rate: NSensorRate, handler: suspend (CarPropertyValue<Raw>) -> Unit) {
    val subscription = subscriptions.getOrPut(key) {
      Timber.d("Initializing a new subscription for ${key.name} (${key.id})")
      VhalPropertySubscription<Raw>(key, scope, forceInitialRead)
    } as VhalPropertySubscription<Raw>

    Timber.d("Registering a new property subscription handler for: $key")
    subscription.addHandler(rate) { propValueResult ->
      Timber.d("propValueResult ${propValueResult.getOrNull()}")
      propValueResult.onSuccess { property ->
        val status = runCatching { property.propertyStatus }.getOrNull()?.toString() ?: "N/A"
        Timber.d("New value ${key.name} (id=${property.propertyId}, areaId=${property.areaId}, status=$status): ${property.value as Raw}")
        handler(property)
      }.onFailure { err ->
        Timber.e(err, "New error ${key.name} (id=${key.id})")
      }
    }
  }

  /**
   * Reads the given property from the actual VHAL car object
   */
  suspend fun <Raw> getProperty(key: NVhalKey) = withContext(Dispatchers.IO) {
    propertyManager.getProperty<Raw>(key.id, key.areaId).also { property ->
      val status = runCatching { property.propertyStatus }.getOrNull()?.toString() ?: "N/A"
      Timber.d("Property read ${key.name} (id=${property.propertyId}, areaId=${property.areaId}, status=$status): ${property.value as Raw}")
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

  /**
   * Starts registered subscriptions on the actual VHAL car instance. This provider does not
   * produce values until it is started.
   */
  override suspend fun start() {
    if (!isRunning) {
      Timber.d("Starting a new service ${subscriptions.values.map { "${it.key.id} ${it.key.areaId} ${it.key.name}" }}")
      subscriptions.values.map { scope.async { it.start(propertyManager) } }.awaitAll()
      isRunning = true
    }
  }

  /**
   * Stops registered subscriptions on the actual VHAL car instance (unsubscribes).
   */
  override fun stop() {
    if (isRunning) {
      subscriptions.values.forEach { it.stop(propertyManager) }
      isRunning = false
    }
  }
}

/**
 * A subscription object represents the subscription relation between a VHAL property and the car
 * instance. It doesn't subscribe to the actual car instance until it is started. Same subscription
 * may trigger more than one listener (handler).
 *
 * @param forceInitialRead Runs `readProperty` first when VHAL subscription is started, this is a
 *                          workaround for some car vendors doesn't supply the current value for
 *                          subscriptions with ON_CHANGE sensor read rate (such as TOGG)
 */
private class VhalPropertySubscription<Raw>(
  val key: NVhalKey,
  val scope: CoroutineScope,
  val forceInitialRead: Boolean
) {

  private var rate: NSensorRate = NSensorRate.OnChange
  private val handlers = mutableListOf<suspend (Result<CarPropertyValue<Raw>>) -> Unit>()
  private val callback = object : CarPropertyManager.CarPropertyEventCallback {
    override fun onChangeEvent(propValue: CarPropertyValue<*>?) {
      Timber.d("onChangeEvent propValue: $propValue")
      propValue?.let { emit(it as CarPropertyValue<Raw>) }
    }

    override fun onErrorEvent(p0: Int, p1: Int) {
      Timber.d("onErrorEvent p0 $p0 p1 $p1")
      scope.launch {
        handlers.forEach {
          it.invoke(Result.failure(Exception("VHAL Property callback error: id=${key.id}, areaId=${key.areaId}")))
        }
      }
    }
  }

  private fun emit(propertyValue: CarPropertyValue<Raw>) {
    Timber.d("emit is $propertyValue")
    scope.launch {
      handlers.forEach { it.invoke(Result.success(propertyValue)) }
    }
  }

  /**
   * Add a new value handler for this property subscription. Subscription's actual update rate
   * will be determined by the fastest added handler, therefore actual sensor update rate may be
   * faster than requested rate.
   */
  fun addHandler(rate: NSensorRate, block: suspend (Result<CarPropertyValue<Raw>>) -> Unit) {
    handlers.add(block)
    if (rate > this.rate) {
      this.rate = rate
    }
  }

  /**
   * Subscribe to actual VHAL car property, and start listening to property value events
   */
  suspend fun start(carPropertyManager: CarPropertyManager) = withContext(Dispatchers.Main) {
    try {
      val name = key.name
      val id = key.id
      val areaId = key.areaId
      Timber.d("Subscribing to %s (%d)", name, id)
      runCatching {
        if (forceInitialRead) {
          val initialValue = carPropertyManager.getProperty<Raw>(key.id, key.areaId)
          emit(initialValue)
        }
      }

      val result = carPropertyManager.registerCallback(
        callback,
        key.id,
        rate.raw,
      )
      Timber.d("Registered carPropertyManager $key $result")
    } catch (t: Throwable) {
      Timber.e(t, "Subscription failed to %s (%d)", key.name, key.id)
    }
  }

  /**
   * Unsubscribes from the actual VHAL car object
   */
  fun stop(carPropertyManager: CarPropertyManager) {
    try {
      carPropertyManager.unregisterCallback(callback, key.id)
      Timber.d("Unsubscribed from ${key.name} (${key.id})")
    } catch (err: Throwable) {
      Timber.e(err, "Unsubscribing failed from ${key.name} (${key.id})")
    }
  }
}
