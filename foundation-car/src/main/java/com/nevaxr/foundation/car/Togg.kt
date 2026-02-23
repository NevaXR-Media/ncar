package com.nevaxr.foundation.car

object Togg: CarSpec<ToggGear> {
    private typealias Identifier = ToggPropertyIdentifier
    override val speed = CarProperty.Measurable<Speed>(Identifier.Speed, Speed.Kph, 0f..230f)
    override val gear = CarProperty.Transformative(Identifier.Gear, ToggGear::fromInt)
}

private object ToggPropertyIdentifier {
    typealias Identifier = CarPropertyIdentifier
    val Speed = Identifier(291504647,  "Performance Vehicle Speed")
    val Gear = Identifier(289408000,  "Gear Selection")
}

enum class ToggGear(val raw: Int) {
    PARKING(1),
    INVALID(Int.MIN_VALUE);

    companion object {
        fun fromInt(raw: Int) = when(raw) {
            1 -> PARKING
            else -> INVALID
        }
    }
}