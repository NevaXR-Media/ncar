package com.nevaxr.foundation.car

import android.car.hardware.CarPropertyValue
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.flow.*

data class NVhalConstantProperty<T>(val value: T) : NCarStateProperty<T> {

  override val displayName = null
  override val requiredPermissions = null

  override fun subscribe(carService: NCarServiceBase, rate: NSensorRate) =
    MutableSharedFlow<T>(1).also { it.tryEmit(value) }

  override suspend fun getProperty(carService: NCarServiceBase) = value
  override fun subscribeStateFlow(carService: NCarServiceBase, rate: NSensorRate) =
    MutableStateFlow(value).asStateFlow()

  override fun subscribeState(carService: NCarServiceBase, rate: NSensorRate) = mutableStateOf(value)
}

data class NVhalIntOutputProperty<T>(val key: NVhalKey, val transform: (T) -> Int) : NCarPropertyWritable<T> {

  override val displayName = key.name
  override val requiredPermissions = key.permissions

  override suspend fun write(carService: NCarServiceBase, value: T) {
    val provider = carService.propertyProviderOf(NVhalProvider::class)
    val rawValue = transform(value)
    provider.setIntProperty(key, rawValue)
  }
}

data class NVhalFloatOutputProperty<T>(val key: NVhalKey, val transform: (T) -> Float) : NCarPropertyWritable<T> {

  override val displayName = key.name
  override val requiredPermissions = key.permissions

  override suspend fun write(carService: NCarServiceBase, value: T) {
    val provider = carService.propertyProviderOf(NVhalProvider::class)
    val rawValue = transform(value)
    provider.setFloatProperty(key, rawValue)
  }
}

data class NVhalProperty<Raw, T>(val key: NVhalKey, val transform: (CarPropertyValue<Raw>) -> T) : NCarProperty<T> {

  override val displayName: String? = key.name
  override val requiredPermissions: Set<String>? = key.permissions

  override fun subscribe(carService: NCarServiceBase, rate: NSensorRate): SharedFlow<T> {
    val provider = carService.propertyProviderOf(NVhalProvider::class)
    val mutableFlow = MutableSharedFlow<T>()
    provider.subscribe(key, rate) { propertyValue ->
      mutableFlow.emit(transform(propertyValue))
    }

    return mutableFlow.asSharedFlow()
  }

  override suspend fun getProperty(carService: NCarServiceBase): T {
    val provider = carService.propertyProviderOf(NVhalProvider::class)
    val propertyValue = provider.getProperty<Raw>(key)
    return transform(propertyValue)
  }

  fun <U> map(block: (T) -> U) = NVhalProperty(key) { block(transform(it)) }
  fun optional() =
    NVhalProperty<Raw?, T?>(key) { propertyValue -> propertyValue.value?.let { _ -> transform(propertyValue as CarPropertyValue<Raw>) } }

  fun withInitial(value: T) = NVhalStateProperty(this, value)

  companion object {

    private fun <T> notrasnform(propertyValue: CarPropertyValue<T>): T = propertyValue.value
    private fun <Raw, T> valueTransformer(transform: (Raw) -> T): (CarPropertyValue<Raw>) -> T = { transform(it.value) }
    inline fun <reified T> array(key: NVhalKey, initial: Array<T> = emptyArray()) =
      NVhalProperty<Array<T>, Array<T>>(key, { it.value }).withInitial(initial)

    inline fun <Raw, reified T> array(
      key: NVhalKey,
      initial: Array<T> = emptyArray(),
      noinline transform: (Raw) -> T,
    ) = NVhalProperty<Array<Raw>, Array<T>>(key) {
      it.value.mapNotNull {
        transform(it)
      }.toTypedArray()
    }.withInitial(initial)

    fun <T> constant(value: T) = NVhalConstantProperty(value)
    fun int(key: NVhalKey, initial: Int = 0) = NVhalProperty<Int, Int>(key, ::notrasnform).withInitial(initial)
    fun <T> int(key: NVhalKey, initial: T, transform: (Int) -> T) =
      NVhalProperty(key, valueTransformer(transform)).withInitial(initial)

    fun intOutput(key: NVhalKey) = NVhalIntOutputProperty<Int>(key) { it }
    fun <T> intOutput(key: NVhalKey, transform: (T) -> Int) = NVhalIntOutputProperty<T>(key, transform)
    fun boolean(key: NVhalKey, initial: Boolean = false) =
      NVhalProperty<Boolean, Boolean>(key, ::notrasnform).withInitial(initial)

    fun float(key: NVhalKey, initial: Float = 0f) = NVhalProperty<Float, Float>(key, ::notrasnform).withInitial(initial)
    fun float(key: NVhalKey, range: ClosedFloatingPointRange<Float>, initial: Float = 0f) =
      NVhalProperty<Float, RangedValue>(key) { RangedValue(it.value, range) }.withInitial(RangedValue(initial, range))

    fun <T> float(key: NVhalKey, transform: (Float) -> T) = NVhalProperty(key, valueTransformer(transform))
    fun floatOutput(key: NVhalKey) = NVhalFloatOutputProperty<Float>(key) { it }
    fun <T> floatOutput(key: NVhalKey, transform: (T) -> Float) = NVhalFloatOutputProperty(key, transform)
    fun string(key: NVhalKey) = NVhalProperty<String, String>(key, ::notrasnform)
    fun <T> string(key: NVhalKey, transform: (String) -> T) = NVhalProperty(key, valueTransformer(transform))
    fun <BaseUnit : MeasurementUnit> measurement(key: NVhalKey, unit: BaseUnit, initial: Float = 0f) =
      NVhalProperty(key, valueTransformer { raw: Float -> Measurement(raw, unit) }).withInitial(
        Measurement(
          initial,
          unit
        )
      )

    fun <BaseUnit : MeasurementUnit> measurement(
      key: NVhalKey,
      unit: BaseUnit,
      range: MeasurementUnitRange<BaseUnit>,
      initial: Float = 0f
    ) = NVhalProperty(
      key,
      valueTransformer { raw: Number -> MeasurementRanged(raw.toFloat(), unit, range) }).withInitial(
      MeasurementRanged(initial, unit, range)
    )

    fun <Raw, T> raw(key: NVhalKey, transform: (CarPropertyValue<Raw>) -> T) = NVhalProperty(key, transform)
    fun <Raw, T> raw(key: NVhalKey, initial: T, transform: (CarPropertyValue<Raw>) -> T) =
      NVhalProperty(key, transform).withInitial(initial)

    fun <Raw, T> rawReducer(
      key: NVhalKey,
      initial: T,
      transform: (T, CarPropertyValue<Raw>) -> T
    ): NVhalStateProperty<Raw, T> {
      var acc = initial
      return NVhalProperty(key) { raw ->
        val result = transform(acc, raw)
        acc = result
        result
      }.withInitial(acc)
    }
  }
}
