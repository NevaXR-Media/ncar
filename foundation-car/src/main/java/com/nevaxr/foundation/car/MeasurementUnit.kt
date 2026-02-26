package com.nevaxr.foundation.car

import kotlin.math.PI

const val SecondsPerHour = 60f * 60f

interface MeasurementUnit {
    /// Unit's name as resource id
    val symbolRes: Int
}

interface Dimension<T> : MeasurementUnit {
    val converter: UnitConverter
    fun baseUnit(): Dimension<T>
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
        val degrees      = R.string.unit_degrees
        val arcMinutes   = R.string.unit_arcMinutes
        val arcSeconds   = R.string.unit_arcSeconds
        val radians      = R.string.unit_radians
        val gradians     = R.string.unit_gradians
        val revolutions  = R.string.unit_revolutions
    }

    private object Coefficient {
        val degrees      = 1f
        val arcMinutes   = 1f / 60f
        val arcSeconds   = 1f / 3600f
        val radians      = 180f / PI.toFloat()
        val gradians     = 0.9f
        val revolutions  = 360f
    }

    private constructor(symbol: Int, coefficient: Float) : this(symbol, UnitConverterLinear(coefficient))

    companion object {
        val degrees      get() = UnitAngle(Symbol.degrees, Coefficient.degrees)
        val arcMinutes   get() = UnitAngle(Symbol.arcMinutes, Coefficient.arcMinutes)
        val arcSeconds   get() = UnitAngle(Symbol.arcSeconds, Coefficient.arcSeconds)
        val radians      get() = UnitAngle(Symbol.radians, Coefficient.radians)
        val gradians     get() = UnitAngle(Symbol.gradians, Coefficient.gradians)
        val revolutions  get() = UnitAngle(Symbol.revolutions, Coefficient.revolutions)
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
        val kelvin     = 1.0f
        val celsius    = 1.0f
        val fahrenheit = 0.5555556f
    }

    private object Constant {
        val kelvin     = 0.0f
        val celsius    = 273.15f
        val fahrenheit = 255.37222f
    }

    private constructor(symbol: Int, coefficient: Float, constant: Float) : this(
        symbol, UnitConverterLinear(coefficient, constant)
    )

    companion object {
        val kelvin get() = UnitTemperature(Symbol.kelvin, Coefficient.kelvin, Constant.kelvin)
        val celsius get() = UnitTemperature(Symbol.celsius, Coefficient.celsius, Constant.celsius)
        val fahrenheit get() = UnitTemperature(Symbol.fahrenheit, Coefficient.fahrenheit, Constant.fahrenheit)
    }
}

data class UnitVolume(override val symbolRes: Int, override val converter: UnitConverter) : Dimension<UnitVolume> {
    override fun baseUnit() = liters

    private object Symbol {
        val megaliters           = R.string.unit_megaliters
        val kiloliters           = R.string.unit_kiloliters
        val liters               = R.string.unit_liters
        val deciliters           = R.string.unit_deciliters
        val centiliters          = R.string.unit_centiliters
        val milliliters          = R.string.unit_milliliters
        val cubicKilometers      = R.string.unit_cubicKilometers
        val cubicMeters          = R.string.unit_cubicMeters
        val cubicDecimeters      = R.string.unit_cubicDecimeters
        val cubicCentimeters     = R.string.unit_cubicCentimeters
        val cubicMillimeters     = R.string.unit_cubicMillimeters
        val cubicInches          = R.string.unit_cubicInches
        val cubicFeet            = R.string.unit_cubicFeet
        val cubicYards           = R.string.unit_cubicYards
        val cubicMiles           = R.string.unit_cubicMiles
        val acreFeet             = R.string.unit_acreFeet
        val bushels              = R.string.unit_bushels
        val teaspoons            = R.string.unit_teaspoons
        val tablespoons          = R.string.unit_tablespoons
        val fluidOunces          = R.string.unit_fluidOunces
        val cups                 = R.string.unit_cups
        val pints                = R.string.unit_pints
        val quarts               = R.string.unit_quarts
        val gallons              = R.string.unit_gallons
        val imperialTeaspoons    = R.string.unit_imperialTeaspoons
        val imperialTablespoons  = R.string.unit_imperialTablespoons
        val imperialFluidOunces  = R.string.unit_imperialFluidOunces
        val imperialPints        = R.string.unit_imperialPints
        val imperialQuarts       = R.string.unit_imperialQuarts
        val imperialGallons      = R.string.unit_imperialGallons
        val metricCups           = R.string.unit_metricCups
    }

