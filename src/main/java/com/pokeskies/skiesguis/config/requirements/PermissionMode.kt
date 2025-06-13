package com.pokeskies.skiesguis.config.requirements

import com.google.gson.*
import com.pokeskies.skiesguis.utils.Utils
import me.lucko.fabric.api.permissions.v0.Permissions
import net.minecraft.server.level.ServerPlayer
import java.lang.reflect.Type

enum class PermissionMode(val identifier: String, val check: (List<String>, ServerPlayer) -> Boolean) {
    ANY("any", { permissions, player -> permissions.any { Permissions.check(player, it) } }),
    ALL("all", { permissions, player -> permissions.all { Permissions.check(player, it) } });

    companion object {
        fun valueOfAnyCase(name: String): PermissionMode? {
            for (type in PermissionMode.entries) {
                if (name.equals(type.identifier, true)) return type
            }
            return null
        }
    }

    internal class PermissionModeAdaptor : JsonSerializer<PermissionMode>, JsonDeserializer<PermissionMode> {
        override fun serialize(src: PermissionMode, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return JsonPrimitive(src.identifier)
        }

        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): PermissionMode {
            val mode = PermissionMode.valueOfAnyCase(json.asString)

            if (mode == null) {
                Utils.printError("Could not deserialize Permission Mode '${json.asString}'! Defaulting to ALL.")
                return ALL
            }

            return mode
        }
    }
}
