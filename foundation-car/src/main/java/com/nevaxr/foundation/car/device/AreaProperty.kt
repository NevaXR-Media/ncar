package com.nevaxr.foundation.car.device

import android.car.VehicleAreaSeat

class AreaProperty

enum class FanSpeed(val value: Int) {
    OFF(0),
    SPEED_1(1),
    SPEED_2(2),
    SPEED_3(3),
    SPEED_4(4),
    SPEED_5(5),
    SPEED_6(6),
    SPEED_7(7),
    SPEED_MAX(15)
}


object HvacAreas {

    const val LEFT = VehicleAreaSeat.SEAT_ROW_1_LEFT or
            VehicleAreaSeat.SEAT_ROW_2_LEFT or
            VehicleAreaSeat.SEAT_ROW_2_CENTER

    const val RIGHT = VehicleAreaSeat.SEAT_ROW_1_RIGHT or VehicleAreaSeat.SEAT_ROW_2_RIGHT

    const val ALL = LEFT or RIGHT

}


enum class ToggHvacFanDirection(val id: Int, val property: String) {
    CENTER(1, "CENTER"),
    DOWN(2, "DOWN"),
    DOWN_CENTER(3, "DOWN_CENTER"),
    UP(4, "UP"),
    UP_CENTER(5, "UP_CENTER"),
    UP_DOWN(6, "UP_DOWN"),
    UP_DOWN_CENTER(7, "UP_DOWN_CENTER"),
}

object HvacFanDirection {
    const val CENTER = 1
    const val DOWN = 2
    const val DOWN_CENTER = 3
    const val UP = 4
    const val UP_CENTER = 5
    const val UP_DOWN = 6
    const val UP_DOWN_CENTER = 7
}

object HvacCustomAreas {
    const val LEFT = 49
    const val RIGHT = 68
}

object SeatArea {
    const val DRIVER_AREA = VehicleAreaSeat.SEAT_ROW_1_LEFT
    const val COPILOT_AREA = VehicleAreaSeat.SEAT_ROW_1_RIGHT
    const val BACK_SEAT_AREA = VehicleAreaSeat.SEAT_ROW_2_CENTER
}