    private object Coefficient {
        val megaliters           = 1e6f
        val kiloliters           = 1e3f
        val liters               = 1.0f
        val deciliters           = 1e-1f
        val centiliters          = 1e-2f
        val milliliters          = 1e-3f
        val cubicKilometers      = 1e12f
        val cubicMeters          = 1000.0f
        val cubicDecimeters      = 1.0f
        val cubicCentimeters     = 1e-3f
        val cubicMillimeters     = 1e-6f
        val cubicInches          = 0.0163871f
        val cubicFeet            = 28.3168f
        val cubicYards           = 764.555f
        val cubicMiles           = 4.168e+12f
        val acreFeet             = 1.233e+6f
        val bushels              = 35.2391f
        val teaspoons            = 0.00492892f
        val tablespoons          = 0.0147868f
        val fluidOunces          = 0.0295735f
        val cups                 = 0.24f
        val pints                = 0.473176f
        val quarts               = 0.946353f
        val gallons              = 3.78541f
        val imperialTeaspoons    = 0.00591939f
        val imperialTablespoons  = 0.0177582f
        val imperialFluidOunces  = 0.0284131f
        val imperialPints        = 0.568261f
        val imperialQuarts       = 1.13652f
        val imperialGallons      = 4.54609f
        val metricCups           = 0.25f
    }

    private constructor(symbol: Int, coefficient: Float) : this (
        symbol, UnitConverterLinear(coefficient)
    )

    companion object {
        val megaliters           get() = UnitVolume(Symbol.megaliters, Coefficient.megaliters)
        val kiloliters           get() = UnitVolume(Symbol.kiloliters, Coefficient.kiloliters)
        val liters               get() = UnitVolume(Symbol.liters, Coefficient.liters)
        val deciliters           get() = UnitVolume(Symbol.deciliters, Coefficient.deciliters)
        val centiliters          get() = UnitVolume(Symbol.centiliters, Coefficient.centiliters)
        val milliliters          get() = UnitVolume(Symbol.milliliters, Coefficient.milliliters)
        val cubicKilometers      get() = UnitVolume(Symbol.cubicKilometers, Coefficient.cubicKilometers)
        val cubicMeters          get() = UnitVolume(Symbol.cubicMeters, Coefficient.cubicMeters)
        val cubicDecimeters      get() = UnitVolume(Symbol.cubicDecimeters, Coefficient.cubicDecimeters)
        val cubicCentimeters     get() = UnitVolume(Symbol.cubicCentimeters, Coefficient.cubicCentimeters)
        val cubicMillimeters     get() = UnitVolume(Symbol.cubicMillimeters, Coefficient.cubicMillimeters)
        val cubicInches          get() = UnitVolume(Symbol.cubicInches, Coefficient.cubicInches)
        val cubicFeet            get() = UnitVolume(Symbol.cubicFeet, Coefficient.cubicFeet)
        val cubicYards           get() = UnitVolume(Symbol.cubicYards, Coefficient.cubicYards)
        val cubicMiles           get() = UnitVolume(Symbol.cubicMiles, Coefficient.cubicMiles)
        val acreFeet             get() = UnitVolume(Symbol.acreFeet, Coefficient.acreFeet)
        val bushels              get() = UnitVolume(Symbol.bushels, Coefficient.bushels)
        val teaspoons            get() = UnitVolume(Symbol.teaspoons, Coefficient.teaspoons)
        val tablespoons          get() = UnitVolume(Symbol.tablespoons, Coefficient.tablespoons)
        val fluidOunces          get() = UnitVolume(Symbol.fluidOunces, Coefficient.fluidOunces)
        val cups                 get() = UnitVolume(Symbol.cups, Coefficient.cups)
        val pints                get() = UnitVolume(Symbol.pints, Coefficient.pints)
        val quarts               get() = UnitVolume(Symbol.quarts, Coefficient.quarts)
        val gallons              get() = UnitVolume(Symbol.gallons, Coefficient.gallons)
        val imperialTeaspoons    get() = UnitVolume(Symbol.imperialTeaspoons, Coefficient.imperialTeaspoons)
        val imperialTablespoons  get() = UnitVolume(Symbol.imperialTablespoons, Coefficient.imperialTablespoons)
        val imperialFluidOunces  get() = UnitVolume(Symbol.imperialFluidOunces, Coefficient.imperialFluidOunces)
        val imperialPints        get() = UnitVolume(Symbol.imperialPints, Coefficient.imperialPints)
        val imperialQuarts       get() = UnitVolume(Symbol.imperialQuarts, Coefficient.imperialQuarts)
        val imperialGallons      get() = UnitVolume(Symbol.imperialGallons, Coefficient.imperialGallons)
        val metricCups           get() = UnitVolume(Symbol.metricCups, Coefficient.metricCups)
    }
}

