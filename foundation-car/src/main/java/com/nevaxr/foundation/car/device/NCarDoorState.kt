package com.nevaxr.foundation.car.device

data class NCarDoorState(
    val frontLeft: Boolean = false,
    val frontRight: Boolean = false,
    val backLeft: Boolean = false,
    val backRight: Boolean = false,
)