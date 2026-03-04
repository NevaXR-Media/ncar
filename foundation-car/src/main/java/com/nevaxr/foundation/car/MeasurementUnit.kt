package com.nevaxr.foundation.car

import kotlin.math.PI
import kotlin.math.pow

const val SecondsPerHour = 60f * 60f

interface MeasurementUnit {
    /// Unit's name as resource id
    val symbolRes: Int
}

interface Dimension<T> : MeasurementUnit {
    val converter: UnitConverter
    fun baseUnit(): Dimension<T>

    fun convert(value: Float, targetUnit: Dimension<T>): Float {
        val baseValue = converter.baseUnitValue(value)
        val result = targetUnit.converter.value(baseValue)
        return result
    }
}

interface UnitConverter {
    fun baseUnitValue(value: Float): Float
    fun value(baseUnitValue: Float): Float
}

data class UnitConverterLinear(val coefficient: Float, val constant: Float = 0f) : UnitConverter {
    override fun baseUnitValue(value: Float) = value * coefficient + constant
    override fun value(baseUnitValue: Float) = (baseUnitValue - constant) / coefficient
}

data class UnitAngle(override val symbolRes: Int, override val converter: UnitConverter) : Dimension<UnitAngle> {
    override fun baseUnit() = degrees

    private object Symbol {
        val degrees = R.string.unit_degrees
        val arcMinutes = R.string.unit_arcMinutes
        val arcSeconds = R.string.unit_arcSeconds
        val radians = R.string.unit_radians
        val gradians = R.string.unit_gradians
        val revolutions = R.string.unit_revolutions
    }

    private object Coefficient {
        val degrees = 1f
        val arcMinutes = 1f / 60f
        val arcSeconds = 1f / 3600f
        val radians = 180f / PI.toFloat()
        val gradians = 0.9f
        val revolutions = 360f
    }

    private constructor(symbol: Int, coefficient: Float) : this(symbol, UnitConverterLinear(coefficient))

    companion object {
        val degrees get() = UnitAngle(Symbol.degrees, Coefficient.degrees)
        val arcMinutes get() = UnitAngle(Symbol.arcMinutes, Coefficient.arcMinutes)
        val arcSeconds get() = UnitAngle(Symbol.arcSeconds, Coefficient.arcSeconds)
        val radians get() = UnitAngle(Symbol.radians, Coefficient.radians)
        val gradians get() = UnitAngle(Symbol.gradians, Coefficient.gradians)
        val revolutions get() = UnitAngle(Symbol.revolutions, Coefficient.revolutions)
    }
}

data class UnitArea(override val symbolRes: Int, override val converter: UnitConverter) : Dimension<UnitArea> {
    override fun baseUnit() = squareMeters

    private object Symbol {
        val squareMegameters = R.string.unit_squareMegameters
        val squareKilometers = R.string.unit_squareKilometers
        val squareMeters = R.string.unit_squareMeters
        val squareCentimeters = R.string.unit_squareCentimeters
        val squareMillimeters = R.string.unit_squareMillimeters
        val squareMicrometers = R.string.unit_squareMicrometers
        val squareNanometers = R.string.unit_squareNanometers
        val squareInches = R.string.unit_squareInches
        val squareFeet = R.string.unit_squareFeet
        val squareYards = R.string.unit_squareYards
        val squareMiles = R.string.unit_squareMiles
        val acres = R.string.unit_acres
        val ares = R.string.unit_ares
        val hectares = R.string.unit_hectares
    }

    private object Coefficient {
        val squareMegameters = 1e12f
        val squareKilometers = 1e6f
        val squareMeters = 1.0f
        val squareCentimeters = 1e-4f
        val squareMillimeters = 1e-6f
        val squareMicrometers = 1e-12f
        val squareNanometers = 1e-18f
        val squareInches = 0.00064516f
        val squareFeet = 0.092903f
        val squareYards = 0.836127f
        val squareMiles = 2.59e+6f
        val acres = 4046.86f
        val ares = 100.0f
        val hectares = 10000.0f
    }

    private constructor(symbol: Int, coefficient: Float) : this(symbol, UnitConverterLinear(coefficient))

    companion object {
        val squareMegameters get() = UnitArea(Symbol.squareMegameters, Coefficient.squareMegameters)
        val squareKilometers get() = UnitArea(Symbol.squareKilometers, Coefficient.squareKilometers)
        val squareMeters get() = UnitArea(Symbol.squareMeters, Coefficient.squareMeters)
        val squareCentimeters get() = UnitArea(Symbol.squareCentimeters, Coefficient.squareCentimeters)
        val squareMillimeters get() = UnitArea(Symbol.squareMillimeters, Coefficient.squareMillimeters)
        val squareMicrometers get() = UnitArea(Symbol.squareMicrometers, Coefficient.squareMicrometers)
        val squareNanometers get() = UnitArea(Symbol.squareNanometers, Coefficient.squareNanometers)
        val squareInches get() = UnitArea(Symbol.squareInches, Coefficient.squareInches)
        val squareFeet get() = UnitArea(Symbol.squareFeet, Coefficient.squareFeet)
        val squareYards get() = UnitArea(Symbol.squareYards, Coefficient.squareYards)
        val squareMiles get() = UnitArea(Symbol.squareMiles, Coefficient.squareMiles)
        val acres get() = UnitArea(Symbol.acres, Coefficient.acres)
        val ares get() = UnitArea(Symbol.ares, Coefficient.ares)
        val hectares get() = UnitArea(Symbol.hectares, Coefficient.hectares)
    }
}