data class UnitArea(override val symbolRes: Int, override val converter: UnitConverter) : Dimension<UnitArea> {
    override fun baseUnit() = squareMeters

    private object Symbol {
        val squareMegameters     = R.string.unit_squareMegameters
        val squareKilometers     = R.string.unit_squareKilometers
        val squareMeters         = R.string.unit_squareMeters
        val squareCentimeters    = R.string.unit_squareCentimeters
        val squareMillimeters    = R.string.unit_squareMillimeters
        val squareMicrometers    = R.string.unit_squareMicrometers
        val squareNanometers     = R.string.unit_squareNanometers
        val squareInches         = R.string.unit_squareInches
        val squareFeet           = R.string.unit_squareFeet
        val squareYards          = R.string.unit_squareYards
        val squareMiles          = R.string.unit_squareMiles
        val acres                = R.string.unit_acres
        val ares                 = R.string.unit_ares
        val hectares             = R.string.unit_hectares
    }

    private object Coefficient {
        val squareMegameters     = 1e12f
        val squareKilometers     = 1e6f
        val squareMeters         = 1.0f
        val squareCentimeters    = 1e-4f
        val squareMillimeters    = 1e-6f
        val squareMicrometers    = 1e-12f
        val squareNanometers     = 1e-18f
        val squareInches         = 0.00064516f
        val squareFeet           = 0.092903f
        val squareYards          = 0.836127f
        val squareMiles          = 2.59e+6f
        val acres                = 4046.86f
        val ares                 = 100.0f
        val hectares             = 10000.0f
    }

    private constructor(symbol: Int, coefficient: Float) : this(symbol, UnitConverterLinear(coefficient))

    companion object {
        val squareMegameters  get() = UnitArea(Symbol.squareMegameters, Coefficient.squareMegameters)
        val squareKilometers  get() = UnitArea(Symbol.squareKilometers, Coefficient.squareKilometers)
        val squareMeters      get() = UnitArea(Symbol.squareMeters, Coefficient.squareMeters)
        val squareCentimeters get() = UnitArea(Symbol.squareCentimeters, Coefficient.squareCentimeters)
        val squareMillimeters get() = UnitArea(Symbol.squareMillimeters, Coefficient.squareMillimeters)
        val squareMicrometers get() = UnitArea(Symbol.squareMicrometers, Coefficient.squareMicrometers)
        val squareNanometers  get() = UnitArea(Symbol.squareNanometers, Coefficient.squareNanometers)
        val squareInches      get() = UnitArea(Symbol.squareInches, Coefficient.squareInches)
        val squareFeet        get() = UnitArea(Symbol.squareFeet, Coefficient.squareFeet)
        val squareYards       get() = UnitArea(Symbol.squareYards, Coefficient.squareYards)
        val squareMiles       get() = UnitArea(Symbol.squareMiles, Coefficient.squareMiles)
        val acres             get() = UnitArea(Symbol.acres, Coefficient.acres)
        val ares              get() = UnitArea(Symbol.ares, Coefficient.ares)
        val hectares          get() = UnitArea(Symbol.hectares, Coefficient.hectares)
    }
}

