package com.pokeskies.skiesguis.data

import com.google.gson.*
import java.lang.reflect.Type

data class MetadataValue(
    val type: MetadataType,
    var value: Any?
) {
    internal class Adapter : JsonSerializer<MetadataValue>, JsonDeserializer<MetadataValue> {
        override fun serialize(src: MetadataValue, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            val obj = JsonObject()
            obj.addProperty("type", src.type.name)

            val value = src.value
            if (value == null) {
                obj.add("value", JsonNull.INSTANCE)
            } else {
                val prim = when (src.type) {
                    MetadataType.INTEGER -> JsonPrimitive((value as Number))
                    MetadataType.DOUBLE -> JsonPrimitive((value as Number))
                    MetadataType.LONG -> JsonPrimitive((value as Number))
                    MetadataType.BOOLEAN -> JsonPrimitive(value as Boolean)
                    MetadataType.STRING -> JsonPrimitive(value as String)
                }
                obj.add("value", prim)
            }

            return obj
        }

        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): MetadataValue {
            if (!json.isJsonObject) error("Error while deserializing MetadataValue. Expected JSON object!")
            val obj = json.asJsonObject

            val type = obj.get("type")?.asString ?: error("Found a missing 'type' field when deserializing a MetadataValue object!")
            val metaType = MetadataType.getFromName(type) ?: error("Unknown MetadataType '$type' found when deserializing a MetadataValue object!")

            val valueElement = obj.get("value")
            val value: Any? = if (valueElement == null || valueElement.isJsonNull) {
                null
            } else {
                when (metaType) {
                    MetadataType.STRING -> valueElement.asString
                    MetadataType.INTEGER -> valueElement.asInt
                    MetadataType.DOUBLE -> valueElement.asDouble
                    MetadataType.LONG -> valueElement.asLong
                    MetadataType.BOOLEAN -> valueElement.asBoolean
                }
            }

            return MetadataValue(metaType, value)
        }
    }
}