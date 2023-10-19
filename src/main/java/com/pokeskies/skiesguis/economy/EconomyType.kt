package com.pokeskies.skiesguis.economy

import com.google.gson.*
import com.pokeskies.skiesguis.utils.Utils
import net.fabricmc.loader.api.FabricLoader
import java.lang.reflect.Type

enum class EconomyType(
    val identifier: String,
    val modId: String
) {
    IMPACTOR("impactor", "impactor"),
    PEBBLES("pebbles", "pebbles-economy");

    fun isPresent() : Boolean {
        return FabricLoader.getInstance().isModLoaded(modId)
    }

    companion object {
        fun valueOfAnyCase(name: String): EconomyType? {
            for (type in values()) {
                if (name.equals(type.identifier, true)) return type
            }
            return null
        }
    }

    internal class EconomyTypeAdaptor : JsonSerializer<EconomyType>, JsonDeserializer<EconomyType> {
        override fun serialize(src: EconomyType, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return JsonPrimitive(src.identifier)
        }

        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): EconomyType {
            val economyType = valueOfAnyCase(json.asString)

            if (economyType == null) {
                Utils.error("Could not deserialize EconomyType '${json.asString}'! Falling back to IMPACTOR")
                return IMPACTOR
            }

            return economyType
        }
    }
}