data class UnitConcentrationMass(override val symbolRes: Int, override val converter: UnitConverter) : Dimension<UnitConcentrationMass> {
    override fun baseUnit() = gramsPerLiter

    private object Symbol {
        val gramsPerLiter            = R.string.unit_gramsPerLiter
        val milligramsPerDeciliter   = R.string.unit_milligramsPerDeciliter
        val millimolesPerLiter       = R.string.unit_millimolesPerLiter
    }

    private object Coefficient {
        val gramsPerLiter            = 1.0f
        val milligramsPerDeciliter   = 0.01f
        val millimolesPerLiter       = 18.0f
    }

    private constructor(symbol: Int, coefficient: Float) : this(symbol, UnitConverterLinear(coefficient))

    companion object {
        val gramsPerLiter            get() = UnitConcentrationMass(Symbol.gramsPerLiter, Coefficient.gramsPerLiter)
        val milligramsPerDeciliter   get() = UnitConcentrationMass(Symbol.milligramsPerDeciliter, Coefficient.milligramsPerDeciliter)
        val millimolesPerLiter       get() = UnitConcentrationMass(Symbol.millimolesPerLiter, Coefficient.millimolesPerLiter)
    }
}

data class UnitDispersion(override val symbolRes: Int, override val converter: UnitConverter) : Dimension<UnitDispersion> {
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
        val picoseconds  = R.string.unit_picoseconds
        val nanoseconds  = R.string.unit_nanoseconds
        val microseconds = R.string.unit_microseconds
        val milliseconds = R.string.unit_milliseconds
        val seconds      = R.string.unit_seconds
        val minutes      = R.string.unit_minutes
        val hours        = R.string.unit_hours
    }

    private object Coefficient {
        val picoseconds  = 1e-12f
        val nanoseconds  = 1e-9f
        val microseconds = 1e-6f
        val milliseconds = 1e-3f
        val seconds      = 1.0f
        val minutes      = 60.0f
        val hours        = 3600.0f
    }

    private constructor(symbol: Int, coefficient: Float) : this(symbol, UnitConverterLinear(coefficient))

    companion object {
        val picoseconds  get() = UnitDuration(Symbol.picoseconds, Coefficient.picoseconds)
        val nanoseconds  get() = UnitDuration(Symbol.nanoseconds, Coefficient.nanoseconds)
        val microseconds get() = UnitDuration(Symbol.microseconds, Coefficient.microseconds)
        val milliseconds get() = UnitDuration(Symbol.milliseconds, Coefficient.milliseconds)
        val seconds      get() = UnitDuration(Symbol.seconds, Coefficient.seconds)
        val minutes      get() = UnitDuration(Symbol.minutes, Coefficient.minutes)
        val hours        get() = UnitDuration(Symbol.hours, Coefficient.hours)
    }
}

data class UnitElectricCharge(override val symbolRes: Int, override val converter: UnitConverter) : Dimension<UnitElectricCharge> {
    override fun baseUnit() = coulombs

    private object Symbol {
        val coulombs         = R.string.unit_coulombs
        val megaampereHours  = R.string.unit_megaampereHours
        val kiloampereHours  = R.string.unit_kiloampereHours
        val ampereHours      = R.string.unit_ampereHours
        val milliampereHours = R.string.unit_milliampereHours
        val microampereHours = R.string.unit_microampereHours
    }

    private object Coefficient {
        val coulombs         = 1.0f
        val megaampereHours  = 3.6e9f
        val kiloampereHours  = 3600000.0f
        val ampereHours      = 3600.0f
        val milliampereHours = 3.6f
        val microampereHours = 0.0036f
    }