data class UnitConcentrationMass(override val symbolRes: Int, override val converter: UnitConverter) :
    Dimension<UnitConcentrationMass> {
    override fun baseUnit() = gramsPerLiter

    private object Symbol {
        val gramsPerLiter = R.string.unit_gramsPerLiter
        val milligramsPerDeciliter = R.string.unit_milligramsPerDeciliter
        val millimolesPerLiter = R.string.unit_millimolesPerLiter
    }

    private object Coefficient {
        val gramsPerLiter = 1.0f
        val milligramsPerDeciliter = 0.01f
        val millimolesPerLiter = 18.0f
    }

    private constructor(symbol: Int, coefficient: Float) : this(symbol, UnitConverterLinear(coefficient))

    companion object {
        val gramsPerLiter get() = UnitConcentrationMass(Symbol.gramsPerLiter, Coefficient.gramsPerLiter)
        val milligramsPerDeciliter
            get() = UnitConcentrationMass(
                Symbol.milligramsPerDeciliter,
                Coefficient.milligramsPerDeciliter
            )
        val millimolesPerLiter get() = UnitConcentrationMass(Symbol.millimolesPerLiter, Coefficient.millimolesPerLiter)
    }
}

data class UnitDispersion(override val symbolRes: Int, override val converter: UnitConverter) :
    Dimension<UnitDispersion> {
    override fun baseUnit() = partsPerMillion

    private object Symbol {
        val partsPerMillion = R.string.unit_partsPerMillion
    }

    private object Coefficient {
        val partsPerMillion = 1.0f
    }

    private constructor(symbol: Int, coefficient: Float) : this(symbol, UnitConverterLinear(coefficient))

    companion object {
        val partsPerMillion get() = UnitDispersion(Symbol.partsPerMillion, Coefficient.partsPerMillion)
    }
}

data class UnitDuration(override val symbolRes: Int, override val converter: UnitConverter) : Dimension<UnitDuration> {
    override fun baseUnit() = seconds

    private object Symbol {
        val picoseconds = R.string.unit_picoseconds
        val nanoseconds = R.string.unit_nanoseconds
        val microseconds = R.string.unit_microseconds
        val milliseconds = R.string.unit_milliseconds
        val seconds = R.string.unit_seconds
        val minutes = R.string.unit_minutes
        val hours = R.string.unit_hours
    }

    private object Coefficient {
        val picoseconds = 1e-12f
        val nanoseconds = 1e-9f
        val microseconds = 1e-6f
        val milliseconds = 1e-3f
        val seconds = 1.0f
        val minutes = 60.0f
        val hours = 3600.0f
    }

    private constructor(symbol: Int, coefficient: Float) : this(symbol, UnitConverterLinear(coefficient))

    companion object {
        val picoseconds get() = UnitDuration(Symbol.picoseconds, Coefficient.picoseconds)
        val nanoseconds get() = UnitDuration(Symbol.nanoseconds, Coefficient.nanoseconds)
        val microseconds get() = UnitDuration(Symbol.microseconds, Coefficient.microseconds)
        val milliseconds get() = UnitDuration(Symbol.milliseconds, Coefficient.milliseconds)
        val seconds get() = UnitDuration(Symbol.seconds, Coefficient.seconds)
        val minutes get() = UnitDuration(Symbol.minutes, Coefficient.minutes)
        val hours get() = UnitDuration(Symbol.hours, Coefficient.hours)
    }
}

data class UnitElectricCharge(override val symbolRes: Int, override val converter: UnitConverter) : Dimension<UnitElectricCharge> {
    override fun baseUnit() = coulombs

    private object Symbol {
        val coulombs = R.string.unit_coulombs
        val megaampereHours = R.string.unit_megaampereHours
        val kiloampereHours = R.string.unit_kiloampereHours
        val ampereHours = R.string.unit_ampereHours
        val milliampereHours = R.string.unit_milliampereHours
        val microampereHours = R.string.unit_microampereHours
    }

    private object Coefficient {
        val coulombs = 1.0f
        val megaampereHours = 3.6e9f
        val kiloampereHours = 3600000.0f
        val ampereHours = 3600.0f
        val milliampereHours = 3.6f
        val microampereHours = 0.0036f
    }

    private constructor(symbol: Int, coefficient: Float) : this(symbol, UnitConverterLinear(coefficient))

    companion object {
        val coulombs get() = UnitElectricCharge(Symbol.coulombs, Coefficient.coulombs)
        val megaampereHours get() = UnitElectricCharge(Symbol.megaampereHours, Coefficient.megaampereHours)
        val kiloampereHours get() = UnitElectricCharge(Symbol.kiloampereHours, Coefficient.kiloampereHours)
        val ampereHours get() = UnitElectricCharge(Symbol.ampereHours, Coefficient.ampereHours)
        val milliampereHours get() = UnitElectricCharge(Symbol.milliampereHours, Coefficient.milliampereHours)
        val microampereHours get() = UnitElectricCharge(Symbol.microampereHours, Coefficient.microampereHours)
    }
}

data class UnitElectricCurrent(override val symbolRes: Int, override val converter: UnitConverter) : Dimension<UnitElectricCurrent> {
    override fun baseUnit() = amperes

    private object Symbol {
        val megaamperes = R.string.unit_megaamperes
        val kiloamperes = R.string.unit_kiloamperes
        val amperes = R.string.unit_amperes
        val milliamperes = R.string.unit_milliamperes
        val microamperes = R.string.unit_microamperes
    }

    private object Coefficient {
        val megaamperes = 1e6f
        val kiloamperes = 1e3f
        val amperes = 1.0f
        val milliamperes = 1e-3f
        val microamperes = 1e-6f
    }

    private constructor(symbol: Int, coefficient: Float) : this(symbol, UnitConverterLinear(coefficient))

    companion object {
        val megaamperes get() = UnitElectricCurrent(Symbol.megaamperes, Coefficient.megaamperes)
        val kiloamperes get() = UnitElectricCurrent(Symbol.kiloamperes, Coefficient.kiloamperes)
        val amperes get() = UnitElectricCurrent(Symbol.amperes, Coefficient.amperes)
        val milliamperes get() = UnitElectricCurrent(Symbol.milliamperes, Coefficient.milliamperes)
        val microamperes get() = UnitElectricCurrent(Symbol.microamperes, Coefficient.microamperes)
    }
}

