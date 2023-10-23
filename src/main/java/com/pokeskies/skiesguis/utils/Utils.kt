package com.pokeskies.skiesguis.utils

import com.google.gson.*
import com.mojang.serialization.Codec
import com.mojang.serialization.JsonOps
import com.mojang.serialization.codecs.RecordCodecBuilder
import com.pokeskies.skiesguis.SkiesGUIs
import net.kyori.adventure.text.minimessage.MiniMessage
import net.minecraft.registry.Registry
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import java.lang.reflect.Type
import java.util.function.Function


object Utils {
    private val miniMessage: MiniMessage = MiniMessage.miniMessage()

    fun parsePlaceholders(player: ServerPlayerEntity, text: String): String {
        return SkiesGUIs.INSTANCE.placeholderManager.parse(player, text)
    }

    fun deseralizeText(text: String): Text {
        return SkiesGUIs.INSTANCE.adventure!!.toNative(miniMessage.deserialize(text))
    }

    fun debug(message: String, bypassCheck: Boolean = false) {
        if (bypassCheck || SkiesGUIs.INSTANCE.configManager.config.debug)
            SkiesGUIs.LOGGER.info("[SkiesGUIs] DEBUG: $message")
    }

    fun error(message: String) {
        SkiesGUIs.LOGGER.error("[SkiesGUIs] ERROR: $message")
    }

    fun info(message: String) {
        SkiesGUIs.LOGGER.info("[SkiesGUIs] $message")
    }

    // Thank you to Patbox for these wonderful serializers =)
    data class RegistrySerializer<T>(val registry: Registry<T>) : JsonSerializer<T>, JsonDeserializer<T> {
        @Throws(JsonParseException::class)
        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): T? {
            var parsed = if (json.isJsonPrimitive) registry.get(Identifier.tryParse(json.asString)) else null
            if (parsed == null)
                error("There was an error while deserializing a Registry Type: $registry")
            return parsed
        }
        override fun serialize(src: T, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return JsonPrimitive(registry.getId(src).toString())
        }
    }

    data class CodecSerializer<T>(val codec: Codec<T>) : JsonSerializer<T>, JsonDeserializer<T> {
        @Throws(JsonParseException::class)
        override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): T? {
            return try {
                codec.decode(JsonOps.INSTANCE, json).getOrThrow(false) { }.first
            } catch (e: Throwable) {
                error("There was an error while deserializing a Codec: $codec")
                null
            }
        }

        override fun serialize(src: T?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
            return try {
                if (src != null)
                    codec.encodeStart(JsonOps.INSTANCE, src).getOrThrow(false) { }
                else
                    JsonNull.INSTANCE
            } catch (e: Throwable) {
                error("There was an error while serializing a Codec: $codec")
                JsonNull.INSTANCE
            }
        }
    }
}

fun <A, B> Codec<A>.recordCodec(id: String, getter: Function<B, A>): RecordCodecBuilder<B, A> {
    return this.fieldOf(id).forGetter(getter)
}

fun <A, B> Codec<A>.optionalRecordCodec(id: String, getter: Function<B, A>, default: A): RecordCodecBuilder<B, A> {
    return this.fieldOf(id).orElse(default).forGetter(getter)
}