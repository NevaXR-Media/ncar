package com.nevaxr.foundation.car

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class NVhalStateProperty<Raw, T>(val property: NVhalProperty<Raw, T>, val initialValue: T) : NCarProperty<T> by property, NCarStateProperty<T> {
    override fun subscribeState(carService: NCarServiceBase, rate: NSensorRate): State<T> {
        val provider = carService.propertyProviderOf(NVhalProvider::class)

        val mutableState = mutableStateOf(initialValue)
        provider.subscribe(property.key, rate) { raw ->
            mutableState.value = property.transform(raw)
        }

        return mutableState
    }

    override fun subscribeStateFlow(carService: NCarServiceBase, rate: NSensorRate): StateFlow<T> {
        val provider = carService.propertyProviderOf(NVhalProvider::class)

        val mutableStateFlow = MutableStateFlow(initialValue)
        provider.subscribe(property.key, rate) { raw ->
            mutableStateFlow.emit(property.transform(raw))
        }

        return mutableStateFlow.asStateFlow()
    }
}
