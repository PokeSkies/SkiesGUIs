package com.pokeskies.skiesguis.config.actions

import com.google.gson.*
import com.pokeskies.skiesguis.config.actions.types.*
import net.fabricmc.loader.api.FabricLoader
import java.lang.reflect.Type


enum class ActionType(val identifier: String, val clazz: Class<*>) {
    COMMAND_CONSOLE("command_console", CommandConsole::class.java),
    COMMAND_PLAYER("command_player", CommandPlayer::class.java),
    MESSAGE("message", MessagePlayer::class.java),
    BROADCAST("broadcast", MessageBroadcast::class.java),
    PLAYSOUND("playsound", PlaySound::class.java),
    OPEN_GUI("open_gui", OpenGUI::class.java),
    CLOSE_GUI("close_gui", CloseGUI::class.java),
    REFRESH_GUI("refresh_gui", RefreshGUI::class.java),
    GIVE_XP("give_xp", GiveXP::class.java),
    CURRENCY_DEPOSIT("currency_deposit", CurrencyDeposit::class.java),
    CURRENCY_WITHDRAW("currency_withdraw", CurrencyWithdraw::class.java),
    CURRENCY_SET("currency_set", CurrencySet::class.java),
    GIVE_ITEM("give_item", GiveItem::class.java),
    TAKE_ITEM("take_item", TakeItem::class.java),
    MOLANG("molang", Molang::class.java),
    METADATA_SET("metadata_set", MetadataSet::class.java),
    METADATA_REMOVE("metadata_remove", MetadataRemove::class.java),
    METADATA_TOGGLE("metadata_toggle", MetadataToggle::class.java),
    METADATA_SHIFT("metadata_shift", MetadataShift::class.java);

    companion object {
        fun valueOfAnyCase(name: String): ActionType? {
            for (type in ActionType.entries) {
                if (name.equals(type.identifier, true)) return type
            }
            return null
        }
    }

    internal class Adapter : JsonSerializer<Action>, JsonDeserializer<Action> {
        override fun serialize(src: Action, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return context.serialize(src, src::class.java)
        }

        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Action {
            val jsonObject: JsonObject = json.getAsJsonObject()
            val value = jsonObject.get("type").asString
            if (value == "molang" && !FabricLoader.getInstance().isModLoaded("cobblemon")) {
                throw JsonParseException("Molang action is not supported without the Cobblemon mod")
            }
            val type: ActionType? = ActionType.valueOfAnyCase(value)
            return try {
                context.deserialize(json, type!!.clazz)
            } catch (e: NullPointerException) {
                throw JsonParseException("Could not deserialize action type: $value", e)
            }
        }
    }
}