data class UnitElectricPotentialDifference(override val symbolRes: Int, override val converter: UnitConverter) :
    Dimension<UnitElectricPotentialDifference> {
    override fun baseUnit() = volts

    private object Symbol {
        val megavolts = R.string.unit_megavolts
        val kilovolts = R.string.unit_kilovolts
        val volts = R.string.unit_volts
        val millivolts = R.string.unit_millivolts
        val microvolts = R.string.unit_microvolts
    }

    private object Coefficient {
        val megavolts = 1e6f
        val kilovolts = 1e3f
        val volts = 1.0f
        val millivolts = 1e-3f
        val microvolts = 1e-6f
    }

    private constructor(symbol: Int, coefficient: Float) : this(symbol, UnitConverterLinear(coefficient))

    companion object {
        val megavolts get() = UnitElectricPotentialDifference(Symbol.megavolts, Coefficient.megavolts)
        val kilovolts get() = UnitElectricPotentialDifference(Symbol.kilovolts, Coefficient.kilovolts)
        val volts get() = UnitElectricPotentialDifference(Symbol.volts, Coefficient.volts)
        val millivolts get() = UnitElectricPotentialDifference(Symbol.millivolts, Coefficient.millivolts)
        val microvolts get() = UnitElectricPotentialDifference(Symbol.microvolts, Coefficient.microvolts)
    }
}

data class UnitElectricResistance(override val symbolRes: Int, override val converter: UnitConverter) : Dimension<UnitElectricResistance> {
    override fun baseUnit() = ohms

    private object Symbol {
        val megaohms = R.string.unit_megaohms
        val kiloohms = R.string.unit_kiloohms
        val ohms = R.string.unit_ohms
        val milliohms = R.string.unit_milliohms
        val microohms = R.string.unit_microohms
    }

    private object Coefficient {
        const val megaohms = 1e6f
        const val kiloohms = 1e3f
        const val ohms = 1.0f
        const val milliohms = 1e-3f
        const val microohms = 1e-6f
    }

    private constructor(symbol: Int, coefficient: Float) : this(symbol, UnitConverterLinear(coefficient))

    companion object {
        val megaohms get() = UnitElectricResistance(Symbol.megaohms, Coefficient.megaohms)
        val kiloohms get() = UnitElectricResistance(Symbol.kiloohms, Coefficient.kiloohms)
        val ohms get() = UnitElectricResistance(Symbol.ohms, Coefficient.ohms)
        val milliohms get() = UnitElectricResistance(Symbol.milliohms, Coefficient.milliohms)
        val microohms get() = UnitElectricResistance(Symbol.microohms, Coefficient.microohms)
    }
}

data class UnitEnergy(override val symbolRes: Int, override val converter: UnitConverter) : Dimension<UnitEnergy> {
    override fun baseUnit() = joules

    private object Symbol {
        val kilojoules = R.string.unit_kilojoules
        val joules = R.string.unit_joules
        val kilocalories = R.string.unit_kilocalories
        val calories = R.string.unit_calories
        val wattHours = R.string.unit_wattHours
        val kilowattHours = R.string.unit_kilowattHours
    }

    private object Coefficient {
        const val kilojoules = 1e3f
        const val joules = 1.0f
        const val kilocalories = 4184.0f
        const val calories = 4.184f
        const val wattHours = 3599.9998f
        const val kilowattHours = 3600000.0f
    }

    private constructor(symbol: Int, coefficient: Float) : this(symbol, UnitConverterLinear(coefficient))

    companion object {
        val kilojoules get() = UnitEnergy(Symbol.kilojoules, Coefficient.kilojoules)
        val joules get() = UnitEnergy(Symbol.joules, Coefficient.joules)
        val kilocalories get() = UnitEnergy(Symbol.kilocalories, Coefficient.kilocalories)
        val calories get() = UnitEnergy(Symbol.calories, Coefficient.calories)
        val wattHours get() = UnitEnergy(Symbol.wattHours, Coefficient.wattHours)
        val kilowattHours get() = UnitEnergy(Symbol.kilowattHours, Coefficient.kilowattHours)
    }
}

data class UnitFrequency(override val symbolRes: Int, override val converter: UnitConverter) : Dimension<UnitFrequency> {
    override fun baseUnit() = hertz

    private object Symbol {
        val terahertz = R.string.unit_terahertz
        val gigahertz = R.string.unit_gigahertz
        val megahertz = R.string.unit_megahertz
        val kilohertz = R.string.unit_kilohertz
        val hertz = R.string.unit_hertz
        val millihertz = R.string.unit_millihertz
        val microhertz = R.string.unit_microhertz
        val nanohertz = R.string.unit_nanohertz
        val framesPerSecond = R.string.unit_framesPerSecond
    }

    private object Coefficient {
        const val terahertz = 1e12f
        const val gigahertz = 1e9f
        const val megahertz = 1e6f
        const val kilohertz = 1e3f
        const val hertz = 1.0f
        const val millihertz = 1e-3f
        const val microhertz = 1e-6f
        const val nanohertz = 1e-9f
        const val framesPerSecond = 1.0f
    }

    private constructor(symbol: Int, coefficient: Float) : this(symbol, UnitConverterLinear(coefficient))

    companion object {
        val terahertz get() = UnitFrequency(Symbol.terahertz, Coefficient.terahertz)
        val gigahertz get() = UnitFrequency(Symbol.gigahertz, Coefficient.gigahertz)
        val megahertz get() = UnitFrequency(Symbol.megahertz, Coefficient.megahertz)
        val kilohertz get() = UnitFrequency(Symbol.kilohertz, Coefficient.kilohertz)
        val hertz get() = UnitFrequency(Symbol.hertz, Coefficient.hertz)
        val millihertz get() = UnitFrequency(Symbol.millihertz, Coefficient.millihertz)
        val microhertz get() = UnitFrequency(Symbol.microhertz, Coefficient.microhertz)
        val nanohertz get() = UnitFrequency(Symbol.nanohertz, Coefficient.nanohertz)
        val framesPerSecond get() = UnitFrequency(Symbol.framesPerSecond, Coefficient.framesPerSecond)
    }
}

data class UnitFuelEfficiency(override val symbolRes: Int, override val converter: UnitConverter) : Dimension<UnitFuelEfficiency> {
    override fun baseUnit() = litersPer100Kilometers

