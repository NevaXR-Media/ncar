package com.nevaxr.foundation.car.device

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
data class DeviceData(
    val name: String,
    val key: String,
    val features: List<Feature> = emptyList(),
)

@Serializable
data class Feature(
    val name: String,
    val key: String,
    val type: String,
    val range: List<Double>? = null,
    val unit: String? = null,
    val values: List<StateMapping>? = null,
    val elementType: String? = null
)
@Serializable
data class StateData(
    val gearState: List<StateMapping> = emptyList(),
    val drivingMode: List<StateMapping> = emptyList(),
    val speed: SpeedData? = null
)

@Serializable
data class StateMapping(
    val name: String,
    val value: Int
)

@Serializable
data class SpeedData(
    @Serializable(with = FloatRangeSerializer::class)
    val range: ClosedFloatingPointRange<Float>,
    val type: String,
    val unit: String
)

object FloatRangeSerializer : KSerializer<ClosedFloatingPointRange<Float>> {
    override val descriptor = buildClassSerialDescriptor("FloatRange") {
        element<List<Float>>("range")
        element<List<Float>>("range")

    }

    override fun serialize(encoder: Encoder, value: ClosedFloatingPointRange<Float>) {
        encoder.encodeSerializableValue(
            ListSerializer(Float.serializer()),
            listOf(value.start, value.endInclusive)
        )
    }

    override fun deserialize(decoder: Decoder): ClosedFloatingPointRange<Float> {
        val list = decoder.decodeSerializableValue(ListSerializer(Float.serializer()))
        return list[0]..list[1]
    }
}