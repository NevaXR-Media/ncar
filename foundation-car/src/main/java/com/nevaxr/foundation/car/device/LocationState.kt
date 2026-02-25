package com.nevaxr.device 

data class LocationState(
    val longitude: Double = 0.0,
    val latitude: Double = 0.0,
    val bearing: Float = 0f,
    val altitude: Double = 0.0,
    val accuracy: Float = 0f,
    val speed: Float = 0f,
    val timestamp: Long = 0L
)