    private constructor(symbol: Int, coefficient: Float) : this(symbol, UnitConverterLinear(coefficient))

    companion object {
        val coulombs         get() = UnitElectricCharge(Symbol.coulombs, Coefficient.coulombs)
        val megaampereHours  get() = UnitElectricCharge(Symbol.megaampereHours, Coefficient.megaampereHours)
        val kiloampereHours  get() = UnitElectricCharge(Symbol.kiloampereHours, Coefficient.kiloampereHours)
        val ampereHours      get() = UnitElectricCharge(Symbol.ampereHours, Coefficient.ampereHours)
        val milliampereHours get() = UnitElectricCharge(Symbol.milliampereHours, Coefficient.milliampereHours)
        val microampereHours get() = UnitElectricCharge(Symbol.microampereHours, Coefficient.microampereHours)
    }
}

data class UnitElectricCurrent(override val symbolRes: Int, override val converter: UnitConverter) : Dimension<UnitElectricCurrent> {
    override fun baseUnit() = amperes

    private object Symbol {
        val megaamperes  = R.string.unit_megaamperes
        val kiloamperes  = R.string.unit_kiloamperes
        val amperes      = R.string.unit_amperes
        val milliamperes = R.string.unit_milliamperes
        val microamperes = R.string.unit_microamperes
    }

    private object Coefficient {
        val megaamperes  = 1e6f
        val kiloamperes  = 1e3f
        val amperes      = 1.0f
        val milliamperes = 1e-3f
        val microamperes = 1e-6f
    }

    private constructor(symbol: Int, coefficient: Float) : this(symbol, UnitConverterLinear(coefficient))

    companion object {
        val megaamperes  get() = UnitElectricCurrent(Symbol.megaamperes, Coefficient.megaamperes)
        val kiloamperes  get() = UnitElectricCurrent(Symbol.kiloamperes, Coefficient.kiloamperes)
        val amperes      get() = UnitElectricCurrent(Symbol.amperes, Coefficient.amperes)
        val milliamperes get() = UnitElectricCurrent(Symbol.milliamperes, Coefficient.milliamperes)
        val microamperes get() = UnitElectricCurrent(Symbol.microamperes, Coefficient.microamperes)
    }
}

data class UnitElectricPotentialDifference(override val symbolRes: Int, override val converter: UnitConverter) : Dimension<UnitElectricPotentialDifference> {
    override fun baseUnit() = volts

    private object Symbol {
        val megavolts  = R.string.unit_megavolts
        val kilovolts  = R.string.unit_kilovolts
        val volts      = R.string.unit_volts
        val millivolts = R.string.unit_millivolts
        val microvolts = R.string.unit_microvolts
    }

    private object Coefficient {
        val megavolts  = 1e6f
        val kilovolts  = 1e3f
        val volts      = 1.0f
        val millivolts = 1e-3f
        val microvolts = 1e-6f
    }

    private constructor(symbol: Int, coefficient: Float) : this(symbol, UnitConverterLinear(coefficient))

    companion object {
        val megavolts  get() = UnitElectricPotentialDifference(Symbol.megavolts, Coefficient.megavolts)
        val kilovolts  get() = UnitElectricPotentialDifference(Symbol.kilovolts, Coefficient.kilovolts)
        val volts      get() = UnitElectricPotentialDifference(Symbol.volts, Coefficient.volts)
        val millivolts get() = UnitElectricPotentialDifference(Symbol.millivolts, Coefficient.millivolts)
        val microvolts get() = UnitElectricPotentialDifference(Symbol.microvolts, Coefficient.microvolts)
    }
}

data class UnitElectricResistance(override val symbolRes: Int, override val converter: UnitConverter) : Dimension<UnitElectricResistance> {
    override fun baseUnit() = ohms

    private object Symbol {
        val megaohms  = R.string.unit_megaohms
        val kiloohms  = R.string.unit_kiloohms
        val ohms      = R.string.unit_ohms
        val milliohms = R.string.unit_milliohms
        val microohms = R.string.unit_microohms
    }

