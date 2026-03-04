package com.nevaxr.foundation.car

interface NCarPropertyProvider {
    fun release() {}
    suspend fun start()
    fun stop()
}
