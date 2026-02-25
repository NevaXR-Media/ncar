package com.nevaxr.foundation.car

/**
 * Represents the different types of permissions that can be granted to an application for interacting with a car.
 *
 * Each permission is represented by a string that corresponds to the Android manifest permission name.
 */
enum class NCarPermission(val permission: String) {
    CONTROL_AUDIO_VOLUME("android.car.permission.CAR_CONTROL_AUDIO_VOLUME"),
    CONTROL_AUDIO_SETTINGS("android.car.permission.CAR_CONTROL_AUDIO_SETTINGS"),
    SPEED("android.car.permission.CAR_SPEED"),
    READ_DISPLAY_UNITS("android.car.permission.READ_CAR_DISPLAY_UNITS"),
    POWERTRAIN("android.car.permission.CAR_POWERTRAIN"),
    ENERGY("android.car.permission.CAR_ENERGY"),
    ENERGY_PORTS("android.car.permission.CAR_ENERGY_PORTS"),
    CAR_INFO("android.car.permission.CAR_INFO"),
    EXTERIOR_ENVIRONMENT("android.car.permission.CAR_EXTERIOR_ENVIRONMENT"),
    NAVIGATION_MANAGER("android.car.permission.CAR_NAVIGATION_MANAGER"),
    CONTROL_DISPLAY_UNITS("android.car.permission.CONTROL_CAR_DISPLAY_UNITS"),
    CONTROL_INTERIOR_LIGHTS("android.car.permission.CONTROL_CAR_INTERIOR_LIGHTS"),
    IDENTIFICATION("android.car.permission.CAR_IDENTIFICATION"),
    READ_INTERIOR_LIGHTS("android.car.permission.READ_CAR_INTERIOR_LIGHTS"),
    READ_STEERING_STATE("android.car.permission.READ_CAR_STEERING"),
    CONTROL_CAR_CLIMATE("android.car.Car.PERMISSION_CONTROL_CAR_CLIMATE"),
    DIAGNOSTIC_READ_ALL("android.car.Car.PERMISSION_CAR_DIAGNOSTIC_READ_ALL"),
    VENDOR("android.car.permission.CAR_VENDOR_EXTENSION"),
    READ_CAR_STEERING("android.car.permission.READ_CAR_STEERING")
}

val allVehiclePermissions: List<String>
    get() = NCarPermission.entries.map { it.permission }