package com.pokeskies.skiesguis.config.requirements

import com.google.gson.*
import com.pokeskies.skiesguis.config.requirements.types.extensions.plan.PlanPlaytimeRequirement
import com.pokeskies.skiesguis.config.requirements.types.internal.*
import net.fabricmc.loader.api.FabricLoader
import java.lang.reflect.Type

enum class RequirementType(val identifier: String, val clazz: Class<*>) {
    // Internal
    PERMISSION("permission", PermissionRequirement::class.java),
    ITEM("item", ItemRequirement::class.java),
    CURRENCY("currency", CurrencyRequirement::class.java),
    DIMENSION("dimension", DimensionRequirement::class.java),
    PLACEHOLDER("placeholder", PlaceholderRequirement::class.java),
    JAVASCRIPT("javascript", JavaScriptRequirement::class.java),
    XP("xp", XPRequirement::class.java),
    ADVANCEMENT("advancement", AdvancementRequirement::class.java),

    // Extensions
    MOLANG("molang", MolangRequirement::class.java),
    PLAN_PLAYTIME("plan_playtime", PlanPlaytimeRequirement::class.java);

    companion object {
        fun valueOfAnyCase(name: String): RequirementType? {
            for (type in RequirementType.entries) {
                if (name.equals(type.identifier, true)) return type
            }
            return null
        }
    }

    internal class RequirementTypeAdaptor : JsonSerializer<Requirement>, JsonDeserializer<Requirement> {
        override fun serialize(src: Requirement, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return context.serialize(src, src::class.java)
        }

        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Requirement {
            val jsonObject: JsonObject = json.getAsJsonObject()
            val value = jsonObject.get("type").asString
            if (value == "molang" && !FabricLoader.getInstance().isModLoaded("cobblemon")) {
                throw JsonParseException("Molang action is not supported without the Cobblemon mod")
            }
            val type: RequirementType? = RequirementType.valueOfAnyCase(value)
            return try {
                context.deserialize(json, type!!.clazz)
            } catch (e: NullPointerException) {
                throw JsonParseException("Could not deserialize requirement type: $type", e)
            }
        }
    }
}