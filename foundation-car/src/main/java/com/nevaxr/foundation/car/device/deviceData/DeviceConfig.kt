package com.nevaxr.device.deviceData

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

@Serializable
data class DeviceConfig(
    val devices: List<Device>,
    val features: List<Feature>
)

@Serializable
data class Device(
    val name: String,
    val key: String,
    val features: List<Feature>
)

@Serializable
data class Feature(
    val name: String,
    val key: String,
    val type: String,
    val range: List<Double>? = null,
    val unit: String? = null,
    @Serializable(with = FeatureValuesSerializer::class)
    val values: List<FeatureValue>? = null,
    val elementType: String? = null,
    val readMethod: String? = null,
    val readId: String? = null
)

@Serializable
data class FeatureValue(
    val name: String? = null,
    val value: JsonElement? = null,
)

data class DataPoint<T>(
    val key: String,
    val value: T,
    val timestamp: Long,
    val range: ClosedFloatingPointRange<Float>? = null,
)

@OptIn(ExperimentalSerializationApi::class)
object FeatureValuesSerializer : KSerializer<List<FeatureValue>> {
    private val listSerializer = ListSerializer(FeatureValue.serializer())

    override val descriptor = listSerializer.descriptor

    override fun deserialize(decoder: Decoder): List<FeatureValue> {
        val jsonDecoder = decoder as JsonDecoder
        val element = jsonDecoder.decodeJsonElement()

        return when (element) {
            is JsonArray -> {
                element.map { item ->
                    when (item) {
                        is JsonPrimitive -> FeatureValue(
                            value = item,
                            name = null
                        )  // Changed this line
                        is JsonObject -> {
                            FeatureValue(
                                name = item.jsonObject["name"]?.jsonPrimitive?.content,
                                value = item.jsonObject["value"]
                            )
                        }

                        else -> FeatureValue(null, null)
                    }
                }
            }

            else -> emptyList()
        }
    }

    override fun serialize(encoder: Encoder, value: List<FeatureValue>) {
        listSerializer.serialize(encoder, value)
    }
}