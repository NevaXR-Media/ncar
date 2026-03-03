package com.nevaxr.foundation.car

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class NVhalStateProperty<Raw, T>(val property: NVhalProperty<Raw, T>, val initialValue: T) : NCarProperty<T> by property, NCarStateProperty<T> {
    override fun subscribeState(carService: NCarServiceBase, rate: NSensorRate): StateFlow<T> {
        val provider = carService.propertyProviderOf(NVhalProvider::class)
        val mutableStateFlow = MutableStateFlow(initialValue)
        provider.subscribe<Raw>(property.key, rate) { raw ->
            mutableStateFlow.emit(property.transform(raw))
        }

        return mutableStateFlow.asStateFlow()
    }
}
