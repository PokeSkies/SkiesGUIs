package com.pokeskies.skiesguis.gui

import com.google.gson.*
import com.pokeskies.skiesguis.utils.Utils
import eu.pb4.sgui.api.ClickType
import net.minecraft.util.StringRepresentable
import java.lang.reflect.Type

enum class GenericClickType(val identifier: String, val buttonClicks: List<ClickType>): StringRepresentable {
    LEFT_CLICK("left_click", listOf(ClickType.MOUSE_LEFT)),
    SHIFT_LEFT_CLICK("shift_left_click", listOf(ClickType.MOUSE_LEFT_SHIFT)),
    ANY_LEFT_CLICK("any_left_click", listOf(ClickType.MOUSE_LEFT, ClickType.MOUSE_LEFT_SHIFT)),

    RIGHT_CLICK("right_click", listOf(ClickType.MOUSE_RIGHT)),
    SHIFT_RIGHT_CLICK("shift_right_click", listOf(ClickType.MOUSE_RIGHT_SHIFT)),
    ANY_RIGHT_CLICK("any_right_click", listOf(ClickType.MOUSE_RIGHT, ClickType.MOUSE_RIGHT_SHIFT)),

    ANY_CLICK("any_click",
        listOf(ClickType.MOUSE_LEFT, ClickType.MOUSE_LEFT_SHIFT, ClickType.MOUSE_RIGHT, ClickType.MOUSE_RIGHT_SHIFT)
    ),
    ANY_MAIN_CLICK("any_main_click", listOf(ClickType.MOUSE_LEFT, ClickType.MOUSE_RIGHT)),
    ANY_SHIFT_CLICK("any_shift_click", listOf(ClickType.MOUSE_LEFT_SHIFT, ClickType.MOUSE_RIGHT_SHIFT)),

    MIDDLE_CLICK("middle_click", listOf(ClickType.MOUSE_MIDDLE)),
    THROW("throw", listOf(ClickType.DROP)),

    ANY("any", ClickType.entries);

    override fun getSerializedName(): String {
        return this.identifier
    }

    companion object {
        fun valueOfAnyCase(name: String): GenericClickType? {
            for (type in entries) {
                if (name.equals(type.identifier, true)) return type
            }
            return null
        }

        fun fromClickType(clickType: ClickType): List<GenericClickType> {
            return entries.filter { it.buttonClicks.contains(clickType) }
        }
    }

    internal class Adapter : JsonSerializer<GenericClickType>, JsonDeserializer<GenericClickType> {
        override fun serialize(src: GenericClickType, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return JsonPrimitive(src.identifier)
        }

        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): GenericClickType {
            val click = valueOfAnyCase(json.asString)

            if (click == null) {
                Utils.printError("Could not deserialize Click Type '${json.asString}'!")
                return ANY
            }

            return click
        }
    }
}