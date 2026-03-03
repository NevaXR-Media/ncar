package com.nevaxr.foundation.car

interface NCarSpec {
    val specName: String
    fun providers(carService: NCarServiceBase): List<NCarPropertyProvider>
    suspend fun identify(carService: NCarServiceBase): Boolean
}
