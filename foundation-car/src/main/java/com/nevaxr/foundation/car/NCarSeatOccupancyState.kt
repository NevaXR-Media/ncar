package com.nevaxr.foundation.car

data class NCarSeatOccupancyState(
    val frontLeft: Boolean = false,
    val frontRight: Boolean = false,
    val backLeft: Boolean = false,
    val backRight: Boolean = false,
)