    private object Coefficient {
        const val megaohms  = 1e6f
        const val kiloohms  = 1e3f
        const val ohms      = 1.0f
        const val milliohms = 1e-3f
        const val microohms = 1e-6f
    }

    private constructor(symbol: Int, coefficient: Float) : this(symbol, UnitConverterLinear(coefficient))

    companion object {
        val megaohms  get() = UnitElectricResistance(Symbol.megaohms, Coefficient.megaohms)
        val kiloohms  get() = UnitElectricResistance(Symbol.kiloohms, Coefficient.kiloohms)
        val ohms      get() = UnitElectricResistance(Symbol.ohms, Coefficient.ohms)
        val milliohms get() = UnitElectricResistance(Symbol.milliohms, Coefficient.milliohms)
        val microohms get() = UnitElectricResistance(Symbol.microohms, Coefficient.microohms)
    }
}

data class UnitEnergy(override val symbolRes: Int, override val converter: UnitConverter) : Dimension<UnitEnergy> {
    override fun baseUnit() = joules

    private object Symbol {
        val kilojoules       = R.string.unit_kilojoules
        val joules           = R.string.unit_joules
        val kilocalories     = R.string.unit_kilocalories
        val calories         = R.string.unit_calories
        val kilowattHours    = R.string.unit_kilowattHours
    }

    private object Coefficient {
        const val kilojoules       = 1e3f
        const val joules           = 1.0f
        const val kilocalories     = 4184.0f
        const val calories         = 4.184f
        const val kilowattHours    = 3600000.0f
    }

    private constructor(symbol: Int, coefficient: Float) : this(symbol, UnitConverterLinear(coefficient))

    companion object {
        val kilojoules       get() = UnitEnergy(Symbol.kilojoules, Coefficient.kilojoules)
        val joules           get() = UnitEnergy(Symbol.joules, Coefficient.joules)
        val kilocalories     get() = UnitEnergy(Symbol.kilocalories, Coefficient.kilocalories)
        val calories         get() = UnitEnergy(Symbol.calories, Coefficient.calories)
        val kilowattHours    get() = UnitEnergy(Symbol.kilowattHours, Coefficient.kilowattHours)
    }
}

data class UnitFrequency(override val symbolRes: Int, override val converter: UnitConverter) : Dimension<UnitFrequency> {
    override fun baseUnit() = hertz

    private object Symbol {
        val terahertz       = R.string.unit_terahertz
        val gigahertz       = R.string.unit_gigahertz
        val megahertz       = R.string.unit_megahertz
        val kilohertz       = R.string.unit_kilohertz
        val hertz           = R.string.unit_hertz
        val millihertz      = R.string.unit_millihertz
        val microhertz      = R.string.unit_microhertz
        val nanohertz       = R.string.unit_nanohertz
        val framesPerSecond = R.string.unit_framesPerSecond
    }

    private object Coefficient {
        const val terahertz       = 1e12f
        const val gigahertz       = 1e9f
        const val megahertz       = 1e6f
        const val kilohertz       = 1e3f
        const val hertz           = 1.0f
        const val millihertz      = 1e-3f
        const val microhertz      = 1e-6f
        const val nanohertz       = 1e-9f
        const val framesPerSecond = 1.0f
    }

    private constructor(symbol: Int, coefficient: Float) : this(symbol, UnitConverterLinear(coefficient))

    companion object {
        val terahertz       get() = UnitFrequency(Symbol.terahertz, Coefficient.terahertz)
        val gigahertz       get() = UnitFrequency(Symbol.gigahertz, Coefficient.gigahertz)
        val megahertz       get() = UnitFrequency(Symbol.megahertz, Coefficient.megahertz)
        val kilohertz       get() = UnitFrequency(Symbol.kilohertz, Coefficient.kilohertz)
        val hertz           get() = UnitFrequency(Symbol.hertz, Coefficient.hertz)
        val millihertz      get() = UnitFrequency(Symbol.millihertz, Coefficient.millihertz)
        val microhertz      get() = UnitFrequency(Symbol.microhertz, Coefficient.microhertz)
        val nanohertz       get() = UnitFrequency(Symbol.nanohertz, Coefficient.nanohertz)
        val framesPerSecond get() = UnitFrequency(Symbol.framesPerSecond, Coefficient.framesPerSecond)
    }
}

