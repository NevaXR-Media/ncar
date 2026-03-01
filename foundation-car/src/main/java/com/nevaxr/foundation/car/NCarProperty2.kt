package com.nevaxr.foundation.car

import android.car.hardware.CarPropertyValue
import android.car.hardware.property.CarPropertyManager
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.reflect.KClass

interface CarServiceBase {
    fun <T: PropertyProvider> propertyProviderOf(klass: KClass<T>): T
}

class Car<BaseCarSpec: CarSpec>(val spec: BaseCarSpec, private val service: CarService<BaseCarSpec>): CarServiceBase by service {
    suspend fun <T> read(property: CarProperty<T>) = property.getProperty(this)

    fun <BaseUnit: MeasurementUnit> stateOf(property: CarProperty<Measurement<BaseUnit>>, sensorRate: NSensorRate, default: Float? = null): StateFlow<Measurement<BaseUnit>> {
        val intiial = Measurement(0f, property.unit)
        return MutableStateFlow(default ?: 0f).also { flow ->
            property.subscribe(service, flow)
        }
    }

    fun <BaseUnit: MeasurementUnit> stateOf(property: CarProperty<MeasurementRanged<BaseUnit>>, sensorRate: NSensorRate, default: Float? = null): MutableStateFlow<MeasurementRanged<BaseUnit>> {
        TODO()
    }

    fun <T> stateOf(property: CarProperty<T>, sensorRate: NSensorRate, default: T): MutableStateFlow<T> {
        TODO()
    }

    fun <T> flowOf(property: CarProperty<T>, sensorRate: NSensorRate): MutableSharedFlow<T> {
        TODO()
    }
}

class CarService<BaseCarSpec: CarSpec>(private val specs: List<BaseCarSpec>, private val providers: Map<KClass<*>, PropertyProvider>) : CarServiceBase {
    val car = CompletableDeferred<Car<BaseCarSpec>>()
    override fun <T : PropertyProvider> propertyProviderOf(klass: KClass<T>): T {
        return providers[klass] as T
    }

    class Builder<BaseCarSpec: CarSpec> {
        val providers = mutableMapOf<KClass<*>, PropertyProvider>()
        val specs = mutableListOf<BaseCarSpec>()

        inline fun <reified T: PropertyProvider> addProvider(provider: T): Builder<BaseCarSpec> {
            providers[T::class] = provider
            return this
        }

        fun addCarSpec(spec: BaseCarSpec): Builder<BaseCarSpec> {
            specs.add(spec)
            return this
        }

        fun build() = CarService<BaseCarSpec>(specs, providers)
    }

    suspend fun awaitReady() {
        TODO()
    }
}

interface PropertyProvider {
    fun start()
    fun stop()
}

interface CarProperty<T> {
    fun subscribe(carService: CarServiceBase): StateFlow<T>
    suspend fun getProperty(carService: CarServiceBase): T
}


interface SocketProvider : PropertyProvider {
    fun <Raw> subscribe(id: String, handler: suspend (Raw) -> Unit)
    suspend fun <Raw> getProperty(id: String): Raw
}


data class SocketProperty<Raw, T>(val id: String, val transform: (Raw) -> T): CarProperty<T> {
    override fun subscribe(carService: CarServiceBase, mutableFlow: MutableStateFlow<T>): PropertySubscription {
        val provider = carService.propertyProviderOf(SocketProvider::class)
        return provider.subscribe<Raw>(id) { raw ->
            mutableFlow.emit(transform(raw))
        }.also(carService.subscriptions::add)
    }

    override suspend fun getProperty(carService: CarServiceBase): T {
        val provider = carService.propertyProviderOf(SocketProvider::class)
        val rawValue = provider.getProperty<Raw>(id)
        return transform(rawValue)
    }
}

interface CarSpec {
    suspend fun identify(carService: CarServiceBase): Boolean
}

interface GenericCarSpec : CarSpec {
    val deviceId: CarProperty<String>
    val model: CarProperty<String?>
    val brand: CarProperty<String?>

    val speedRange: MeasurementUnitRange<UnitSpeed>
    val speed: CarProperty<MeasurementRanged<UnitSpeed>>
    val gear: CarProperty<NVehicleGear>
}

object ToggSpec : GenericCarSpec {
    override suspend fun identify(carService: CarServiceBase): Boolean {
        val brand = brand.getProperty(carService)
        val model = model.getProperty(carService)
        Timber.d("Checking if the car is Togg, brand: ${brand ?: "null"}, model: ${model ?: "null"}")
        return true
    }

    override val deviceId = VhalProperty.string(333333)
    override val model = VhalProperty.string(444444).optional()
    override val brand = VhalProperty.string(555555).optional()

    override val speedRange = MeasurementUnitRange(0f, 51.3889f, UnitSpeed.metersPerSecond)
    override val speed = VhalProperty.measurement(11111, UnitSpeed.metersPerSecond, speedRange)
    override val gear = VhalProperty(22222) { rawGear: Int ->
        when (rawGear) {
            1 -> NVehicleGear.Neutral
            2 -> NVehicleGear.Reverse
            4 -> NVehicleGear.Park
            8 -> NVehicleGear.Drive
            else -> NVehicleGear.Park
        }
    }
}