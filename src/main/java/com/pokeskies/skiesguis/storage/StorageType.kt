package com.pokeskies.skiesguis.storage

import com.google.gson.*
import com.pokeskies.skiesguis.utils.Utils
import java.lang.reflect.Type

enum class StorageType(val identifier: String) {
    JSON("json"),
    MONGO("mongo"),
    MYSQL("mysql"),
    SQLITE("sqlite");

    companion object {
        fun valueOfAnyCase(identifier: String): StorageType? {
            for (type in entries) {
                if (identifier.equals(type.identifier, true)) return type
            }
            return null
        }
    }

    internal class Adapter: JsonSerializer<StorageType>, JsonDeserializer<StorageType> {
        override fun serialize(src: StorageType, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return JsonPrimitive(src.identifier)
        }

        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): StorageType {
            val storageType = valueOfAnyCase(json.asString)

            if (storageType == null) {
                Utils.printError("Could not deserialize Storage Type '${json.asString}' using SQLite as backup!")
                return SQLITE
            }

            return storageType
        }
    }
}