data class UnitFuelEfficiency(override val symbolRes: Int, override val converter: UnitConverter) : Dimension<UnitFuelEfficiency> {
    override fun baseUnit() = litersPer100Kilometers

    private object Symbol {
        val litersPer100Kilometers   = R.string.unit_litersPer100Kilometers
        val milesPerImperialGallon   = R.string.unit_milesPerImperialGallon
        val milesPerGallon           = R.string.unit_milesPerGallon
    }

    private object Coefficient {
        const val litersPer100Kilometers   = 1.0f
        const val milesPerImperialGallon   = 282.481f
        const val milesPerGallon           = 235.215f
    }

    private constructor(symbol: Int, coefficient: Float) : this(symbol, UnitConverterLinear(coefficient))

    companion object {
        val litersPer100Kilometers   get() = UnitFuelEfficiency(Symbol.litersPer100Kilometers, Coefficient.litersPer100Kilometers)
        val milesPerImperialGallon   get() = UnitFuelEfficiency(Symbol.milesPerImperialGallon, Coefficient.milesPerImperialGallon)
        val milesPerGallon           get() = UnitFuelEfficiency(Symbol.milesPerGallon, Coefficient.milesPerGallon)
    }
}

class UnitSpeed(override val symbolRes: Int, override val converter: UnitConverter) : Dimension<SpeedUnit> {
    private object Symbol {
        val metersPerSecond      = R.string.unit_mps
        val kilometersPerHour    = R.string.unit_kmph
        val kilometersPerSecond  = R.string.unit_kmps
        val milesPerHour         = R.string.unit_miph
    }

    private object Coefficient {
        val metersPerSecond    = 1.0f
        val kilometersPerHour  = 0.277778f
        val kilometersPerSecond = 0.001f
        val milesPerHour       = 0.44704f
    }

    private constructor(symbolRes: Int, coefficient: Float) : this(
        symbolRes,
        UnitConverterLinear(coefficient)
    )

    override fun baseUnit() = metersPerSecond

    companion object {
        val metersPerSecond get() = UnitSpeed(Symbol.metersPerSecond, Coefficient.metersPerSecond)
        val kilometersPerHour get() = UnitSpeed(Symbol.kilometersPerHour, Coefficient.kilometersPerHour)
        val milesPerHour get() = UnitSpeed(Symbol.milesPerHour, Coefficient.milesPerHour)
        val kilometersPerSecond get() = UnitSpeed(Symbol.kilometersPerSecond, Coefficient.kilometersPerSecond)
    }
}

class UnitRpm(symbolRes: Int, converter: UnitConverter) : Dimension<UnitRpm>(symbolRes, converter) {
    private object Symbol {
        val rpm = R.string.unit_rpm
    }

    private object Coefficient {
        val rpm = 1f
    }

    override fun baseUnit() = rpm

    companion object {
        val rpm get() = UnitRpm(Symbol.rpm, UnitConverterLinear(Coefficient.rpm))
    }
}

sealed interface EnergyUnit : MeasurementUnit, SensorUnitConvertible<EnergyUnit> {
    object Kw : EnergyUnit {
        override val symbolRes = R.string.unit_kw
    }

    override fun convert(value: Float, targetUnit: EnergyUnit): Float {
        return when (this) {
            Kw -> value
        }
    }
}

sealed interface TemperatureUnit : MeasurementUnit, SensorUnitConvertible<TemperatureUnit> {
    object Celsius : TemperatureUnit {
        override val symbolRes: Int = R.string.unit_celsius
    }

