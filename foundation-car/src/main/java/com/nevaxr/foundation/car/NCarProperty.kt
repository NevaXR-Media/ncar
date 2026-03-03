package com.nevaxr.foundation.car

import androidx.compose.runtime.State
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface NCarPropertyWritable<T> {
    val displayName: String?
    val requiredPermissions: Set<String>?
    suspend fun write(carService: NCarServiceBase, value: T)
}

interface NCarProperty<T> {
    val displayName: String?
    val requiredPermissions: Set<String>?

    fun subscribe(carService: NCarServiceBase, rate: NSensorRate): SharedFlow<T>
    suspend fun getProperty(carService: NCarServiceBase): T
}

interface NCarStateProperty<T>: NCarProperty<T> {
    fun subscribeState(carService: NCarServiceBase, rate: NSensorRate): State<T>
    fun subscribeStateFlow(carService: NCarServiceBase, rate: NSensorRate): StateFlow<T>
}
