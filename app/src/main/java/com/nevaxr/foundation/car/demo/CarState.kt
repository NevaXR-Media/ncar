package com.nevaxr.foundation.car.demo

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.MutableStateFlow

@Stable
class CarState {
    private val _speed = MutableStateFlow(0f)
    val speed get() = _speed.value
}