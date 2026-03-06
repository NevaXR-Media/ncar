package com.nevaxr.foundation.car.device.deviceData

const val DEFAULT_DEVICE_DATA = """{
  "devices": [
    {
      "name": "Togg T10X RWD",
      "key": "TOGG_T10X_RWD",
      "features": [
        {
          "name": "Speed",
          "key": "SENSOR:SPEED:DEVICE",
          "range": [0, 51.3889],
          "type": "float",
          "unit": "MPS",
          "readMethod": "VHAL"
        },
        {
          "name": "Engine",
          "key": "SENSOR:ENGINE:DEVICE",
          "type": "float",
          "range": [0, 15000],
          "unit": "KW",
          "readMethod": "VHAL"
        },
        {
          "name": "Battery Capacity",
          "key": "SENSOR:BATTERY_CAPACITY:DEVICE",
          "range": [0, 88.5],
          "type": "float",
          "unit": "KWH",
          "readMethod": "VHAL"
        },
        {
          "name": "Driving Mode",
          "key": "SENSOR:DRIVING_MODE:DEVICE",
          "type": "string",
          "values": [
            {
              "name": "ECO",
              "value": 0
            },
            {
              "name": "COMFORT",
              "value": 1
            },
            {
              "name": "SPORT",
              "value": 2
            }
          ],
          "readMethod": "VHAL"
        },
        {
          "name": "Gear State",
          "key": "SENSOR:GEAR:DEVICE",
          "type": "string",
          "values": [
            {
              "name": "PARK",
              "value": 4
            },
            {
              "name": "REVERSE",
              "value": 2
            },
            {
              "name": "NEUTRAL",
              "value": 1
            },
            {
              "name": "DRIVE",
              "value": 8
            }
          ],
          "readMethod": "VHAL"
        },
        {
          "name": "Battery Charge Rate",
          "key": "SENSOR:BATTERY_CHARGE_RATE:DEVICE",
          "unit": "MW",
          "range": [0, 175000000],
          "type": "float",
          "readMethod": "VHAL"
        },
        {
          "name": "Reverse Battery Charge Rate",
          "key": "SENSOR:REVERSE_BATTERY_CHARGE_RATE:DEVICE",
          "unit": "MW",
          "range": [0, 175000000],
          "type": "float",
          "readMethod": "VHAL"
        },
        {
          "name": "HVAC Temperature",
          "key": "SENSOR:HVAC_TEMPERATURE:DEVICE",
          "values": [
            {
              "name": "AREA_ID",
              "value": 49
            }
          ],
          "unit": "C",
          "type": "float",
          "readMethod": "VHAL"
        },
        {
          "name": "HVAC Passenger Temperature",
          "key": "SENSOR:HVAC_PASSENGER_TEMPERATURE:DEVICE",
          "values": [
            {
              "name": "AREA_ID",
              "value": 68
            }
          ],
          "unit": "C",
          "type": "float",
          "readMethod": "VHAL"
        },
        {
          "name": "HVAC Fan Speed",
          "key": "SENSOR:HVAC_FAN_SPEED:DEVICE",
          "unit": "RPM",
          "type": "float",
          "range": [0, 7],
          "readMethod": "VHAL"
        },
        {
          "name": "HVAC Passenger Fan Speed",
          "key": "SENSOR:HVAC_PASSENGER_FAN_SPEED:DEVICE",
          "unit": "RPM",
          "type": "float",
          "range": [0, 7],
          "readMethod": "VHAL"
        },
        {
          "name": "HVAC Interior Temperature",
          "key": "SENSOR:HVAC_INTERIOR_TEMPERATURE:DEVICE",
          "unit": "C",
          "type": "float",
          "readMethod": "VHAL",
          "readId": "559939846"
        },
        {
          "name": "HVAC Exterior Temperature",
          "key": "SENSOR:HVAC_EXTERIOR_TEMPERATURE:DEVICE",
          "unit": "C",
          "type": "float",
          "readMethod": "VHAL"
        },
        {
          "name": "Doors' State",
          "key": "SENSOR:DOORS_STATE:DEVICE",
          "type": "array",
          "elementType": "boolean",
          "values": [
            {
              "name": "LEFT_TOP_AREA_ID",
              "value": 1
            },
            {
              "name": "RIGHT_TOP_AREA_ID",
              "value": 4
            },
            {
              "name": "LEFT_BACK_AREA_ID",
              "value": 16
            },
            {
              "name": "RIGHT_BACK_AREA_ID",
              "value": 64
            }
          ]
        },
        {
          "name": "Trunk State",
          "key": "SENSOR:TRUNK_STATE:DEVICE",
          "type": "boolean",
          "values": [
            {
              "name": "AREA_ID",
              "value": 536870912
            }
          ]
        },
        {
          "name": "Frunk State",
          "key": "SENSOR:FRUNK_STATE:DEVICE",
          "type": "boolean",
          "values": [
            {
              "name": "AREA_ID",
              "value": 268435456
            }
          ]
        },
        {
          "name": "Windows' State",
          "key": "SENSOR:WINDOWS_STATE:DEVICE",
          "type": "array",
          "elementType": "float",
          "range": [0, 100],
          "unit": "%",
          "values": [
            {
              "name": "LEFT_TOP_AREA_ID",
              "value": 16
            },
            {
              "name": "RIGHT_TOP_AREA_ID",
              "value": 64
            },
            {
              "name": "LEFT_BACK_AREA_ID",
              "value": 256
            },
            {
              "name": "RIGHT_BACK_AREA_ID",
              "value": 1024
            }
          ]
        }
      ]
    },
    {
      "name": "Togg T10X AWD",
      "key": "TOGG_T10X_AWD",
      "features": [
        {
          "name": "Speed",
          "key": "SENSOR:SPEED:DEVICE",
          "range": [0, 51.3889],
          "type": "float",
          "unit": "MPS",
          "readMethod": "VHAL"
        },
        {
          "name": "Engine",
          "key": "SENSOR:ENGINE:DEVICE",
          "type": "float",
          "range": [0, 15000],
          "unit": "KW",
          "readMethod": "VHAL"
        },
        {
          "name": "Battery Capacity",
          "key": "SENSOR:BATTERY_CAPACITY:DEVICE",
          "range": [0, 88.5],
          "type": "float",
          "unit": "KWH",
          "readMethod": "VHAL"
        },
        {
          "name": "Driving Mode",
          "key": "SENSOR:DRIVING_MODE:DEVICE",
          "type": "string",
          "values": [
            {
              "name": "ECO",
              "value": 0
            },
            {
              "name": "COMFORT",
              "value": 1
            },
            {
              "name": "SPORT",
              "value": 2
            }
          ],
          "readMethod": "VHAL"
        },
        {
          "name": "Gear State",
          "key": "SENSOR:GEAR:DEVICE",
          "type": "string",
          "values": [
            {
              "name": "PARK",
              "value": 4
            },
            {
              "name": "REVERSE",
              "value": 2
            },
            {
              "name": "NEUTRAL",
              "value": 1
            },
            {
              "name": "DRIVE",
              "value": 8
            }
          ],
          "readMethod": "VHAL"
        },
        {
          "name": "Battery Charge Rate",
          "key": "SENSOR:BATTERY_CHARGE_RATE:DEVICE",
          "unit": "MW",
          "range": [0, 175000000],
          "type": "float",
          "readMethod": "VHAL"
        },
        {
          "name": "Reverse Battery Charge Rate",
          "key": "SENSOR:REVERSE_BATTERY_CHARGE_RATE:DEVICE",
          "unit": "MW",
          "range": [0, 175000000],
          "type": "float",
          "readMethod": "VHAL"
        },
        {
          "name": "HVAC Temperature",
          "key": "SENSOR:HVAC_TEMPERATURE:DEVICE",
          "values": [
            {
              "name": "AREA_ID",
              "value": 49
            }
          ],
          "unit": "C",
          "type": "float",
          "readMethod": "VHAL"
        },
        {
          "name": "HVAC Passenger Temperature",
          "key": "SENSOR:HVAC_PASSENGER_TEMPERATURE:DEVICE",
          "values": [
            {
              "name": "AREA_ID",
              "value": 68
            }
          ],
          "unit": "C",
          "type": "float",
          "readMethod": "VHAL"
        },
        {
          "name": "HVAC Fan Speed",
          "key": "SENSOR:HVAC_FAN_SPEED:DEVICE",
          "unit": "RPM",
          "type": "float",
          "range": [0, 7],
          "readMethod": "VHAL"
        },
        {
          "name": "HVAC Passenger Fan Speed",
          "key": "SENSOR:HVAC_PASSENGER_FAN_SPEED:DEVICE",
          "unit": "RPM",
          "type": "float",
          "range": [0, 7],
          "readMethod": "VHAL"
        },
        {
          "name": "HVAC Interior Temperature",
          "key": "SENSOR:HVAC_INTERIOR_TEMPERATURE:DEVICE",
          "unit": "C",
          "type": "float",
          "readMethod": "VHAL",
          "readId": "559939846"
        },
        {
          "name": "HVAC Exterior Temperature",
          "key": "SENSOR:HVAC_EXTERIOR_TEMPERATURE:DEVICE",
          "unit": "C",
          "type": "float",
          "readMethod": "VHAL"
        },
        {
          "name": "Doors' State",
          "key": "SENSOR:DOORS_STATE:DEVICE",
          "type": "array",
          "elementType": "boolean",
          "values": [
            {
              "name": "LEFT_TOP_AREA_ID",
              "value": 1
            },
            {
              "name": "RIGHT_TOP_AREA_ID",
              "value": 4
            },
            {
              "name": "LEFT_BACK_AREA_ID",
              "value": 16
            },
            {
              "name": "RIGHT_BACK_AREA_ID",
              "value": 64
            }
          ]
        },
        {
          "name": "Trunk State",
          "key": "SENSOR:TRUNK_STATE:DEVICE",
          "type": "boolean",
          "values": [
            {
              "name": "AREA_ID",
              "value": 536870912
            }
          ]
        },
        {
          "name": "Frunk State",
          "key": "SENSOR:FRUNK_STATE:DEVICE",
          "type": "boolean",
          "values": [
            {
              "name": "AREA_ID",
              "value": 268435456
            }
          ]
        },
        {
          "name": "Windows' State",
          "key": "SENSOR:WINDOWS_STATE:DEVICE",
          "type": "array",
          "elementType": "float",
          "range": [0, 100],
          "unit": "%",
          "values": [
            {
              "name": "LEFT_TOP_AREA_ID",
              "value": 16
            },
            {
              "name": "RIGHT_TOP_AREA_ID",
              "value": 64
            },
            {
              "name": "LEFT_BACK_AREA_ID",
              "value": 256
            },
            {
              "name": "RIGHT_BACK_AREA_ID",
              "value": 1024
            }
          ]
        }
      ]
    }
  ],
  "features": [
    {
      "name": "Magic",
      "key": "SENSOR:MAGIC:GENERIC",
      "type": "float",
      "range": [0, 1]
    },
    {
      "name": "Speed",
      "key": "SENSOR:SPEED:GENERIC",
      "type": "float",
      "range": [0, 1]
    },
    {
      "name": "Acceleration",
      "key": "SENSOR:ACCELERATION:GENERIC",
      "type": "float",
      "range": [0, 1]
    },
    {
      "name": "Reverse Acceleration",
      "key": "SENSOR:REVERSE_ACCELERATION:GENERIC",
      "type": "float",
      "range": [0, 1]
    },
    {
      "name": "Direction X",
      "key": "SENSOR:DIRECTION_X:GENERIC",
      "type": "float",
      "range": [0, 1]
    },
    {
      "name": "Direction Y",
      "key": "SENSOR:DIRECTION_Y:GENERIC",
      "type": "float",
      "range": [0, 1]
    },
    {
      "name": "Direction Z",
      "key": "SENSOR:DIRECTION_Z:GENERIC",
      "type": "float",
      "range": [0, 1]
    },
    {
      "name": "Location",
      "key": "SENSOR:LOCATION:GENERIC",
      "unit": "DEGREE",
      "type": "array",
      "elementType": "float"
    },
    {
      "name": "Altitude",
      "key": "SENSOR:ALTITUDE:GENERIC",
      "type": "float",
      "range": [0, 10000]
    },
    {
      "name": "Nearby Terrain",
      "key": "SENSOR:NEARBY_TERRAIN:GENERIC",
      "type": "string",
      "values": [
        "CITY",
        "RURAL",
        "MOUNTAIN",
        "BEACH",
        "LAKE",
        "OCEAN",
        "DESERT",
        "FOREST",
        "VOLCANO",
        "ANCIENT",
        "SPACE",
        "OTHER"
      ]
    },
    {
      "name": "Weather Condition",
      "key": "SENSOR:WEATHER_CONDITION:GENERIC",
      "type": "string",
      "values": [
        "BLOWINGDUST",
        "CLEAR",
        "CLOUDY",
        "FOGGY",
        "HAZE",
        "MOSTLYCLEAR",
        "MOSTLYCLOUDY",
        "PARTLYCLOUDY",
        "SMOKY",
        "BREEZY",
        "WINDY",
        "DRIZZLE",
        "HEAVYRAIN",
        "ISOLATEDTHUNDERSTORMS",
        "RAIN",
        "SUNSHOWERS",
        "SCATTEREDTHUNDERSTORMS",
        "STRONGSTORMS",
        "THUNDERSTORMS",
        "FRIGID",
        "HAIL",
        "HOT",
        "FLURRIES",
        "SLEET",
        "SNOW",
        "SUNFLURRIES",
        "WINTRYMIX",
        "BLIZZARD",
        "BLOWINGSNOW",
        "FREEZINGDRIZZLE",
        "FREEZINGRAIN",
        "HEAVYSNOW",
        "HURRICANE",
        "TROPICALSTORM"
      ]
    },
    {
      "name": "Weather Temperature",
      "key": "SENSOR:WEATHER_TEMPERATURE:GENERIC",
      "unit": "C",
      "type": "float"
    },
    {
      "name": "Weather Low Temperature",
      "key": "SENSOR:WEATHER_LOW_TEMPERATURE:GENERIC",
      "unit": "C",
      "type": "float"
    },
    {
      "name": "Weather High Temperature",
      "key": "SENSOR:WEATHER_HIGH_TEMPERATURE:GENERIC",
      "unit": "C",
      "type": "float"
    },
    {
      "name": "Weather Humidity",
      "key": "SENSOR:WEATHER_HUMIDITY:GENERIC",
      "unit": "%",
      "type": "float"
    },
    {
      "name": "Weather Wind Speed",
      "key": "SENSOR:WEATHER_WIND_SPEED:GENERIC",
      "unit": "KMH",
      "type": "float"
    },
    {
      "name": "Weather Wind Direction",
      "key": "SENSOR:WEATHER_WIND_DIRECTION:GENERIC",
      "unit": "DEGREE",
      "type": "float"
    },
    {
      "name": "Weather Precipitation",
      "key": "SENSOR:WEATHER_PRECIPITATION:GENERIC",
      "unit": "MM",
      "type": "float"
    },
    {
      "name": "Weather Precipitation Type",
      "key": "SENSOR:WEATHER_PRECIPITATION_TYPE:GENERIC",
      "type": "string",
      "values": ["RAIN", "SNOW", "SLEET", "HAIL"]
    },
    {
      "name": "Weather UV Index",
      "key": "SENSOR:WEATHER_UV_INDEX:GENERIC",
      "unit": "UV",
      "type": "float"
    },
    {
      "name": "Reverse Speed",
      "key": "SENSOR:REVERSE_SPEED:NORMALIZED",
      "type": "float",
      "range": [0, 1]
    },
    {
      "name": "Speed",
      "key": "SENSOR:SPEED:NORMALIZED",
      "type": "float",
      "range": [0, 1]
    },
    {
      "name": "Throttle Pedal Position",
      "key": "SENSOR:THROTTLE:NORMALIZED",
      "range": [0, 1],
      "type": "float"
    },
    {
      "name": "Brake Pedal Position",
      "key": "SENSOR:BRAKE:NORMALIZED",
      "range": [0, 1],
      "type": "float"
    },
    {
      "name": "Engine RPM",
      "key": "SENSOR:RPM:NORMALIZED",
      "unit": "RPM",
      "type": "float"
    },
    {
      "name": "Ignition State",
      "key": "SENSOR:IGNITION:NORMALIZED",
      "type": "boolean"
    },
    {
      "name": "Driving Mode",
      "key": "SENSOR:DRIVING_MODE:NORMALIZED",
      "type": "string",
      "values": [
        "ECO",
        "COMFORT",
        "SPORT",
        "TRACK",
        "SNOW",
        "SAND",
        "MUD",
        "ROCK",
        "EXTREME"
      ]
    },
    {
      "name": "Gear State",
      "key": "SENSOR:GEAR:NORMALIZED",
      "type": "string",
      "values": ["PARK", "REVERSE", "NEUTRAL", "DRIVE"]
    },
    {
      "name": "Day Status",
      "key": "SENSOR:DAY_STATUS:NORMALIZED",
      "type": "string",
      "values": ["MORNING", "AFTERNOON", "EVENING", "NIGHT"]
    },
    {
      "name": "Driving Mode",
      "key": "SENSOR:DRIVING_MODE:RAW",
      "type": "string"
    },
    {
      "name": "Reverse Speed",
      "key": "SENSOR:REVERSE_SPEED:RAW",
      "unit": "KMH",
      "type": "float"
    },
    {
      "name": "Speed",
      "key": "SENSOR:SPEED:RAW",
      "unit": "KMH",
      "type": "float"
    },
    {
      "name": "Throttle Pedal Position",
      "key": "SENSOR:THROTTLE:RAW",
      "range": [0, 175000000],
      "type": "float"
    },
    {
      "name": "Brake Pedal Position",
      "key": "SENSOR:BRAKE:RAW",
      "range": [0, 175000000],
      "type": "float"
    },
    {
      "name": "Engine RPM",
      "key": "SENSOR:RPM:RAW",
      "unit": "RPM",
      "type": "float"
    },
    {
      "name": "Battery Percentage",
      "key": "SENSOR:BATTERY:RAW",
      "unit": "%",
      "type": "float"
    },
    {
      "name": "Battery Charge Rate",
      "key": "SENSOR:BATTERY_CHARGE_RATE:RAW",
      "unit": "MW",
      "range": [0, 175000000],
      "type": "float"
    },
    {
      "name": "Reverse Battery Charge Rate",
      "key": "SENSOR:REVERSE_BATTERY_CHARGE_RATE:RAW",
      "unit": "MW",
      "range": [0, 175000000],
      "type": "float"
    },
    {
      "name": "HVAC Status",
      "key": "SENSOR:HVAC_STATUS:RAW",
      "type": "boolean"
    },
    {
      "name": "HVAC Dual Status",
      "key": "SENSOR:HVAC_DUAL_STATUS:RAW",
      "type": "boolean"
    },
    {
      "name": "HVAC Temperature",
      "key": "SENSOR:HVAC_TEMPERATURE:RAW",
      "unit": "C",
      "type": "float"
    },
    {
      "name": "HVAC Passenger Temperature",
      "key": "SENSOR:HVAC_PASSENGER_TEMPERATURE:RAW",
      "unit": "C",
      "type": "float"
    },
    {
      "name": "HVAC Fan Speed",
      "key": "SENSOR:HVAC_FAN_SPEED:RAW",
      "unit": "RPM",
      "type": "float"
    },
    {
      "name": "HVAC Passenger Fan Speed",
      "key": "SENSOR:HVAC_PASSENGER_FAN_SPEED:RAW",
      "unit": "RPM",
      "type": "float"
    },
    {
      "name": "HVAC Maximum",
      "key": "SENSOR:HVAC_MAX:RAW",
      "type": "boolean"
    },
    {
      "name": "HVAC Interior Temperature",
      "key": "SENSOR:HVAC_INTERIOR_TEMPERATURE:RAW",
      "unit": "C",
      "type": "float"
    },
    {
      "name": "HVAC Exterior Temperature",
      "key": "SENSOR:HVAC_EXTERIOR_TEMPERATURE:RAW",
      "unit": "C",
      "type": "float"
    },
    {
      "name": "Seat Occupancy",
      "key": "SENSOR:SEAT_OCCUPANCY:RAW",
      "type": "array",
      "elementType": "boolean"
    },
    {
      "name": "Steering Wheel Angle",
      "key": "SENSOR:STEERING_WHEEL_ANGLE:RAW",
      "unit": "DEGREE",
      "type": "float"
    },
    {
      "name": "Doors' State",
      "key": "SENSOR:DOORS_STATE:RAW",
      "type": "array",
      "elementType": "boolean"
    },
    {
      "name": "Windows' State",
      "key": "SENSOR:WINDOWS_STATE:RAW",
      "type": "array",
      "elementType": "float",
      "range": [0, 100],
      "unit": "%"
    },
    {
      "name": "Trunk State",
      "key": "SENSOR:TRUNK_STATE:RAW",
      "type": "boolean"
    },
    {
      "name": "Frunk State",
      "key": "SENSOR:FRUNK_STATE:RAW",
      "type": "boolean"
    },
    {
      "name": "Trunk Open Angle",
      "key": "SENSOR:TRUNK_OPEN_ANGLE:RAW",
      "unit": "DEGREE",
      "type": "float"
    },
    {
      "name": "Frunk Open Angle",
      "key": "SENSOR:FRUNK_OPEN_ANGLE:RAW",
      "unit": "DEGREE",
      "type": "float"
    },
    {
      "name": "Bed Cover Position",
      "key": "SENSOR:BED_COVER_POSITION:RAW",
      "type": "float",
      "range": [0, 100],
      "unit": "%"
    },
    {
      "name": "Beam State",
      "key": "SENSOR:LIGHTS_BEAM:RAW",
      "type": "boolean"
    },
    {
      "name": "Headlights State",
      "key": "SENSOR:LIGHTS_HEADLIGHTS:RAW",
      "type": "boolean"
    },
    {
      "name": "Brake Lights State",
      "key": "SENSOR:LIGHTS_BRAKE:RAW",
      "type": "boolean"
    },
    {
      "name": "Turn Signals State",
      "key": "SENSOR:LIGHTS_TURNSIGNALS:RAW",
      "type": "boolean"
    },
    {
      "name": "Engine",
      "key": "SENSOR:ENGINE:STATIC",
      "unit": "KW",
      "type": "float"
    },
    {
      "name": "Battery Capacity",
      "key": "SENSOR:BATTERY_CAPACITY:STATIC",
      "unit": "KWH",
      "type": "float"
    }
  ]
}"""