    private object Symbol {
        val litersPer100Kilometers = R.string.unit_litersPer100Kilometers
        val milesPerImperialGallon = R.string.unit_milesPerImperialGallon
        val milesPerGallon = R.string.unit_milesPerGallon
    }

    private object Coefficient {
        const val litersPer100Kilometers = 1.0f
        const val milesPerImperialGallon = 282.481f
        const val milesPerGallon = 235.215f
    }

    private constructor(symbol: Int, coefficient: Float) : this(symbol, UnitConverterLinear(coefficient))

    companion object {
        val litersPer100Kilometers
            get() = UnitFuelEfficiency(
                Symbol.litersPer100Kilometers,
                Coefficient.litersPer100Kilometers
            )
        val milesPerImperialGallon
            get() = UnitFuelEfficiency(
                Symbol.milesPerImperialGallon,
                Coefficient.milesPerImperialGallon
            )
        val milesPerGallon get() = UnitFuelEfficiency(Symbol.milesPerGallon, Coefficient.milesPerGallon)
    }
}

data class UnitLength(override val symbolRes: Int, override val converter: UnitConverter) : Dimension<UnitLength> {
    override fun baseUnit() = meters

    private object Symbol {
        val megameters = R.string.unit_megameters
        val kilometers = R.string.unit_kilometers
        val hectometers = R.string.unit_hectometers
        val decameters = R.string.unit_decameters
        val meters = R.string.unit_meters
        val decimeters = R.string.unit_decimeters
        val centimeters = R.string.unit_centimeters
        val millimeters = R.string.unit_millimeters
        val micrometers = R.string.unit_micrometers
        val nanometers = R.string.unit_nanometers
        val picometers = R.string.unit_picometers
        val inches = R.string.unit_inches
        val feet = R.string.unit_feet
        val yards = R.string.unit_yards
        val miles = R.string.unit_miles
        val scandinavianMiles = R.string.unit_scandinavianMiles
        val lightyears = R.string.unit_lightyears
        val nauticalMiles = R.string.unit_nauticalMiles
        val fathoms = R.string.unit_fathoms
        val furlongs = R.string.unit_furlongs
        val astronomicalUnits = R.string.unit_astronomicalUnits
        val parsecs = R.string.unit_parsecs
    }

    private object Coefficient {
        const val megameters = 1e6f
        const val kilometers = 1e3f
        const val hectometers = 1e2f
        const val decameters = 1e1f
        const val meters = 1.0f
        const val decimeters = 1e-1f
        const val centimeters = 1e-2f
        const val millimeters = 1e-3f
        const val micrometers = 1e-6f
        const val nanometers = 1e-9f
        const val picometers = 1e-12f
        const val inches = 0.0254f
        const val feet = 0.3048f
        const val yards = 0.9144f
        const val miles = 1609.34f
        const val scandinavianMiles = 10000.0f
        const val lightyears = 9.461e+15f
        const val nauticalMiles = 1852.0f
        const val fathoms = 1.8288f
        const val furlongs = 201.168f
        const val astronomicalUnits = 1.496e+11f
        const val parsecs = 3.086e+16f
    }

    private constructor(symbol: Int, coefficient: Float) : this(symbol, UnitConverterLinear(coefficient))

    companion object {
        val megameters get() = UnitLength(Symbol.megameters, Coefficient.megameters)
        val kilometers get() = UnitLength(Symbol.kilometers, Coefficient.kilometers)
        val hectometers get() = UnitLength(Symbol.hectometers, Coefficient.hectometers)
        val decameters get() = UnitLength(Symbol.decameters, Coefficient.decameters)
        val meters get() = UnitLength(Symbol.meters, Coefficient.meters)
        val decimeters get() = UnitLength(Symbol.decimeters, Coefficient.decimeters)
        val centimeters get() = UnitLength(Symbol.centimeters, Coefficient.centimeters)
        val millimeters get() = UnitLength(Symbol.millimeters, Coefficient.millimeters)
        val micrometers get() = UnitLength(Symbol.micrometers, Coefficient.micrometers)
        val nanometers get() = UnitLength(Symbol.nanometers, Coefficient.nanometers)
        val picometers get() = UnitLength(Symbol.picometers, Coefficient.picometers)
        val inches get() = UnitLength(Symbol.inches, Coefficient.inches)
        val feet get() = UnitLength(Symbol.feet, Coefficient.feet)
        val yards get() = UnitLength(Symbol.yards, Coefficient.yards)
        val miles get() = UnitLength(Symbol.miles, Coefficient.miles)
        val scandinavianMiles get() = UnitLength(Symbol.scandinavianMiles, Coefficient.scandinavianMiles)
        val lightyears get() = UnitLength(Symbol.lightyears, Coefficient.lightyears)
        val nauticalMiles get() = UnitLength(Symbol.nauticalMiles, Coefficient.nauticalMiles)
        val fathoms get() = UnitLength(Symbol.fathoms, Coefficient.fathoms)
        val furlongs get() = UnitLength(Symbol.furlongs, Coefficient.furlongs)
        val astronomicalUnits get() = UnitLength(Symbol.astronomicalUnits, Coefficient.astronomicalUnits)
        val parsecs get() = UnitLength(Symbol.parsecs, Coefficient.parsecs)
    }
}

data class UnitIlluminance(override val symbolRes: Int, override val converter: UnitConverter) : Dimension<UnitIlluminance> {
    override fun baseUnit() = lux

    private object Symbol {
        val lux = R.string.unit_lux
    }

    private object Coefficient {
        val lux = 1.0f
    }

    private constructor(symbol: Int, coefficient: Float) : this(symbol, UnitConverterLinear(coefficient))

    companion object {
        val lux get() = UnitIlluminance(Symbol.lux, Coefficient.lux)
    }
}

data class UnitInformationStorage(override val symbolRes: Int, override val converter: UnitConverter) : Dimension<UnitInformationStorage> {
    override fun baseUnit() = bits

