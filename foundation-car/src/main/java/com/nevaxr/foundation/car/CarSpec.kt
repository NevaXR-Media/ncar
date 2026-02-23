package com.nevaxr.foundation.car

interface CarSpec<GearType> {
    val speed: CarProperty.Measurable<Speed>
    val gear: CarProperty.Transformative<Int, GearType>
}

