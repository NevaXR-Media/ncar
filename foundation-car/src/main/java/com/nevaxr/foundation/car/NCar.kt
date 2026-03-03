package com.nevaxr.foundation.car

import kotlinx.coroutines.CoroutineScope

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
            property.requiredPermissions?.let(_requiredPermissions::addAll)
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