    private object Symbol {
        val bytes       = R.string.unit_bytes
        val bits        = R.string.unit_bits
        val nibbles     = R.string.unit_nibbles
        val yottabytes  = R.string.unit_yottabytes
        val zettabytes  = R.string.unit_zettabytes
        val exabytes    = R.string.unit_exabytes
        val petabytes   = R.string.unit_petabytes
        val terabytes   = R.string.unit_terabytes
        val gigabytes   = R.string.unit_gigabytes
        val megabytes   = R.string.unit_megabytes
        val kilobytes   = R.string.unit_kilobytes

        val yottabits   = R.string.unit_yottabits
        val zettabits   = R.string.unit_zettabits
        val exabits     = R.string.unit_exabits
        val petabits    = R.string.unit_petabits
        val terabits    = R.string.unit_terabits
        val gigabits    = R.string.unit_gigabits
        val megabits    = R.string.unit_megabits
        val kilobits    = R.string.unit_kilobits

        val yobibytes   = R.string.unit_yobibytes
        val zebibytes   = R.string.unit_zebibytes
        val exbibytes   = R.string.unit_exbibytes
        val pebibytes   = R.string.unit_pebibytes
        val tebibytes   = R.string.unit_tebibytes
        val gibibytes   = R.string.unit_gibibytes
        val mebibytes   = R.string.unit_mebibytes
        val kibibytes   = R.string.unit_kibibytes

        val yobibits    = R.string.unit_yobibits
        val zebibits    = R.string.unit_zebibits
        val exbibits    = R.string.unit_exbibits
        val pebibits    = R.string.unit_pebibits
        val tebibits    = R.string.unit_tebibits
        val gibibits    = R.string.unit_gibibits
        val mebibits    = R.string.unit_mebibits
        val kibibits    = R.string.unit_kibibits
    }

    private object Coefficient {
        val bytes        = 8.0f
        val bits         = 1.0f
        val nibbles      = 4.0f
        val yottabytes   = 8.0f*1000.0f.pow(8.0f)
        val zettabytes   = 8.0f*1000.0f.pow(7.0f)
        val exabytes     = 8.0f*1000.0f.pow(6.0f)
        val petabytes    = 8.0f*1000.0f.pow(5.0f)
        val terabytes    = 8.0f*1000.0f.pow(4.0f)
        val gigabytes    = 8.0f*1000.0f.pow(3.0f)
        val megabytes    = 8.0f*1000.0f.pow(2.0f)
        val kilobytes    = 8.0f*1000f

        val yottabits    = (1000f).pow(8.0f)
        val zettabits    = (1000f).pow(7.0f)
        val exabits      = (1000f).pow(6.0f)
        val petabits     = (1000f).pow(5.0f)
        val terabits     = (1000f).pow(4.0f)
        val gigabits     = (1000f).pow(3.0f)
        val megabits     = (1000f).pow(2.0f)
        val kilobits     = 1000.0f

        val yobibytes    = 8f*(1024.0f).pow(8.0f)
        val zebibytes    = 8f*(1024.0f).pow(7.0f)
        val exbibytes    = 8f*(1024.0f).pow(6.0f)
        val pebibytes    = 8f*(1024.0f).pow(5.0f)
        val tebibytes    = 8f*(1024.0f).pow(4.0f)
        val gibibytes    = 8f*(1024.0f).pow(3.0f)
        val mebibytes    = 8f*(1024.0f).pow(2.0f)
        val kibibytes    = 8f*1024.0f

        val yobibits     = (1024.0f).pow(8.0f)
        val zebibits     = (1024.0f).pow(7.0f)
        val exbibits     = (1024.0f).pow(6.0f)
        val pebibits     = (1024.0f).pow(5.0f)
        val tebibits     = (1024.0f).pow(4.0f)
        val gibibits     = (1024.0f).pow(3.0f)
        val mebibits     = (1024.0f).pow(2.0f)
        val kibibits     = 1024.0f
    }

    private constructor(symbol: Int, coefficient: Float) : this(symbol, UnitConverterLinear(coefficient))

    companion object {
        val bytes       = UnitInformationStorage(Symbol.bytes, Coefficient.bytes)
        val bits        = UnitInformationStorage(Symbol.bits, Coefficient.bits)
        val nibbles     = UnitInformationStorage(Symbol.nibbles, Coefficient.nibbles)
        val yottabytes  = UnitInformationStorage(Symbol.yottabytes, Coefficient.yottabytes)
        val zettabytes  = UnitInformationStorage(Symbol.zettabytes, Coefficient.zettabytes)
        val exabytes    = UnitInformationStorage(Symbol.exabytes, Coefficient.exabytes)
        val petabytes   = UnitInformationStorage(Symbol.petabytes, Coefficient.petabytes)
        val terabytes   = UnitInformationStorage(Symbol.terabytes, Coefficient.terabytes)
        val gigabytes   = UnitInformationStorage(Symbol.gigabytes, Coefficient.gigabytes)
        val megabytes   = UnitInformationStorage(Symbol.megabytes, Coefficient.megabytes)
        val kilobytes   = UnitInformationStorage(Symbol.kilobytes, Coefficient.kilobytes)

        val yottabits   = UnitInformationStorage(Symbol.yottabits, Coefficient.yottabits)
        val zettabits   = UnitInformationStorage(Symbol.zettabits, Coefficient.zettabits)
        val exabits     = UnitInformationStorage(Symbol.exabits, Coefficient.exabits)
        val petabits    = UnitInformationStorage(Symbol.petabits, Coefficient.petabits)
        val terabits    = UnitInformationStorage(Symbol.terabits, Coefficient.terabits)
        val gigabits    = UnitInformationStorage(Symbol.gigabits, Coefficient.gigabits)
        val megabits    = UnitInformationStorage(Symbol.megabits, Coefficient.megabits)
        val kilobits    = UnitInformationStorage(Symbol.kilobits, Coefficient.kilobits)

        val yobibytes   = UnitInformationStorage(Symbol.yobibytes, Coefficient.yobibytes)
        val zebibytes   = UnitInformationStorage(Symbol.zebibytes, Coefficient.zebibytes)
        val exbibytes   = UnitInformationStorage(Symbol.exbibytes, Coefficient.exbibytes)
        val pebibytes   = UnitInformationStorage(Symbol.pebibytes, Coefficient.pebibytes)
        val tebibytes   = UnitInformationStorage(Symbol.tebibytes, Coefficient.tebibytes)
        val gibibytes   = UnitInformationStorage(Symbol.gibibytes, Coefficient.gibibytes)
        val mebibytes   = UnitInformationStorage(Symbol.mebibytes, Coefficient.mebibytes)
        val kibibytes   = UnitInformationStorage(Symbol.kibibytes, Coefficient.kibibytes)

        val yobibits    = UnitInformationStorage(Symbol.yobibits, Coefficient.yobibits)
        val zebibits    = UnitInformationStorage(Symbol.zebibits, Coefficient.zebibits)
        val exbibits    = UnitInformationStorage(Symbol.exbibits, Coefficient.exbibits)
        val pebibits    = UnitInformationStorage(Symbol.pebibits, Coefficient.pebibits)
        val tebibits    = UnitInformationStorage(Symbol.tebibits, Coefficient.tebibits)
        val gibibits    = UnitInformationStorage(Symbol.gibibits, Coefficient.gibibits)
        val mebibits    = UnitInformationStorage(Symbol.mebibits, Coefficient.mebibits)
        val kibibits    = UnitInformationStorage(Symbol.kibibits, Coefficient.kibibits)
    }
}

