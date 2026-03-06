package com.nevaxr.foundation.car

import kotlinx.coroutines.CoroutineScope
import timber.log.Timber

class NCar<BaseCarSpec: NCarSpec, CarState>(
    val scope: CoroutineScope,
    val spec: BaseCarSpec,
    private val service: NCarService<BaseCarSpec, CarState>,
    private val stateBuilder: (NCar<BaseCarSpec, CarState>) -> CarState
) {
    private var _requiredPermissions = mutableSetOf<String>()
    val requiredPermissions: Set<String> get() = _requiredPermissions
    val state = stateBuilder(this)

    fun <T> reader(property: NCarStateProperty<T>) = stateOf(property, NSensorRate.OnChange)

    suspend fun <T> setProperty(property: NCarPropertyWritable<T>, value: T) {
        property.write(service, value)
    }

    fun <T> stateOf(property: NCarStateProperty<T>, sensorRate: NSensorRate = NSensorRate.OnChange) =
        property.subscribeState(service, sensorRate).also {
          Timber.d("NCar state of property ${property.displayName} -> ${property.requiredPermissions}")
            property.requiredPermissions?.let(_requiredPermissions::addAll)
          Timber.d("NCar state of property _requiredPermissions $_requiredPermissions")
        }

    fun <T> stateFlowOf(property: NCarStateProperty<T>, sensorRate: NSensorRate = NSensorRate.OnChange) =
        property.subscribeStateFlow(service, sensorRate).also {
            property.requiredPermissions?.let(_requiredPermissions::addAll)
        }

    fun <T> flowOf(property: NCarProperty<T>, sensorRate: NSensorRate = NSensorRate.OnChange) =
        property.subscribe(service, sensorRate).also {
            property.requiredPermissions?.let(_requiredPermissions::addAll)
        }
}