    object Fahrenheit : TemperatureUnit {
        override val symbolRes: Int = R.string.unit_fahrenheit
    }

    override fun convert(value: Float, targetUnit: TemperatureUnit): Float {
        return when (Pair(this, targetUnit)) {
            Pair(Celsius, Fahrenheit) -> (value * (9f / 5f)) + 32f
            Pair(Fahrenheit, Celsius) -> (value - 32f) * (9f / 5f)
            else -> value
        }
    }
}

sealed interface SpeedUnit : MeasurementUnit, SensorUnitConvertible<SpeedUnit> {
    val timeFactor: Float
    val distanceFactor: Float

    // Kilometers per hour
    object KMph : SpeedUnit {
        override val timeFactor: Float = 1f
        override val distanceFactor: Float = 1f
        override val symbolRes: Int = R.string.unit_kmph
    }

    // Meters per hour
    object Mph : SpeedUnit {
        override val timeFactor: Float = 1f
        override val distanceFactor: Float = 1000f
        override val symbolRes: Int = R.string.unit_mph
    }

    // Meters per second
    object Mps : SpeedUnit {
        override val timeFactor: Float = SecondsPerHour
        override val distanceFactor: Float = 1000f
        override val symbolRes: Int = R.string.unit_mps
    }

    // Miles per hour
    object Miph : SpeedUnit {
        override val timeFactor: Float = SecondsPerHour
        override val distanceFactor: Float = 0.6213712f
        override val symbolRes: Int = R.string.unit_miph
    }

    override fun convert(value: Float, targetUnit: SpeedUnit): Float {
        val baseValue = (value / distanceFactor) * targetUnit.distanceFactor
        return (baseValue / timeFactor) * targetUnit.timeFactor
    }
}

data class Measurable<U : MeasurementUnit>(val value: Float, val unit: U)
data class ClosedMeasurableRange<U: MeasurementUnit>(val start: Float, val endInclusive: Float, val unit: U)
data class MeasurableRanged<U: MeasurementUnit>(val value: Float, val unit: U, val range: ClosedMeasurableRange<U>)

fun <BaseUnit> Measurable<BaseUnit>.convert(targetUnit: BaseUnit): Measurable<BaseUnit> where BaseUnit : MeasurementUnit, BaseUnit : SensorUnitConvertible<BaseUnit> {
    return if (unit == targetUnit) {
        this
    } else {
        Measurable(
            value = unit.convert(value, targetUnit),
            unit = targetUnit
        )
    }
}
fun <BaseUnit> ClosedMeasurableRange<BaseUnit>.convert(targetUnit: BaseUnit): ClosedMeasurableRange<BaseUnit> where BaseUnit : MeasurementUnit, BaseUnit : SensorUnitConvertible<BaseUnit> {
    return if (unit == targetUnit) {
        this
    } else {
        ClosedMeasurableRange(
            start = unit.convert(start, targetUnit),
            endInclusive = unit.convert(endInclusive, targetUnit),
            unit = targetUnit,
        )
    }
}

fun <BaseUnit> Measurable<BaseUnit>.normalized(range: ClosedMeasurableRange<BaseUnit>): Float where BaseUnit : MeasurementUnit, BaseUnit : SensorUnitConvertible<BaseUnit> {
    val value = convert(range.unit).value.coerceAtLeast(range.start)
    return (value - range.start) / range.endInclusive
}

fun <BaseUnit> MeasurableRanged<BaseUnit>.convert(targetUnit: BaseUnit): MeasurableRanged<BaseUnit> where BaseUnit: MeasurementUnit, BaseUnit : SensorUnitConvertible<BaseUnit> {
    return MeasurableRanged(value = unit.convert(value, targetUnit), targetUnit, range)
}

fun <BaseUnit> MeasurableRanged<BaseUnit>.normalized(): Float where BaseUnit : MeasurementUnit, BaseUnit : SensorUnitConvertible<BaseUnit> {
    val value = convert(range.unit).value.coerceAtLeast(range.start)
    return (value - range.start) / range.endInclusive
}