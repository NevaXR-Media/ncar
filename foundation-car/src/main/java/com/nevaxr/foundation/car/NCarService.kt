package com.nevaxr.foundation.car

import android.car.Car
import android.car.hardware.CarPropertyValue
import android.car.hardware.property.CarPropertyManager
import android.content.Context
import com.nevaxr.device.NCarPropertyId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.reflect.KProperty

class NCarService(context: Context, val coroutineScope: CoroutineScope) {
    private val carApi = Car.createCar(context)
    private val carPropertyManager = carApi.getCarManager(Car.PROPERTY_SERVICE) as CarPropertyManager
    private var subscriptions = mutableMapOf<Int, Subscription>()

    private class Subscription(val property: NCarPropertyId, val coroutineScope: CoroutineScope) {
        var rate = NSensorRate.OnChange
        val listeners: MutableList<suspend (Result<CarPropertyValue<*>>) -> Unit> = mutableListOf()
        val callback = object : CarPropertyManager.CarPropertyEventCallback {
            override fun onChangeEvent(propValue: CarPropertyValue<*>?) {
                propValue?.let { propValue ->
                    coroutineScope.launch {
                        listeners.forEach { it.invoke(Result.success(propValue)) }
                    }
                }
            }
            override fun onErrorEvent(p0: Int, p1: Int) {
                coroutineScope.launch {
                    listeners.forEach {
                        it.invoke(
                            Result.failure(
                                Exception("Property callback error: ${property.name} (${property.id})")
                            )
                        )
                    }
                }
            }
        }

        fun addListener(rate: NSensorRate, block: suspend (Result<CarPropertyValue<*>>) -> Unit) {
            listeners.add(block)
            if (rate > this.rate) {
                this.rate = rate
            }
        }

        fun start(carPropertyManager: CarPropertyManager) {
            carPropertyManager.subscribePropertyEvents(
                property.id,
                rate.raw,
                callback
            )
        }

        fun stop(carPropertyManager: CarPropertyManager) {
            carPropertyManager.unsubscribePropertyEvents(
                property.id,
                callback
            )
        }
    }

    private fun subscriptionFor(property: NCarPropertyId) = subscriptions.getOrPut(property.id) {
        Subscription(property, coroutineScope)
    }
    private fun <T> subscribeFlow(property: NCarPropertyId, rate: NSensorRate, block: suspend (CarPropertyValue<*>) -> T): SharedFlow<T> =
        MutableSharedFlow<T>(replay = 1).also { sharedFlow ->
            subscriptionFor(property).addListener(rate) { propValueResult ->
                propValueResult.onSuccess {
                    sharedFlow.emit(block(it))
                }
            }
        }
    private fun <T> subscribeState(property: NCarPropertyId, rate: NSensorRate, initial: T, block: suspend (CarPropertyValue<*>) -> T): StateFlow<T> =
        MutableStateFlow(initial).also { stateFlow ->
            subscriptionFor(property).addListener(rate) { propValueResult ->
                propValueResult.onSuccess {
                    stateFlow.emit(block(it))
                }
            }
        }

    fun startListening() = subscriptions.values.forEach { it.start(carPropertyManager) }
    fun stopListening() = subscriptions.values.forEach { it.stop(carPropertyManager) }

    fun <T> flowOf(property: NCarProperty.Raw<T>, rate: NSensorRate) = subscribeFlow(property.id, rate) { it.value as T }
    fun <Raw, T> flowOf(property: NCarProperty.RawTransform<Raw, T>, rate: NSensorRate) = subscribeFlow(property.id, rate) { property.transform(it.value as Raw) }
    fun <U: MeasurementUnit> flowOf(property: NCarProperty.Measurable<U>, rate: NSensorRate) = subscribeFlow(property.id, rate) {
        val value = it.value as Float
        Measurable(value, property.unit)
    }
    fun <U: MeasurementUnit> flowOf(property: NCarProperty.MeasurableRanged<U>, rate: NSensorRate) = subscribeFlow(property.id, rate) {
        MeasurableRanged(
            value = it.value as Float,
            unit = property.unit,
            range = ClosedMeasurableRange(
                start = property.range.start,
                endInclusive = property.range.endInclusive,
                unit = property.unit
            )
        )
    }

    fun <T> stateOf(property: NCarProperty.Raw<T>, rate: NSensorRate, initial: T): StateFlow<T> = subscribeState(property.id, rate, initial) { it.value as T }
    fun <Raw, T> stateOf(property: NCarProperty.RawTransform<Raw, T>, rate: NSensorRate, initial: T) = subscribeState(property.id, rate, initial) { property.transform(it.value as Raw) }
    fun <U: MeasurementUnit> stateOf(property: NCarProperty.Measurable<U>, rate: NSensorRate, initial: Float = 0f): StateFlow<Any> {
        return subscribeState(property.id, rate, Measurable(initial, property.unit)) {
            Measurable(it.value as Float, property.unit)
        }
    }
    fun <U: MeasurementUnit> stateOf(property: NCarProperty.MeasurableRanged<U>, rate: NSensorRate, initial: Float = 0f): StateFlow<MeasurableRanged<U>> {
        val range = ClosedMeasurableRange(
            start = property.range.start,
            endInclusive = property.range.endInclusive,
            unit = property.unit
        )

        return subscribeState(property.id, rate, MeasurableRanged(initial, property.unit, range)) {
            MeasurableRanged(
                value = it.value as Float,
                unit = property.unit,
                range = range
            )
        }
    }

    fun <T> propertyValue(property: NCarProperty.RawArea<T>) = NCarPropertyRawReader<T, T>(property.id.id, property.areaId, carPropertyManager, { it })
    fun <U: MeasurementUnit> propertyValue(property: NCarProperty.MeasurableArea<U>) = NCarPropertyMeasurableReader(property.id.id, property.areaId, property.unit, carPropertyManager)
    fun <U: MeasurementUnit> propertyValue(property: NCarProperty.MeasurableRangedArea<U>) = NCarPropertyMeasurableRangedReader(property.id.id, property.areaId, property.unit, property.range, carPropertyManager)
}

data class NCarPropertyRawReader<Raw, T>(val propertyId: Int, val areaId: Int, val propertyManager: CarPropertyManager, val transform: (Raw) -> T) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T =
        propertyManager.getProperty<Raw>(propertyId, areaId).value.let(transform)
}

data class NCarPropertyMeasurableReader<U: MeasurementUnit>(val propertyId: Int, val areaId: Int, val unit: U, val propertyManager: CarPropertyManager) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): Measurable<U> =
        Measurable(propertyManager.getFloatProperty(propertyId, areaId), unit)
}

data class NCarPropertyMeasurableRangedReader<U: MeasurementUnit>(val propertyId: Int, val areaId: Int, val unit: U, val range: ClosedFloatingPointRange<Float>, val propertyManager: CarPropertyManager) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): MeasurableRanged<U> =
        MeasurableRanged(
            value = propertyManager.getFloatProperty(propertyId, areaId),
            unit = unit,
            range = ClosedMeasurableRange(
                start = range.start,
                endInclusive = range.endInclusive,
                unit = unit
            )
        )
}
