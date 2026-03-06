package com.nevaxr.foundation.car

import android.content.Context
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapNotNull
import timber.log.Timber
import kotlin.reflect.KClass

interface NCarServiceBase {

  fun <T : NCarPropertyProvider> propertyProviderOf(klass: KClass<T>): T
}

class NCarService<BaseCarSpec : NCarSpec, CarState>(
  private val scope: CoroutineScope,
  private val specs: List<BaseCarSpec>,
  private val providerLoaders: Map<KClass<*>, suspend () -> NCarPropertyProvider>,
  private val carStateBuilder: (NCar<BaseCarSpec, CarState>) -> CarState
) : NCarServiceBase {

  private val _state = MutableStateFlow<State<BaseCarSpec, CarState>>(Loading())
  val state get() = _state.asStateFlow()

  val car get() = (state.value as? Ready)?.car
  val isReady get() = state.value is Ready
  val isLoading get() = state.value is Loading

  suspend fun awaitReady() = _state.mapNotNull { state ->
    when (state) {
      is Ready -> Result.success(state.car)
      is Unavailable -> Result.failure(Exception("CarService is not available"))
      else -> null
    }
  }.first()

  private var propertyProviders = mapOf<KClass<*>, NCarPropertyProvider>()
  override fun <T : NCarPropertyProvider> propertyProviderOf(klass: KClass<T>): T {
    return propertyProviders[klass] as T
  }

  fun loadCar() {
    if (isReady) {
      return
    }

    Timber.d("Loading car...")

    scope.launch {
      propertyProviders = this@NCarService.providerLoaders.map { pair ->
        async {
          runCatching {
            Timber.d("Initializing provider %s", pair.key.simpleName)
            Pair(pair.key, pair.value())
          }.onFailure { err ->
            Timber.e(err, "Property provider (${pair.key.simpleName}) loading failed")
          }.onSuccess {
            Timber.d("Property provider (${pair.key.simpleName} loaded successfully")
          }
        }
      }.awaitAll().mapNotNull { it.getOrNull() }.toMap()

      val foundSpec = specs.firstOrNull { spec ->
        val result = runCatching { spec.identify(this@NCarService) }
        result.getOrNull() == true
      }

      if (foundSpec != null) {
        Timber.d("Car spec identified: ${foundSpec.specName}")

        val car = NCar(scope, foundSpec, this@NCarService, carStateBuilder)
        Timber.d("Car state is ready")

        _state.value = Ready(car)
      } else {
        Timber.w("None of car specs matched the current device, Car service is unavailable")
        _state.value = Unavailable()
      }
    }
  }

  private var isProvidersRunning = false
  private var startJob: Job? = null
  fun start() {
    if (startJob?.isActive == true) return
    if (!isProvidersRunning) {
      startJob = scope.launch {
        val car = awaitReady().getOrThrow()
        car.spec.providers(this@NCarService).forEach { it.start() }
        isProvidersRunning = true
      }
    }
  }

  fun stop() {
    if (startJob?.isCompleted == true && isProvidersRunning) {
      Timber.d("Stopping providers...")
      car?.spec?.providers(this)?.forEach { it.stop() }
      isProvidersRunning = false
    }
  }

  fun releaseCar() {
    propertyProviders.values.forEach { it.release() }
    propertyProviders = emptyMap()
    _state.tryEmit(Loading())
  }

  sealed interface State<BaseCarSpec : NCarSpec, CarState>
  class Loading<BaseCarSpec : NCarSpec, CarState> : State<BaseCarSpec, CarState>
  class Unavailable<BaseCarSpec : NCarSpec, CarState> : State<BaseCarSpec, CarState>
  data class Ready<BaseCarSpec : NCarSpec, CarState>(val car: NCar<BaseCarSpec, CarState>) :
    State<BaseCarSpec, CarState>

  class Builder<BaseCarSpec : NCarSpec, CarState>(private val scope: CoroutineScope) {

    val providerLoaders = mutableMapOf<KClass<*>, suspend () -> NCarPropertyProvider>()
    val specs = mutableListOf<BaseCarSpec>()

    inline fun <reified T : NCarPropertyProvider> addProvider(noinline provider: suspend () -> T): Builder<BaseCarSpec, CarState> {
      providerLoaders[T::class] = provider
      return this
    }

    fun addCarSpec(spec: BaseCarSpec): Builder<BaseCarSpec, CarState> {
      specs.add(spec)
      return this
    }

    fun build(carStateBuilder: (NCar<BaseCarSpec, CarState>) -> CarState) =
      NCarService(scope, specs, providerLoaders, carStateBuilder)
  }

  companion object {

    fun <CarState> buildAndroidAutoGeneric(
      context: Context,
      scope: CoroutineScope,
      forceInitialPropertyRead: Boolean = false,
      carStateBuilder: (NCar<NCarSpecGeneric, CarState>) -> CarState
    ): NCarService<NCarSpecGeneric, CarState> {
      return Builder<NCarSpecGeneric, CarState>(scope)
        .addCarSpec(NCarSpecTogg)
        .addProvider { NVhalProvider(context, scope, forceInitialPropertyRead) }
        .build(carStateBuilder)
    }

    fun <CarState> buildTogg(
      context: Context,
      scope: CoroutineScope,
      carStateBuilder: (NCar<NCarSpecTogg, CarState>) -> CarState
    ): NCarService<NCarSpecTogg, CarState> {
      return Builder<NCarSpecTogg, CarState>(scope)
        .addCarSpec(NCarSpecTogg)
        .addProvider { NVhalProvider(context, scope, true) }
        .build(carStateBuilder)
    }
  }
}
