package com.nevaxr.foundation.car

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

data class FallbackStateProperty<T>(
  override val displayName: String?,
  override val requiredPermissions: Set<String>?,
  private val initialValue: T,
  private val subscribePrimary: (NCarServiceBase, NSensorRate, suspend (T) -> Unit) -> Unit,
  private val subscribeFallback: (NCarServiceBase, NSensorRate, suspend (T) -> Unit) -> Unit,
  private val readPrimary: suspend (NCarServiceBase) -> T,
  private val readFallback: suspend (NCarServiceBase) -> T,
) : NCarStateProperty<T> {

  override fun subscribe(carService: NCarServiceBase, rate: NSensorRate): SharedFlow<T> {
    val flow = MutableSharedFlow<T>(replay = 1)
    flow.tryEmit(initialValue)
    attach(
      carService = carService,
      rate = rate,
      emit = { flow.emit(it) },
    )
    return flow.asSharedFlow()
  }

  override suspend fun getProperty(carService: NCarServiceBase): T {
    return runCatching { readPrimary(carService) }.getOrElse { readFallback(carService) }
  }

  override fun subscribeState(carService: NCarServiceBase, rate: NSensorRate): State<T> {
    val state = mutableStateOf(initialValue)
    attach(
      carService = carService,
      rate = rate,
      emit = { state.value = it },
    )
    return state
  }

  override fun subscribeStateFlow(carService: NCarServiceBase, rate: NSensorRate): StateFlow<T> {
    val state = MutableStateFlow(initialValue)
    attach(
      carService = carService,
      rate = rate,
      emit = { state.emit(it) },
    )
    return state.asStateFlow()
  }

  private fun attach(
    carService: NCarServiceBase,
    rate: NSensorRate,
    emit: suspend (T) -> Unit,
  ) {
    var hasPrimaryValue = false
    subscribeFallback(carService, rate) { fallbackValue ->
      if (!hasPrimaryValue) {
        emit(fallbackValue)
      }
    }
    subscribePrimary(carService, rate) { primaryValue ->
      hasPrimaryValue = true
      emit(primaryValue)
    }
  }
}

data class DemoWritableProperty<T>(
  override val displayName: String?,
  private val writeToProvider: suspend (DemoPropertyProvider, T) -> Unit,
) : NCarPropertyWritable<T> {

  override val requiredPermissions: Set<String>? = null

  override suspend fun write(carService: NCarServiceBase, value: T) {
    val provider = carService.propertyProviderOf(DemoPropertyProvider::class)
    writeToProvider(provider, value)
  }
}

