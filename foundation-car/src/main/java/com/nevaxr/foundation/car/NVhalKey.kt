package com.nevaxr.foundation.car

import android.Manifest
import android.car.Car as ACar

data class NVhalKey(val id: Int, val areaId: Int = 0, val name: String? = null, val permissions: Set<String>? = null) {
    fun areaId(id: Int) = copy(areaId = id)

    companion object {
        fun findById(id: Int) = all.firstOrNull { it.id == id }

        val PERF_VEHICLE_SPEED = NVhalKey(291504647, 0, "Performance Vehicle Speed", setOf(ACar.PERMISSION_SPEED))
        val PERF_VEHICLE_SPEED_DISPLAY = NVhalKey(291504648, 0, "Performance Vehicle Speed Display", setOf(ACar.PERMISSION_SPEED))
        val ABS_ACTIVE = NVhalKey(287310858, 0, "ABS Active", setOf(ACar.PERMISSION_CAR_DYNAMICS_STATE))


        @Deprecated("Use CarPowerManager instead")
        val AP_POWER_BOOTUP_REASON = NVhalKey(289409538, 0, "AP Power Bootup Reason")
        @Deprecated("Use CarPowerManager instead")
        val AP_POWER_STATE_REPORT = NVhalKey(289475073, 0, "AP Power State Report")
        @Deprecated("Use CarPowerManager instead")
        val AP_POWER_STATE_REQ = NVhalKey(289475072, 0, "AP Power State Request")

        val CABIN_LIGHTS_STATE = NVhalKey(289410817, 0, "Cabin Lights State", setOf(ACar.PERMISSION_READ_INTERIOR_LIGHTS))
        val CABIN_LIGHTS_SWITCH = NVhalKey(289410818, 0, "Cabin Lights Switch", setOf(ACar.PERMISSION_CONTROL_INTERIOR_LIGHTS))
        val CRITICALLY_LOW_TIRE_PRESSURE = NVhalKey(392168202, 0, "Critically Low Tire Pressure", setOf(ACar.PERMISSION_TIRES))
        val CURRENT_GEAR = NVhalKey(289408001, 0, "Current Gear", setOf(ACar.PERMISSION_POWERTRAIN))

        @Deprecated("Use CarPowerManager instead")
        val DISPLAY_BRIGHTNESS = NVhalKey(289409539, 0, "Display Brightness")

        val DISTANCE_DISPLAY_UNITS = NVhalKey(289408512, 0, "Distance Display Units", setOf(ACar.PERMISSION_READ_DISPLAY_UNITS))
        val DOOR_LOCK = NVhalKey(371198722, 0, "Door Lock", setOf(ACar.PERMISSION_CONTROL_CAR_DOORS))
        val DOOR_MOVE = NVhalKey(373295873, 0, "Door Move", setOf(ACar.PERMISSION_CONTROL_CAR_DOORS))
        val DOOR_POS = NVhalKey(373295872, 0, "Door Position", setOf(ACar.PERMISSION_CONTROL_CAR_DOORS))
        val ELECTRONIC_TOLL_COLLECTION_CARD_STATUS = NVhalKey(289410874, 0, "Electronic Toll Collection Card Status", setOf(ACar.PERMISSION_CAR_INFO))
        val ELECTRONIC_TOLL_COLLECTION_CARD_TYPE = NVhalKey(289410873, 0, "Electronic Toll Collection Card Type", setOf(ACar.PERMISSION_CAR_INFO))
        val ENGINE_COOLANT_TEMP = NVhalKey(291504897, 0, "Engine Coolant Temperature", setOf(ACar.PERMISSION_CAR_ENGINE_DETAILED))
        val ENGINE_OIL_LEVEL = NVhalKey(289407747, 0, "Engine Oil Level", setOf(ACar.PERMISSION_CAR_ENGINE_DETAILED))
        val ENGINE_OIL_TEMP = NVhalKey(291504900, 0, "Engine Oil Temperature", setOf(ACar.PERMISSION_CAR_ENGINE_DETAILED))
        val ENGINE_RPM = NVhalKey(291504901, 0, "Engine RPM", setOf(ACar.PERMISSION_CAR_ENGINE_DETAILED))
        val ENV_OUTSIDE_TEMPERATURE = NVhalKey(291505923, 0, "Outside Temperature", setOf(ACar.PERMISSION_EXTERIOR_ENVIRONMENT))
        val EPOCH_TIME = NVhalKey(290457094, 0, "Epoch Time", setOf(ACar.PERMISSION_CAR_EPOCH_TIME))
        val EV_BATTERY_DISPLAY_UNITS = NVhalKey(289408515, 0, "EV Battery Display Units", setOf(ACar.PERMISSION_READ_DISPLAY_UNITS))
        val EV_BATTERY_INSTANTANEOUS_CHARGE_RATE = NVhalKey(291504908, 0, "EV Battery Instantaneous Charge Rate", setOf(ACar.PERMISSION_ENERGY))
        val EV_BATTERY_LEVEL = NVhalKey(291504905, 0, "EV Battery Level", setOf(ACar.PERMISSION_ENERGY))
        val EV_BRAKE_REGENERATION_LEVEL = NVhalKey(289408012, 0, "EV Brake Regeneration Level", setOf(ACar.PERMISSION_POWERTRAIN))
        val EV_CHARGE_CURRENT_DRAW_LIMIT = NVhalKey(291508031, 0, "EV Charge Current Draw Limit", setOf(ACar.PERMISSION_CONTROL_CAR_ENERGY))
        val EV_CHARGE_PERCENT_LIMIT = NVhalKey(291508032, 0, "EV Charge Percent Limit", setOf(ACar.PERMISSION_CONTROL_CAR_ENERGY))
        val EV_CHARGE_PORT_CONNECTED = NVhalKey(287310603, 0, "EV Charge Port Connected", setOf(ACar.PERMISSION_ENERGY_PORTS))
        val EV_CHARGE_PORT_OPEN = NVhalKey(287310602, 0, "EV Charge Port Open", setOf(ACar.PERMISSION_ENERGY_PORTS))
        val EV_CHARGE_STATE = NVhalKey(289410881, 0, "EV Charge State", setOf(ACar.PERMISSION_ENERGY))
        val EV_CHARGE_SWITCH = NVhalKey(287313730, 0, "EV Charge Switch", setOf(ACar.PERMISSION_CONTROL_CAR_ENERGY))
        val EV_CHARGE_TIME_REMAINING = NVhalKey(289410883, 0, "EV Charge Time Remaining", setOf(ACar.PERMISSION_ENERGY))
        val EV_CURRENT_BATTERY_CAPACITY = NVhalKey(291504909, 0, "EV Current Battery Capacity", setOf(ACar.PERMISSION_ENERGY))
        val EV_REGENERATIVE_BRAKING_STATE = NVhalKey(289410884, 0, "EV Regenerative Braking State", setOf(ACar.PERMISSION_ENERGY))
        val EV_STOPPING_MODE = NVhalKey(289408013, 0, "EV Stopping Mode", setOf(ACar.PERMISSION_POWERTRAIN))
        val FOG_LIGHTS_STATE = NVhalKey(289410562, 0, "Fog Lights State", setOf(ACar.PERMISSION_EXTERIOR_LIGHTS))
        val FOG_LIGHTS_SWITCH = NVhalKey(289410578, 0, "Fog Lights Switch", setOf(ACar.PERMISSION_CONTROL_EXTERIOR_LIGHTS))
        val FRONT_FOG_LIGHTS_STATE = NVhalKey(289410875, 0, "Front Fog Lights State", setOf(ACar.PERMISSION_EXTERIOR_LIGHTS))
        val FRONT_FOG_LIGHTS_SWITCH = NVhalKey(289410876, 0, "Front Fog Lights Switch", setOf(ACar.PERMISSION_CONTROL_EXTERIOR_LIGHTS))
        val FUEL_CONSUMPTION_UNITS_DISTANCE_OVER_VOLUME = NVhalKey(287311364, 0, "Fuel Consumption Units Distance Over Volume", setOf(ACar.PERMISSION_READ_DISPLAY_UNITS))
        val FUEL_DOOR_OPEN = NVhalKey(287310600, 0, "Fuel Door Open", setOf(ACar.PERMISSION_ENERGY_PORTS))
        val FUEL_LEVEL = NVhalKey(291504903, 0, "Fuel Level", setOf(ACar.PERMISSION_ENERGY))
        val FUEL_LEVEL_LOW = NVhalKey(287310853, 0, "Fuel Level Low", setOf(ACar.PERMISSION_ENERGY))
        val FUEL_VOLUME_DISPLAY_UNITS = NVhalKey(289408513, 0, "Fuel Volume Display Units", setOf(ACar.PERMISSION_READ_DISPLAY_UNITS))
        val GEAR_SELECTION = NVhalKey(289408000, 0, "Gear Selection", setOf(ACar.PERMISSION_POWERTRAIN))
        val GENERAL_SAFETY_REGULATION_COMPLIANCE = NVhalKey(289410887, 0, "General Safety Regulation Compliance", setOf(ACar.PERMISSION_CAR_INFO))
        val HAZARD_LIGHTS_STATE = NVhalKey(289410563, 0, "Hazard Lights State", setOf(ACar.PERMISSION_EXTERIOR_LIGHTS))
        val HAZARD_LIGHTS_SWITCH = NVhalKey(289410579, 0, "Hazard Lights Switch", setOf(ACar.PERMISSION_CONTROL_EXTERIOR_LIGHTS))
        val HEADLIGHTS_STATE = NVhalKey(289410560, 0, "Headlights State", setOf(ACar.PERMISSION_EXTERIOR_LIGHTS))
        val HEADLIGHTS_SWITCH = NVhalKey(289410576, 0, "Headlights Switch", setOf(ACar.PERMISSION_CONTROL_EXTERIOR_LIGHTS))
        val HIGH_BEAM_LIGHTS_STATE = NVhalKey(289410561, 0, "High Beam Lights State", setOf(ACar.PERMISSION_EXTERIOR_LIGHTS))
        val HIGH_BEAM_LIGHTS_SWITCH = NVhalKey(289410577, 0, "High Beam Lights Switch", setOf(ACar.PERMISSION_CONTROL_EXTERIOR_LIGHTS))
        val HVAC_ACTUAL_FAN_SPEED_RPM = NVhalKey(356517135, 0, "HVAC Actual Fan Speed RPM", setOf(ACar.PERMISSION_CONTROL_CAR_CLIMATE))
        val HVAC_AC_ON = NVhalKey(354419973, 0, "HVAC AC On", setOf(ACar.PERMISSION_CONTROL_CAR_CLIMATE))
        val HVAC_AUTO_ON = NVhalKey(354419978, 0, "HVAC Auto On", setOf(ACar.PERMISSION_CONTROL_CAR_CLIMATE))
        val HVAC_AUTO_RECIRC_ON = NVhalKey(354419986, 0, "HVAC Auto Recirculation On", setOf(ACar.PERMISSION_CONTROL_CAR_CLIMATE))
        val HVAC_DEFROSTER = NVhalKey(320865540, 0, "HVAC Defroster", setOf(ACar.PERMISSION_CONTROL_CAR_CLIMATE))
        val HVAC_DUAL_ON = NVhalKey(354419977, 0, "HVAC Dual On", setOf(ACar.PERMISSION_CONTROL_CAR_CLIMATE))
        val HVAC_FAN_DIRECTION = NVhalKey(356517121, 0, "HVAC Fan Direction", setOf(ACar.PERMISSION_CONTROL_CAR_CLIMATE))
        val HVAC_FAN_DIRECTION_AVAILABLE = NVhalKey(356582673, 0, "HVAC Fan Direction Available", setOf(ACar.PERMISSION_CONTROL_CAR_CLIMATE))
        val HVAC_FAN_SPEED = NVhalKey(356517120, 0, "HVAC Fan Speed", setOf(ACar.PERMISSION_CONTROL_CAR_CLIMATE))
        val HVAC_MAX_AC_ON = NVhalKey(354419974, 0, "HVAC Max AC On", setOf(ACar.PERMISSION_CONTROL_CAR_CLIMATE))
        val HVAC_MAX_DEFROST_ON = NVhalKey(354419975, 0, "HVAC Max Defrost On", setOf(ACar.PERMISSION_CONTROL_CAR_CLIMATE))
        val HVAC_POWER_ON = NVhalKey(354419984, 0, "HVAC Power On", setOf(ACar.PERMISSION_CONTROL_CAR_CLIMATE))
        val HVAC_RECIRC_ON = NVhalKey(354419976, 0, "HVAC Recirculation On", setOf(ACar.PERMISSION_CONTROL_CAR_CLIMATE))
        val HVAC_SEAT_TEMPERATURE = NVhalKey(356517131, 0, "HVAC Seat Temperature", setOf(ACar.PERMISSION_CONTROL_CAR_CLIMATE))
        val HVAC_SEAT_VENTILATION = NVhalKey(356517139, 0, "HVAC Seat Ventilation", setOf(ACar.PERMISSION_CONTROL_CAR_CLIMATE))
        val HVAC_SIDE_MIRROR_HEAT = NVhalKey(339739916, 0, "HVAC Side Mirror Heat", setOf(ACar.PERMISSION_CONTROL_CAR_CLIMATE))
        val HVAC_STEERING_WHEEL_HEAT = NVhalKey(289408269, 0, "HVAC Steering Wheel Heat", setOf(ACar.PERMISSION_CONTROL_CAR_CLIMATE))
        val HVAC_TEMPERATURE_CURRENT = NVhalKey(358614274, 0, "HVAC Temperature Current", setOf(ACar.PERMISSION_CONTROL_CAR_CLIMATE))
        val HVAC_TEMPERATURE_DISPLAY_UNITS = NVhalKey(289408270, 0, "HVAC Temperature Display Units", setOf(ACar.PERMISSION_READ_DISPLAY_UNITS))
        val HVAC_TEMPERATURE_SET = NVhalKey(358614275, 0, "HVAC Temperature Set", setOf(ACar.PERMISSION_CONTROL_CAR_CLIMATE))
        val HVAC_TEMPERATURE_VALUE_SUGGESTION = NVhalKey(291570965, 0, "HVAC Temperature Value Suggestion", setOf(ACar.PERMISSION_CONTROL_CAR_CLIMATE))
//        val HW_KEY_INPUT = NVhalKey(289475088, 0, "HW Key Input")
        val IGNITION_STATE = NVhalKey(289408009, 0, "Ignition State", setOf(ACar.PERMISSION_POWERTRAIN))
        val INFO_DRIVER_SEAT = NVhalKey(356516106, 0, "Info Driver Seat", setOf(ACar.PERMISSION_CAR_INFO))
        val INFO_EV_BATTERY_CAPACITY = NVhalKey(291504390, 0, "Info EV Battery Capacity", setOf(ACar.PERMISSION_CAR_INFO))
        val INFO_EV_CONNECTOR_TYPE = NVhalKey(289472775, 0, "Info EV Connector Type", setOf(ACar.PERMISSION_CAR_INFO))
        val INFO_EV_PORT_LOCATION = NVhalKey(289407241, 0, "Info EV Port Location", setOf(ACar.PERMISSION_CAR_INFO))
        val INFO_EXTERIOR_DIMENSIONS = NVhalKey(289472779, 0, "Info Exterior Dimensions", setOf(ACar.PERMISSION_CAR_INFO))
        val INFO_FUEL_CAPACITY = NVhalKey(291504388, 0, "Info Fuel Capacity", setOf(ACar.PERMISSION_CAR_INFO))
        val INFO_FUEL_DOOR_LOCATION = NVhalKey(289407240, 0, "Info Fuel Door Location", setOf(ACar.PERMISSION_CAR_INFO))
        val INFO_FUEL_TYPE = NVhalKey(289472773, 0, "Info Fuel Type", setOf(ACar.PERMISSION_CAR_INFO))
        val INFO_MAKE = NVhalKey(286261505, 0, "Info Make", setOf(ACar.PERMISSION_CAR_INFO))
        val INFO_MODEL = NVhalKey(286261506, 0, "Info Model", setOf(ACar.PERMISSION_CAR_INFO))
        val INFO_MODEL_YEAR = NVhalKey(289407235, 0, "Info Model Year", setOf(ACar.PERMISSION_CAR_INFO))
        val INFO_MULTI_EV_PORT_LOCATIONS = NVhalKey(289472780, 0, "Info Multi EV Port Locations", setOf(ACar.PERMISSION_CAR_INFO))
        val INFO_VIN = NVhalKey(286261504, 0, "Info VIN", setOf(ACar.PERMISSION_IDENTIFICATION))
        val INVALID = NVhalKey(0, 0, "Invalid")
        val LOCATION_CHARACTERIZATION = NVhalKey(289410064, 0, "Location Characterization", setOf(Manifest.permission.ACCESS_FINE_LOCATION))
        val MIRROR_FOLD = NVhalKey(287312709, 0, "Mirror Fold", setOf(ACar.PERMISSION_CONTROL_CAR_MIRRORS))
        val MIRROR_LOCK = NVhalKey(287312708, 0, "Mirror Lock", setOf(ACar.PERMISSION_CONTROL_CAR_MIRRORS))
        val MIRROR_Y_MOVE = NVhalKey(339741507, 0, "Mirror Y Move", setOf(ACar.PERMISSION_CONTROL_CAR_MIRRORS))
        val MIRROR_Y_POS = NVhalKey(339741506, 0, "Mirror Y Position", setOf(ACar.PERMISSION_CONTROL_CAR_MIRRORS))
        val MIRROR_Z_MOVE = NVhalKey(339741505, 0, "Mirror Z Move", setOf(ACar.PERMISSION_CONTROL_CAR_MIRRORS))
        val MIRROR_Z_POS = NVhalKey(339741504, 0, "Mirror Z Position", setOf(ACar.PERMISSION_CONTROL_CAR_MIRRORS))
        val NIGHT_MODE = NVhalKey(287310855, 0, "Night Mode", setOf(ACar.PERMISSION_EXTERIOR_ENVIRONMENT))
        val OBD2_FREEZE_FRAME = NVhalKey(299896065, 0, "OBD2 Freeze Frame", setOf("android.car.Car.PERMISSION_CAR_DIAGNOSTIC_READ_ALL"))
        val OBD2_FREEZE_FRAME_CLEAR = NVhalKey(299896067, 0, "OBD2 Freeze Frame Clear", setOf("android.car.Car.PERMISSION_CAR_DIAGNOSTIC_CLEAR"))
        val OBD2_FREEZE_FRAME_INFO = NVhalKey(299896066, 0, "OBD2 Freeze Frame Info", setOf("android.car.Car.PERMISSION_CAR_DIAGNOSTIC_READ_ALL"))
        val OBD2_LIVE_FRAME = NVhalKey(299896064, 0, "OBD2 Live Frame", setOf("android.car.Car.PERMISSION_CAR_DIAGNOSTIC_READ_ALL"))
        val PARKING_BRAKE_AUTO_APPLY = NVhalKey(287310851, 0, "Parking Brake Auto Apply", setOf(ACar.PERMISSION_POWERTRAIN))
        val PARKING_BRAKE_ON = NVhalKey(287310850, 0, "Parking Brake On", setOf(ACar.PERMISSION_POWERTRAIN))
        val PERF_ODOMETER = NVhalKey(291504644, 0, "Performance Odometer", setOf(ACar.PERMISSION_MILEAGE))
        val PERF_REAR_STEERING_ANGLE = NVhalKey(291504656, 0, "Performance Rear Steering Angle", setOf(ACar.PERMISSION_READ_STEERING_STATE))
        val PERF_STEERING_ANGLE = NVhalKey(291504649, 0, "Performance Steering Angle", setOf(ACar.PERMISSION_READ_STEERING_STATE))
        val PER_DISPLAY_BRIGHTNESS = NVhalKey(289475076, 0, "Performance Display Brightness", setOf("android.car.Car.PERMISSION_CAR_POWER"))
        val RANGE_REMAINING = NVhalKey(291504904, 0, "Range Remaining", setOf(ACar.PERMISSION_ENERGY))
        val READING_LIGHTS_STATE = NVhalKey(356519683, 0, "Reading Lights State", setOf(ACar.PERMISSION_READ_INTERIOR_LIGHTS))
        val READING_LIGHTS_SWITCH = NVhalKey(356519684, 0, "Reading Lights Switch", setOf(ACar.PERMISSION_CONTROL_INTERIOR_LIGHTS))
        val REAR_FOG_LIGHTS_STATE = NVhalKey(289410877, 0, "Rear Fog Lights State", setOf(ACar.PERMISSION_EXTERIOR_LIGHTS))
        val REAR_FOG_LIGHTS_SWITCH = NVhalKey(289410878, 0, "Rear Fog Lights Switch", setOf(ACar.PERMISSION_CONTROL_EXTERIOR_LIGHTS))
        val SEAT_BACKREST_ANGLE_1_MOVE = NVhalKey(356518792, 0, "Seat Backrest Angle 1 Move", setOf(ACar.PERMISSION_CONTROL_CAR_SEATS))
        val SEAT_BACKREST_ANGLE_1_POS = NVhalKey(356518791, 0, "Seat Backrest Angle 1 Position", setOf(ACar.PERMISSION_CONTROL_CAR_SEATS))
        val SEAT_BACKREST_ANGLE_2_MOVE = NVhalKey(356518794, 0, "Seat Backrest Angle 2 Move", setOf(ACar.PERMISSION_CONTROL_CAR_SEATS))
        val SEAT_BACKREST_ANGLE_2_POS = NVhalKey(356518793, 0, "Seat Backrest Angle 2 Position", setOf(ACar.PERMISSION_CONTROL_CAR_SEATS))
        val SEAT_BELT_BUCKLED = NVhalKey(354421634, 0, "Seat Belt Buckled", setOf(ACar.PERMISSION_CONTROL_CAR_SEATS))
        val SEAT_BELT_HEIGHT_MOVE = NVhalKey(356518788, 0, "Seat Belt Height Move", setOf(ACar.PERMISSION_CONTROL_CAR_SEATS))
        val SEAT_BELT_HEIGHT_POS = NVhalKey(356518787, 0, "Seat Belt Height Position", setOf(ACar.PERMISSION_CONTROL_CAR_SEATS))
        val SEAT_DEPTH_MOVE = NVhalKey(356518798, 0, "Seat Depth Move", setOf(ACar.PERMISSION_CONTROL_CAR_SEATS))
        val SEAT_DEPTH_POS = NVhalKey(356518797, 0, "Seat Depth Position", setOf(ACar.PERMISSION_CONTROL_CAR_SEATS))
        val SEAT_FOOTWELL_LIGHTS_STATE = NVhalKey(356518811, 0, "Seat Footwell Lights State", setOf(ACar.PERMISSION_READ_INTERIOR_LIGHTS))
        val SEAT_FOOTWELL_LIGHTS_SWITCH = NVhalKey(356518812, 0, "Seat Footwell Lights Switch", setOf(ACar.PERMISSION_CONTROL_INTERIOR_LIGHTS))
        val SEAT_FORE_AFT_MOVE = NVhalKey(356518790, 0, "Seat Fore-Aft Move", setOf(ACar.PERMISSION_CONTROL_CAR_SEATS))
        val SEAT_FORE_AFT_POS = NVhalKey(356518789, 0, "Seat Fore-Aft Position", setOf(ACar.PERMISSION_CONTROL_CAR_SEATS))
        val SEAT_HEADREST_ANGLE_MOVE = NVhalKey(356518808, 0, "Seat Headrest Angle Move", setOf(ACar.PERMISSION_CONTROL_CAR_SEATS))
        val SEAT_HEADREST_ANGLE_POS = NVhalKey(356518807, 0, "Seat Headrest Angle Position", setOf(ACar.PERMISSION_CONTROL_CAR_SEATS))
        val SEAT_HEADREST_FORE_AFT_MOVE = NVhalKey(356518810, 0, "Seat Headrest Fore-Aft Move", setOf(ACar.PERMISSION_CONTROL_CAR_SEATS))
        val SEAT_HEADREST_FORE_AFT_POS = NVhalKey(356518809, 0, "Seat Headrest Fore-Aft Position", setOf(ACar.PERMISSION_CONTROL_CAR_SEATS))
        val SEAT_HEADREST_HEIGHT_MOVE = NVhalKey(356518806, 0, "Seat Headrest Height Move", setOf(ACar.PERMISSION_CONTROL_CAR_SEATS))
        val SEAT_HEADREST_HEIGHT_POS = NVhalKey(289409941, 0, "Seat Headrest Height Position", setOf(ACar.PERMISSION_CONTROL_CAR_SEATS))
        val SEAT_HEADREST_HEIGHT_POS_V2 = NVhalKey(356518820, 0, "Seat Headrest Height Position V2", setOf(ACar.PERMISSION_CONTROL_CAR_SEATS))
        val SEAT_HEIGHT_MOVE = NVhalKey(356518796, 0, "Seat Height Move", setOf(ACar.PERMISSION_CONTROL_CAR_SEATS))
        val SEAT_HEIGHT_POS = NVhalKey(356518795, 0, "Seat Height Position", setOf(ACar.PERMISSION_CONTROL_CAR_SEATS))
        val SEAT_LUMBAR_FORE_AFT_MOVE = NVhalKey(356518802, 0, "Seat Lumbar Fore-Aft Move", setOf(ACar.PERMISSION_CONTROL_CAR_SEATS))
        val SEAT_LUMBAR_FORE_AFT_POS = NVhalKey(356518801, 0, "Seat Lumbar Fore-Aft Position", setOf(ACar.PERMISSION_CONTROL_CAR_SEATS))
        val SEAT_LUMBAR_SIDE_SUPPORT_MOVE = NVhalKey(356518804, 0, "Seat Lumbar Side Support Move", setOf(ACar.PERMISSION_CONTROL_CAR_SEATS))
        val SEAT_LUMBAR_SIDE_SUPPORT_POS = NVhalKey(356518803, 0, "Seat Lumbar Side Support Position", setOf(ACar.PERMISSION_CONTROL_CAR_SEATS))
        val SEAT_MEMORY_SELECT = NVhalKey(356518784, 0, "Seat Memory Select", setOf(ACar.PERMISSION_CONTROL_CAR_SEATS))
        val SEAT_MEMORY_SET = NVhalKey(356518785, 0, "Seat Memory Set", setOf(ACar.PERMISSION_CONTROL_CAR_SEATS))
        val SEAT_OCCUPANCY = NVhalKey(356518832, 0, "Seat Occupancy", setOf(ACar.PERMISSION_READ_CAR_SEATS))
        val SEAT_TILT_MOVE = NVhalKey(356518800, 0, "Seat Tilt Move", setOf(ACar.PERMISSION_CONTROL_CAR_SEATS))
        val SEAT_TILT_POS = NVhalKey(356518799, 0, "Seat Tilt Position", setOf(ACar.PERMISSION_CONTROL_CAR_SEATS))
        val STEERING_WHEEL_LIGHTS_STATE = NVhalKey(289410828, 0, "Steering Wheel Lights State", setOf(ACar.PERMISSION_READ_INTERIOR_LIGHTS))
        val STEERING_WHEEL_LIGHTS_SWITCH = NVhalKey(289410829, 0, "Steering Wheel Lights Switch", setOf(ACar.PERMISSION_CONTROL_INTERIOR_LIGHTS))
        val TIRE_PRESSURE = NVhalKey(392168201, 0, "Tire Pressure", setOf(ACar.PERMISSION_TIRES))
        val TIRE_PRESSURE_DISPLAY_UNITS = NVhalKey(289408514, 0, "Tire Pressure Display Units", setOf(ACar.PERMISSION_READ_DISPLAY_UNITS))
        val TRACTION_CONTROL_ACTIVE = NVhalKey(287310859, 0, "Traction Control Active", setOf(ACar.PERMISSION_CAR_DYNAMICS_STATE))
        val TRAILER_PRESENT = NVhalKey(289410885, 0, "Trailer Present", setOf(ACar.PERMISSION_PRIVILEGED_CAR_INFO))
        val TURN_SIGNAL_STATE = NVhalKey(289408008, 0, "Turn Signal State", setOf(ACar.PERMISSION_READ_EXTERIOR_LIGHTS))
        val VEHICLE_CURB_WEIGHT = NVhalKey(289410886, 0, "Vehicle Curb Weight", setOf(ACar.PERMISSION_CAR_INFO))
        val VEHICLE_MAP_SERVICE = NVhalKey(299895808, 0, "Vehicle Map Service", setOf("android.car.Car.PERMISSION_VMS_SUBSCRIBER"))
        val VEHICLE_SPEED_DISPLAY_UNITS = NVhalKey(289408516, 0, "Vehicle Speed Display Units", setOf(ACar.PERMISSION_READ_DISPLAY_UNITS))
        val WHEEL_TICK = NVhalKey(290521862, 0, "Wheel Tick", setOf(ACar.PERMISSION_SPEED))
        val WINDOW_LOCK = NVhalKey(320867268, 0, "Window Lock", setOf(ACar.PERMISSION_CONTROL_CAR_WINDOWS))
        val WINDOW_MOVE = NVhalKey(322964417, 0, "Window Move", setOf(ACar.PERMISSION_CONTROL_CAR_WINDOWS))
        val WINDOW_POS = NVhalKey(322964416, 0, "Window Position", setOf(ACar.PERMISSION_CONTROL_CAR_WINDOWS))

        // TODO(umur): These are TOGG specific vendor properties???
        val VENDOR_DRIVE_MODE_PROPERTY = NVhalKey(557842693, 0, "Vendor Drive Mode Property")
        val VENDOR_CABIN_CURRENT_TEMP_DEG_PROPERTY = NVhalKey(559939846, 0, "Vendor Cabin Current Temp Deg Property")
        val VENDOR_SOC_BATTERY_LEVEL_PROPERTY = NVhalKey(559939847, 0, "Vendor Soc Battery Level Property")
        val VENDOR_CRUISE_CONTROL_STATUS = NVhalKey(557842696, 0, "Vendor Cruise Control Status")
        val VENDOR_AMBIENT_LIGHT_READ = NVhalKey(557842697, 0, "Vendor Ambient Light Property") // VENDOR_AMBIENT_LIGH
        val VENDOR_AMBIENT_LIGHT_WRITE = NVhalKey(557842961, 0, "Vendor Ambient Light Property") // VENDOR_AMBIENT_LIGHT_REQ

        val all = arrayOf(
            PERF_VEHICLE_SPEED,
            PERF_VEHICLE_SPEED_DISPLAY,
            ABS_ACTIVE,
            AP_POWER_BOOTUP_REASON,
            AP_POWER_STATE_REPORT,
            AP_POWER_STATE_REQ,
            CABIN_LIGHTS_STATE,
            CABIN_LIGHTS_SWITCH,
            CRITICALLY_LOW_TIRE_PRESSURE,
            CURRENT_GEAR,
            DISPLAY_BRIGHTNESS,
            DISTANCE_DISPLAY_UNITS,
            DOOR_LOCK,
            DOOR_MOVE,
            DOOR_POS,
            ELECTRONIC_TOLL_COLLECTION_CARD_STATUS,
            ELECTRONIC_TOLL_COLLECTION_CARD_TYPE,
            ENGINE_COOLANT_TEMP,
            ENGINE_OIL_LEVEL,
            ENGINE_OIL_TEMP,
            ENGINE_RPM,
            ENV_OUTSIDE_TEMPERATURE,
            EPOCH_TIME,
            EV_BATTERY_DISPLAY_UNITS,
            EV_BATTERY_INSTANTANEOUS_CHARGE_RATE,
            EV_BATTERY_LEVEL,
            EV_BRAKE_REGENERATION_LEVEL,
            EV_CHARGE_CURRENT_DRAW_LIMIT,
            EV_CHARGE_PERCENT_LIMIT,
            EV_CHARGE_PORT_CONNECTED,
            EV_CHARGE_PORT_OPEN,
            EV_CHARGE_STATE,
            EV_CHARGE_SWITCH,
            EV_CHARGE_TIME_REMAINING,
            EV_CURRENT_BATTERY_CAPACITY,
            EV_REGENERATIVE_BRAKING_STATE,
            EV_STOPPING_MODE,
            FOG_LIGHTS_STATE,
            FOG_LIGHTS_SWITCH,
            FRONT_FOG_LIGHTS_STATE,
            FRONT_FOG_LIGHTS_SWITCH,
            FUEL_CONSUMPTION_UNITS_DISTANCE_OVER_VOLUME,
            FUEL_DOOR_OPEN,
            FUEL_LEVEL,
            FUEL_LEVEL_LOW,
            FUEL_VOLUME_DISPLAY_UNITS,
            GEAR_SELECTION,
            GENERAL_SAFETY_REGULATION_COMPLIANCE,
            HAZARD_LIGHTS_STATE,
            HAZARD_LIGHTS_SWITCH,
            HEADLIGHTS_STATE,
            HEADLIGHTS_SWITCH,
            HIGH_BEAM_LIGHTS_STATE,
            HIGH_BEAM_LIGHTS_SWITCH,
            HVAC_ACTUAL_FAN_SPEED_RPM,
            HVAC_AC_ON,
            HVAC_AUTO_ON,
            HVAC_AUTO_RECIRC_ON,
            HVAC_DEFROSTER,
            HVAC_DUAL_ON,
            HVAC_FAN_DIRECTION,
            HVAC_FAN_DIRECTION_AVAILABLE,
            HVAC_FAN_SPEED,
            HVAC_MAX_AC_ON,
            HVAC_MAX_DEFROST_ON,
            HVAC_POWER_ON,
            HVAC_RECIRC_ON,
            HVAC_SEAT_TEMPERATURE,
            HVAC_SEAT_VENTILATION,
            HVAC_SIDE_MIRROR_HEAT,
            HVAC_STEERING_WHEEL_HEAT,
            HVAC_TEMPERATURE_CURRENT,
            HVAC_TEMPERATURE_DISPLAY_UNITS,
            HVAC_TEMPERATURE_SET,
            HVAC_TEMPERATURE_VALUE_SUGGESTION,
//            HW_KEY_INPUT,
            IGNITION_STATE,
            INFO_DRIVER_SEAT,
            INFO_EV_BATTERY_CAPACITY,
            INFO_EV_CONNECTOR_TYPE,
            INFO_EV_PORT_LOCATION,
            INFO_EXTERIOR_DIMENSIONS,
            INFO_FUEL_CAPACITY,
            INFO_FUEL_DOOR_LOCATION,
            INFO_FUEL_TYPE,
            INFO_MAKE,
            INFO_MODEL,
            INFO_MODEL_YEAR,
            INFO_MULTI_EV_PORT_LOCATIONS,
            INFO_VIN,
            INVALID,
            LOCATION_CHARACTERIZATION,
            MIRROR_FOLD,
            MIRROR_LOCK,
            MIRROR_Y_MOVE,
            MIRROR_Y_POS,
            MIRROR_Z_MOVE,
            MIRROR_Z_POS,
            NIGHT_MODE,
            OBD2_FREEZE_FRAME,
            OBD2_FREEZE_FRAME_CLEAR,
            OBD2_FREEZE_FRAME_INFO,
            OBD2_LIVE_FRAME,
            PARKING_BRAKE_AUTO_APPLY,
            PARKING_BRAKE_ON,
            PERF_ODOMETER,
            PERF_REAR_STEERING_ANGLE,
            PERF_STEERING_ANGLE,
            PER_DISPLAY_BRIGHTNESS,
            RANGE_REMAINING,
            READING_LIGHTS_STATE,
            READING_LIGHTS_SWITCH,
            REAR_FOG_LIGHTS_STATE,
            REAR_FOG_LIGHTS_SWITCH,
            SEAT_BACKREST_ANGLE_1_MOVE,
            SEAT_BACKREST_ANGLE_1_POS,
            SEAT_BACKREST_ANGLE_2_MOVE,
            SEAT_BACKREST_ANGLE_2_POS,
            SEAT_BELT_BUCKLED,
            SEAT_BELT_HEIGHT_MOVE,
            SEAT_BELT_HEIGHT_POS,
            SEAT_DEPTH_MOVE,
            SEAT_DEPTH_POS,
            SEAT_FOOTWELL_LIGHTS_STATE,
            SEAT_FOOTWELL_LIGHTS_SWITCH,
            SEAT_FORE_AFT_MOVE,
            SEAT_FORE_AFT_POS,
            SEAT_HEADREST_ANGLE_MOVE,
            SEAT_HEADREST_ANGLE_POS,
            SEAT_HEADREST_FORE_AFT_MOVE,
            SEAT_HEADREST_FORE_AFT_POS,
            SEAT_HEADREST_HEIGHT_MOVE,
            SEAT_HEADREST_HEIGHT_POS,
            SEAT_HEADREST_HEIGHT_POS_V2,
            SEAT_HEIGHT_MOVE,
            SEAT_HEIGHT_POS,
            SEAT_LUMBAR_FORE_AFT_MOVE,
            SEAT_LUMBAR_FORE_AFT_POS,
            SEAT_LUMBAR_SIDE_SUPPORT_MOVE,
            SEAT_LUMBAR_SIDE_SUPPORT_POS,
            SEAT_MEMORY_SELECT,
            SEAT_MEMORY_SET,
            SEAT_OCCUPANCY,
            SEAT_TILT_MOVE,
            SEAT_TILT_POS,
            STEERING_WHEEL_LIGHTS_STATE,
            STEERING_WHEEL_LIGHTS_SWITCH,
            TIRE_PRESSURE,
            TIRE_PRESSURE_DISPLAY_UNITS,
            TRACTION_CONTROL_ACTIVE,
            TRAILER_PRESENT,
            TURN_SIGNAL_STATE,
            VEHICLE_CURB_WEIGHT,
            VEHICLE_MAP_SERVICE,
            VEHICLE_SPEED_DISPLAY_UNITS,
            WHEEL_TICK,
            WINDOW_LOCK,
            WINDOW_MOVE,
            WINDOW_POS,
            VENDOR_DRIVE_MODE_PROPERTY,
            VENDOR_CABIN_CURRENT_TEMP_DEG_PROPERTY,
            VENDOR_SOC_BATTERY_LEVEL_PROPERTY,
            VENDOR_CRUISE_CONTROL_STATUS,
            VENDOR_AMBIENT_LIGHT_READ,
            VENDOR_AMBIENT_LIGHT_WRITE,
        )
    }
}