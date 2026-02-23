package com.nevaxr.foundation.car

import android.car.Car
import android.car.hardware.property.CarPropertyManager
import android.content.Context
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow

//class CarService(context: Context) {
//    var propertyProvider: CarPropertyProvider
//
//    init {
//        if (CarVHalPropertyProvider.isAvailable()) {
//            propertyProvider = CarVHalPropertyProvider(context)
//        } else {
//            propertyProvider = CarMockPropertyProvider()
//        }
//    }
//
//    fun <Raw, T> propertyFlow(property: CarInputProperty<Raw, T>): MutableStateFlow<T> {
//
//    }
//}
//
//interface CarPropertyProvider {
//    fun start()
//    fun stop()
//
//    fun <T> subscribe(property: CarProperty.Generic<T>): CarPropertySubscription
//    fun <BaseUnit: RawFloatUnit> subscribe(property: CarProperty.Measurable<BaseUnit>): CarPropertySubscription
//    fun subscribe(property: CarProperty.Numeric): CarPropertySubscription
//    fun subscribe(property: CarProperty.NumericRange): CarPropertySubscription
//    fun <Raw, T> subscribe(property: CarProperty.Transformative<Raw, T>): CarPropertySubscription
//}
//
//interface CarPropertySubscription {
//    fun cancel()
//}
//
//class CarMockPropertyProvider: CarPropertyProvider {
//    override fun start() { }
//    override fun stop() { }
//
//    override fun <T> subscribe(property: CarProperty.Generic<T>): CarPropertySubscription {
//        TODO("Not yet implemented")
//    }
//
//    override fun <BaseUnit : RawFloatUnit> subscribe(property: CarProperty.Measurable<BaseUnit>): CarPropertySubscription {
//        TODO("Not yet implemented")
//    }
//
//    override fun subscribe(property: CarProperty.Numeric): CarPropertySubscription {
//        TODO("Not yet implemented")
//    }
//
//    override fun subscribe(property: CarProperty.NumericRange): CarPropertySubscription {
//        TODO("Not yet implemented")
//    }
//
//    override fun <Raw, T> subscribe(property: CarProperty.Transformative<Raw, T>): CarPropertySubscription {
//        TODO("Not yet implemented")
//    }
//}
//
//class CarVHalPropertyProvider(context: Context) : CarPropertyProvider {
//    private val car = Car.createCar(context)
//    private val propertyManager = car.getCarManager(Car.PROPERTY_SERVICE) as CarPropertyManager
//
//    companion object {
//        fun isAvailable(): Boolean {
//            return try {
//                Class.forName("android.car.Car")
//                true
//            } catch (e: ClassNotFoundException) {
//                Log.e("CarService", "Car API is not available.", e)
//                false
//            }
//        }
//    }
//
//    override fun start() {
//        TODO("Not yet implemented")
//    }
//
//    override fun stop() {
//        TODO("Not yet implemented")
//    }
//
//    override fun <T> subscribe(property: CarProperty.Generic<T>): CarPropertySubscription {
//        TODO("Not yet implemented")
//    }
//
//    override fun <BaseUnit : RawFloatUnit> subscribe(property: CarProperty.Measurable<BaseUnit>): CarPropertySubscription {
//        TODO("Not yet implemented")
//    }
//
//    override fun subscribe(property: CarProperty.Numeric): CarPropertySubscription {
//        TODO("Not yet implemented")
//    }
//
//    override fun subscribe(property: CarProperty.NumericRange): CarPropertySubscription {
//        TODO("Not yet implemented")
//    }
//
//    override fun <Raw, T> subscribe(property: CarProperty.Transformative<Raw, T>): CarPropertySubscription {
//        TODO("Not yet implemented")
//    }
//}