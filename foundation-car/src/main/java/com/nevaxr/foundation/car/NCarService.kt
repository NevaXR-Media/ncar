package com.nevaxr.foundation.car

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapNotNull
import timber.log.Timber
import kotlin.reflect.KClass

interface NCarServiceBase {
    fun <T: NCarPropertyProvider> propertyProviderOf(klass: KClass<T>): T
}

class NCarService<BaseCarSpec: NCarSpec>(private val scope: CoroutineScope, private val specs: List<BaseCarSpec>, private val providerLoaders: Map<KClass<*>, suspend () -> NCarPropertyProvider>) : NCarServiceBase {
    private val _state = MutableStateFlow<State<BaseCarSpec>>(Loading())
    val state get() = _state.value

    val car get() = (state as? Ready)?.car
    val isReady get() = state is Ready
    val isLoading get() = state is Loading

    suspend fun awaitReady() = _state.mapNotNull { it as? Ready<BaseCarSpec> }.first().car

    private var propertyProviders = mapOf<KClass<*>, NCarPropertyProvider>()
    override fun <T : NCarPropertyProvider> propertyProviderOf(klass: KClass<T>): T {
        return propertyProviders[klass] as T
    }

    suspend fun loadCar(): Result<NCar<BaseCarSpec>> {
        val state = state
        if (state is Ready<BaseCarSpec>) {
            return Result.success(state.car)
        }

        return scope.async {
            propertyProviders = this@NCarService.providerLoaders.map { pair ->
                async {
                    runCatching {
                        Pair(pair.key, pair.value())
                    }.onFailure { err ->
                        Timber.e(err, "Property provider (${pair.key.simpleName}) loading failed")
                    }.onSuccess {
                        Timber.d("Property provider (${pair.key.simpleName} loaded successfully")
                    }
                }
            }.awaitAll().mapNotNull { it.getOrNull() }.toMap()

            val foundSpec = specs.firstOrNull { it.identify(this@NCarService) } ?: run {
                val err = Exception("CarSpec not found")
                _state.value = Unavailable()
                return@async Result.failure(err)
            }

            Timber.d("Car spec identified: ${foundSpec.specName}")

            val car = NCar(scope, foundSpec, this@NCarService)
            Timber.d("Car state is ready")

            _state.value = Ready(car)
            Result.success(car)
        }.await()
    }

    fun start() {
        car?.spec?.providers(this)?.forEach { it.start() }
    }

    fun stop() {
        car?.spec?.providers(this)?.forEach { it.stop() }
    }

    sealed interface State<BaseCarSpec: NCarSpec>
    class Loading<BaseCarSpec: NCarSpec> : State<BaseCarSpec>
    class Unavailable<BaseCarSpec: NCarSpec>: State<BaseCarSpec>
    data class Ready<BaseCarSpec: NCarSpec>(val car: NCar<BaseCarSpec>): State<BaseCarSpec>

    class Builder<BaseCarSpec: NCarSpec>(private val scope: CoroutineScope) {
        val providerLoaders = mutableMapOf<KClass<*>, suspend () -> NCarPropertyProvider>()
        val specs = mutableListOf<BaseCarSpec>()

        inline fun <reified T: NCarPropertyProvider> addProvider(noinline provider: suspend () -> T): Builder<BaseCarSpec> {
            providerLoaders[T::class] = provider
            return this
        }

        fun addCarSpec(spec: BaseCarSpec): Builder<BaseCarSpec> {
            specs.add(spec)
            return this
        }

        fun build() = NCarService(scope, specs, providerLoaders)
    }

    companion object {
        fun buildTogg(context: Context, scope: CoroutineScope): NCarService<NCarSpecTogg> {
            return Builder<NCarSpecTogg>(scope)
                .addCarSpec(NCarSpecTogg)
                .addProvider { NVhalProvider(context, scope) }
                .build()
        }
    }
}