data class UnitMass(override val symbolRes: Int, override val converter: UnitConverter) : Dimension<UnitMass> {
    override fun baseUnit() = kilograms

    private object Symbol {
        val kilograms    = R.string.unit_kilograms
        val grams        = R.string.unit_grams
        val decigrams    = R.string.unit_decigrams
        val centigrams   = R.string.unit_centigrams
        val milligrams   = R.string.unit_milligrams
        val micrograms   = R.string.unit_micrograms
        val nanograms    = R.string.unit_nanograms
        val picograms    = R.string.unit_picograms
        val ounces       = R.string.unit_ounces
        val pounds       = R.string.unit_pounds
        val stones       = R.string.unit_stones
        val metricTons   = R.string.unit_metricTons
        val shortTons    = R.string.unit_shortTons
        val carats       = R.string.unit_carats
        val ouncesTroy   = R.string.unit_ouncesTroy
        val slugs        = R.string.unit_slugs
    }

    private object Coefficient {
        const val kilograms    = 1.0f
        const val grams        = 1e-3f
        const val decigrams    = 1e-4f
        const val centigrams   = 1e-5f
        const val milligrams   = 1e-6f
        const val micrograms   = 1e-9f
        const val nanograms    = 1e-12f
        const val picograms    = 1e-15f
        const val ounces       = 0.0283495f
        const val pounds       = 0.453592f
        const val stones       = 0.157473f
        const val metricTons   = 1000.0f
        const val shortTons    = 907.185f
        const val carats       = 0.0002f
        const val ouncesTroy   = 0.03110348f
        const val slugs        = 14.5939f
    }

    private constructor(symbol: Int, coefficient: Float) : this(symbol, UnitConverterLinear(coefficient))

    companion object {
        val kilograms    = UnitMass(Symbol.kilograms, Coefficient.kilograms)
        val grams        = UnitMass(Symbol.grams, Coefficient.grams)
        val decigrams    = UnitMass(Symbol.decigrams, Coefficient.decigrams)
        val centigrams   = UnitMass(Symbol.centigrams, Coefficient.centigrams)
        val milligrams   = UnitMass(Symbol.milligrams, Coefficient.milligrams)
        val micrograms   = UnitMass(Symbol.micrograms, Coefficient.micrograms)
        val nanograms    = UnitMass(Symbol.nanograms, Coefficient.nanograms)
        val picograms    = UnitMass(Symbol.picograms, Coefficient.picograms)
        val ounces       = UnitMass(Symbol.ounces, Coefficient.ounces)
        val pounds       = UnitMass(Symbol.pounds, Coefficient.pounds)
        val stones       = UnitMass(Symbol.stones, Coefficient.stones)
        val metricTons   = UnitMass(Symbol.metricTons, Coefficient.metricTons)
        val shortTons    = UnitMass(Symbol.shortTons, Coefficient.shortTons)
        val carats       = UnitMass(Symbol.carats, Coefficient.carats)
        val ouncesTroy   = UnitMass(Symbol.ouncesTroy, Coefficient.ouncesTroy)
        val slugs        = UnitMass(Symbol.slugs, Coefficient.slugs)
    }
}

data class UnitPower(override val symbolRes: Int, override val converter: UnitConverter) : Dimension<UnitPower> {
    override fun baseUnit() = watts

    private object Symbol {
        val terawatts  = R.string.unit_terawatts
        val gigawatts  = R.string.unit_gigawatts
        val megawatts  = R.string.unit_megawatts
        val kilowatts  = R.string.unit_kilowatts
        val watts      = R.string.unit_watts
        val milliwatts = R.string.unit_milliwatts
        val microwatts = R.string.unit_microwatts
        val nanowatts  = R.string.unit_nanowatts
        val picowatts  = R.string.unit_picowatts
        val femtowatts = R.string.unit_femtowatts
        val horsepower = R.string.unit_horsepower
    }

    private object Coefficient {
        const val terawatts  = 1e12f
        const val gigawatts  = 1e9f
        const val megawatts  = 1e6f
        const val kilowatts  = 1e3f
        const val watts      = 1.0f
        const val milliwatts = 1e-3f
        const val microwatts = 1e-6f
        const val nanowatts  = 1e-9f
        const val picowatts  = 1e-12f
        const val femtowatts = 1e-15f
        const val horsepower = 745.7f
    }

    private constructor(symbol: Int, coefficient: Float) : this(symbol, UnitConverterLinear(coefficient))

