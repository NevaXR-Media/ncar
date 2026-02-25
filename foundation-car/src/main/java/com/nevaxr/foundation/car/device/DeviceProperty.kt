package com.nevaxr.device

import kotlinx.serialization.Serializable

@Serializable
data class DeviceProperty<T>(
    val timestamp: Long,
    val id: Int = 0,
    val name: String,
    val value: T?,
    val areaId: Int = 0,
)