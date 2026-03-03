package com.nevaxr.foundation.car

import kotlinx.coroutines.CoroutineScope

class NCar<BaseCarSpec: NCarSpec>(val scope: CoroutineScope, val spec: BaseCarSpec, private val service: NCarService<BaseCarSpec>) {
    private var _requiredPermissions = mutableSetOf<String>()
    val requiredPermissions: Set<String> get() = _requiredPermissions

    suspend fun <T> read(property: NCarProperty<T>) = property.getProperty(service)

    fun <T> stateOf(property: NCarStateProperty<T>, sensorRate: NSensorRate) =
        property.subscribeState(service, sensorRate).also {
            property.requiredPermissions?.let(_requiredPermissions::addAll)
        }

    fun <T> flowOf(property: NCarProperty<T>, sensorRate: NSensorRate) =
        property.subscribe(service, sensorRate).also {
            property.requiredPermissions?.let(_requiredPermissions::addAll)
        }
}