    companion object {
        val terawatts  = UnitPower(Symbol.terawatts, Coefficient.terawatts)
        val gigawatts  = UnitPower(Symbol.gigawatts, Coefficient.gigawatts)
        val megawatts  = UnitPower(Symbol.megawatts, Coefficient.megawatts)
        val kilowatts  = UnitPower(Symbol.kilowatts, Coefficient.kilowatts)
        val watts      = UnitPower(Symbol.watts, Coefficient.watts)
        val milliwatts = UnitPower(Symbol.milliwatts, Coefficient.milliwatts)
        val microwatts = UnitPower(Symbol.microwatts, Coefficient.microwatts)
        val nanowatts  = UnitPower(Symbol.nanowatts, Coefficient.nanowatts)
        val picowatts  = UnitPower(Symbol.picowatts, Coefficient.picowatts)
        val femtowatts = UnitPower(Symbol.femtowatts, Coefficient.femtowatts)
        val horsepower = UnitPower(Symbol.horsepower, Coefficient.horsepower)
    }
}

data class UnitPressure(override val symbolRes: Int, override val converter: UnitConverter) : Dimension<UnitPressure> {
    override fun baseUnit() = newtonsPerMetersSquared

    private object Symbol {
        val newtonsPerMetersSquared  = R.string.unit_newtonsPerMetersSquared
        val gigapascals              = R.string.unit_gigapascals
        val megapascals              = R.string.unit_megapascals
        val kilopascals              = R.string.unit_kilopascals
        val hectopascals             = R.string.unit_hectopascals
        val inchesOfMercury          = R.string.unit_inchesOfMercury
        val bars                     = R.string.unit_bars
        val millibars                = R.string.unit_millibars
        val millimetersOfMercury     = R.string.unit_millimetersOfMercury
        val poundsForcePerSquareInch = R.string.unit_poundsForcePerSquareInch
    }

    private object Coefficient {
        const val newtonsPerMetersSquared  = 1.0f
        const val gigapascals              = 1e9f
        const val megapascals              = 1e6f
        const val kilopascals              = 1e3f
        const val hectopascals             = 1e2f
        const val inchesOfMercury          = 3386.39f
        const val bars                     = 1e5f
        const val millibars                = 1e2f
        const val millimetersOfMercury     = 133.322f
        const val poundsForcePerSquareInch = 6894.76f
    }

    private constructor(symbol: Int, coefficient: Float) : this(symbol, UnitConverterLinear(coefficient))

    companion object {
        val newtonsPerMetersSquared  = UnitPressure(Symbol.newtonsPerMetersSquared, Coefficient.newtonsPerMetersSquared)
        val gigapascals              = UnitPressure(Symbol.gigapascals, Coefficient.gigapascals)
        val megapascals              = UnitPressure(Symbol.megapascals, Coefficient.megapascals)
        val kilopascals              = UnitPressure(Symbol.kilopascals, Coefficient.kilopascals)
        val hectopascals             = UnitPressure(Symbol.hectopascals, Coefficient.hectopascals)
        val inchesOfMercury          = UnitPressure(Symbol.inchesOfMercury, Coefficient.inchesOfMercury)
        val bars                     = UnitPressure(Symbol.bars, Coefficient.bars)
        val millibars                = UnitPressure(Symbol.millibars, Coefficient.millibars)
        val millimetersOfMercury     = UnitPressure(Symbol.millimetersOfMercury, Coefficient.millimetersOfMercury)
        val poundsForcePerSquareInch = UnitPressure(Symbol.poundsForcePerSquareInch, Coefficient.poundsForcePerSquareInch)
    }
}

data class UnitSpeed(override val symbolRes: Int, override val converter: UnitConverter) : Dimension<UnitSpeed> {
    override fun baseUnit() = metersPerSecond

    private object Symbol {
        val metersPerSecond = R.string.unit_metersPerSecond
        val kilometersPerHour = R.string.unit_kilometersPerHour
        val milesPerHour = R.string.unit_milesPerHour
        val knots = R.string.unit_knots
    }

    private object Coefficient {
        val metersPerSecond = 1.0f
        val kilometersPerHour = 0.277778f
        val milesPerHour = 0.44704f
        val knots = 0.514444f
    }

    private constructor(symbolRes: Int, coefficient: Float) : this(symbolRes, UnitConverterLinear(coefficient))

    companion object {
        val metersPerSecond get() = UnitSpeed(Symbol.metersPerSecond, Coefficient.metersPerSecond)
        val kilometersPerHour get() = UnitSpeed(Symbol.kilometersPerHour, Coefficient.kilometersPerHour)
        val milesPerHour get() = UnitSpeed(Symbol.milesPerHour, Coefficient.milesPerHour)
        val knots get() = UnitSpeed(Symbol.knots, Coefficient.knots)
    }
}

data class UnitTemperature(override val symbolRes: Int, override val converter: UnitConverter) : Dimension<UnitTemperature> {
    override fun baseUnit() = kelvin

    private object Symbol {
        val kelvin     = R.string.unit_kelvin
        val celsius    = R.string.unit_celsius
        val fahrenheit = R.string.unit_fahrenheit
    }

    private object Coefficient {
        const val kelvin     = 1.0f
        const val celsius    = 1.0f
        const val fahrenheit = 0.55555555555556f
    }

    private object Constant {
        const val kelvin     = 0.0f
        const val celsius    = 273.15f
        const val fahrenheit = 255.37222222222427f
    }

    private constructor(symbol: Int,  coefficient: Float, constant: Float) : this(symbol, UnitConverterLinear(coefficient, constant))

    companion object {
        val kelvin     = UnitTemperature(Symbol.kelvin, Coefficient.kelvin, Constant.kelvin)
        val celsius    = UnitTemperature(Symbol.celsius, Coefficient.celsius, Constant.celsius)
        val fahrenheit = UnitTemperature(Symbol.fahrenheit, Coefficient.fahrenheit, Constant.fahrenheit)
    }
}

data class UnitVolume(override val symbolRes: Int, override val converter: UnitConverter) : Dimension<UnitVolume> {
    override fun baseUnit() = liters

