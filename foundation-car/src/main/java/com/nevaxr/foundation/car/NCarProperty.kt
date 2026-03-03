package com.nevaxr.foundation.car

import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface NCarPropertyWritable<T> {
    suspend fun setProperty(carService: NCarServiceBase, value: T)
}

interface NCarProperty<T> {
    val displayName: String? get() = null
    val requiredPermissions: Set<String>? get() = null

    fun subscribe(carService: NCarServiceBase, rate: NSensorRate): SharedFlow<T>
    suspend fun getProperty(carService: NCarServiceBase): T
}

interface NCarStateProperty<T>: NCarProperty<T> {
    fun subscribeState(carService: NCarServiceBase, rate: NSensorRate): StateFlow<T>
}
