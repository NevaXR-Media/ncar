package com.nevaxr.foundation.car

interface CarInputProvider<PropertyIdentifier> {
    fun <Raw, T> read(property: CarInputProperty<Raw, T>, identifier: PropertyIdentifier)
    fun <Property: CarInputProperty<Raw, T>, Raw, T> property(identifier: PropertyIdentifier, block: CarProperty.() -> Property) =
        CarInputProviderProperty(CarProperty.run(block), identifier)
}

data class CarInputProviderProperty<Property: CarInputProperty<Raw, T>, Raw, Identifier, T>(val property: Property, val identifier: Identifier)

class CarVhalInputProvider<PropertyIdentifier> : CarInputProvider<PropertyIdentifier> {
    override fun <Raw, T> read(
        property: CarInputProperty<Raw, T>,
        identifier: PropertyIdentifier
    ) {

    }
}