    private object Symbol {
        val megaliters = R.string.unit_megaliters
        val kiloliters = R.string.unit_kiloliters
        val liters = R.string.unit_liters
        val deciliters = R.string.unit_deciliters
        val centiliters = R.string.unit_centiliters
        val milliliters = R.string.unit_milliliters
        val cubicKilometers = R.string.unit_cubicKilometers
        val cubicMeters = R.string.unit_cubicMeters
        val cubicDecimeters = R.string.unit_cubicDecimeters
        val cubicCentimeters = R.string.unit_cubicCentimeters
        val cubicMillimeters = R.string.unit_cubicMillimeters
        val cubicInches = R.string.unit_cubicInches
        val cubicFeet = R.string.unit_cubicFeet
        val cubicYards = R.string.unit_cubicYards
        val cubicMiles = R.string.unit_cubicMiles
        val acreFeet = R.string.unit_acreFeet
        val bushels = R.string.unit_bushels
        val teaspoons = R.string.unit_teaspoons
        val tablespoons = R.string.unit_tablespoons
        val fluidOunces = R.string.unit_fluidOunces
        val cups = R.string.unit_cups
        val pints = R.string.unit_pints
        val quarts = R.string.unit_quarts
        val gallons = R.string.unit_gallons
        val imperialTeaspoons = R.string.unit_imperialTeaspoons
        val imperialTablespoons = R.string.unit_imperialTablespoons
        val imperialFluidOunces = R.string.unit_imperialFluidOunces
        val imperialPints = R.string.unit_imperialPints
        val imperialQuarts = R.string.unit_imperialQuarts
        val imperialGallons = R.string.unit_imperialGallons
        val metricCups = R.string.unit_metricCups
    }

    private object Coefficient {
        val megaliters = 1e6f
        val kiloliters = 1e3f
        val liters = 1.0f
        val deciliters = 1e-1f
        val centiliters = 1e-2f
        val milliliters = 1e-3f
        val cubicKilometers = 1e12f
        val cubicMeters = 1000.0f
        val cubicDecimeters = 1.0f
        val cubicCentimeters = 1e-3f
        val cubicMillimeters = 1e-6f
        val cubicInches = 0.0163871f
        val cubicFeet = 28.3168f
        val cubicYards = 764.555f
        val cubicMiles = 4.168e+12f
        val acreFeet = 1.233e+6f
        val bushels = 35.2391f
        val teaspoons = 0.00492892f
        val tablespoons = 0.0147868f
        val fluidOunces = 0.0295735f
        val cups = 0.24f
        val pints = 0.473176f
        val quarts = 0.946353f
        val gallons = 3.78541f
        val imperialTeaspoons = 0.00591939f
        val imperialTablespoons = 0.0177582f
        val imperialFluidOunces = 0.0284131f
        val imperialPints = 0.568261f
        val imperialQuarts = 1.13652f
        val imperialGallons = 4.54609f
        val metricCups = 0.25f
    }

    private constructor(symbol: Int, coefficient: Float) : this(
        symbol, UnitConverterLinear(coefficient)
    )

    companion object {
        val megaliters get() = UnitVolume(Symbol.megaliters, Coefficient.megaliters)
        val kiloliters get() = UnitVolume(Symbol.kiloliters, Coefficient.kiloliters)
        val liters get() = UnitVolume(Symbol.liters, Coefficient.liters)
        val deciliters get() = UnitVolume(Symbol.deciliters, Coefficient.deciliters)
        val centiliters get() = UnitVolume(Symbol.centiliters, Coefficient.centiliters)
        val milliliters get() = UnitVolume(Symbol.milliliters, Coefficient.milliliters)
        val cubicKilometers get() = UnitVolume(Symbol.cubicKilometers, Coefficient.cubicKilometers)
        val cubicMeters get() = UnitVolume(Symbol.cubicMeters, Coefficient.cubicMeters)
        val cubicDecimeters get() = UnitVolume(Symbol.cubicDecimeters, Coefficient.cubicDecimeters)
        val cubicCentimeters get() = UnitVolume(Symbol.cubicCentimeters, Coefficient.cubicCentimeters)
        val cubicMillimeters get() = UnitVolume(Symbol.cubicMillimeters, Coefficient.cubicMillimeters)
        val cubicInches get() = UnitVolume(Symbol.cubicInches, Coefficient.cubicInches)
        val cubicFeet get() = UnitVolume(Symbol.cubicFeet, Coefficient.cubicFeet)
        val cubicYards get() = UnitVolume(Symbol.cubicYards, Coefficient.cubicYards)
        val cubicMiles get() = UnitVolume(Symbol.cubicMiles, Coefficient.cubicMiles)
        val acreFeet get() = UnitVolume(Symbol.acreFeet, Coefficient.acreFeet)
        val bushels get() = UnitVolume(Symbol.bushels, Coefficient.bushels)
        val teaspoons get() = UnitVolume(Symbol.teaspoons, Coefficient.teaspoons)
        val tablespoons get() = UnitVolume(Symbol.tablespoons, Coefficient.tablespoons)
        val fluidOunces get() = UnitVolume(Symbol.fluidOunces, Coefficient.fluidOunces)
        val cups get() = UnitVolume(Symbol.cups, Coefficient.cups)
        val pints get() = UnitVolume(Symbol.pints, Coefficient.pints)
        val quarts get() = UnitVolume(Symbol.quarts, Coefficient.quarts)
        val gallons get() = UnitVolume(Symbol.gallons, Coefficient.gallons)
        val imperialTeaspoons get() = UnitVolume(Symbol.imperialTeaspoons, Coefficient.imperialTeaspoons)
        val imperialTablespoons get() = UnitVolume(Symbol.imperialTablespoons, Coefficient.imperialTablespoons)
        val imperialFluidOunces get() = UnitVolume(Symbol.imperialFluidOunces, Coefficient.imperialFluidOunces)
        val imperialPints get() = UnitVolume(Symbol.imperialPints, Coefficient.imperialPints)
        val imperialQuarts get() = UnitVolume(Symbol.imperialQuarts, Coefficient.imperialQuarts)
        val imperialGallons get() = UnitVolume(Symbol.imperialGallons, Coefficient.imperialGallons)
        val metricCups get() = UnitVolume(Symbol.metricCups, Coefficient.metricCups)
    }
}

object UnitRpm : MeasurementUnit {
    override val symbolRes = R.string.unit_rpm
}
