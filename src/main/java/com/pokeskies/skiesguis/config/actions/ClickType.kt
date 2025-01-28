package com.pokeskies.skiesguis.config.actions

import ca.landonjw.gooeylibs2.api.button.ButtonClick
import com.google.gson.*
import com.pokeskies.skiesguis.utils.Utils
import net.minecraft.util.StringRepresentable
import java.lang.reflect.Type

enum class ClickType(val identifier: String, val buttonClicks: List<ButtonClick>) : StringRepresentable {
    LEFT_CLICK("left_click", listOf(ButtonClick.LEFT_CLICK)),
    SHIFT_LEFT_CLICK("shift_left_click", listOf(ButtonClick.SHIFT_LEFT_CLICK)),
    ANY_LEFT_CLICK("any_left_click", listOf(ButtonClick.LEFT_CLICK, ButtonClick.SHIFT_LEFT_CLICK)),

    RIGHT_CLICK("right_click", listOf(ButtonClick.RIGHT_CLICK)),
    SHIFT_RIGHT_CLICK("shift_right_click", listOf(ButtonClick.SHIFT_RIGHT_CLICK)),
    ANY_RIGHT_CLICK("any_right_click", listOf(ButtonClick.RIGHT_CLICK, ButtonClick.SHIFT_RIGHT_CLICK)),

    ANY_CLICK("any_click", listOf(ButtonClick.LEFT_CLICK, ButtonClick.SHIFT_LEFT_CLICK, ButtonClick.RIGHT_CLICK, ButtonClick.SHIFT_RIGHT_CLICK)),
    ANY_MAIN_CLICK("any_main_click", listOf(ButtonClick.LEFT_CLICK, ButtonClick.RIGHT_CLICK)),
    ANY_SHIFT_CLICK("any_shift_click", listOf(ButtonClick.SHIFT_LEFT_CLICK, ButtonClick.SHIFT_RIGHT_CLICK)),

    MIDDLE_CLICK("middle_click", listOf(ButtonClick.MIDDLE_CLICK)),
    THROW("throw", listOf(ButtonClick.THROW)),

    ANY("any", ButtonClick.values().toList());

    override fun getSerializedName(): String {
        return this.identifier
    }

    companion object {
        fun valueOfAnyCase(name: String): ClickType? {
            for (type in values()) {
                if (name.equals(type.identifier, true)) return type
            }
            return null
        }
    }

    internal class ClickTypeAdaptor : JsonSerializer<ClickType>, JsonDeserializer<ClickType> {
        override fun serialize(src: ClickType, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return JsonPrimitive(src.identifier)
        }

        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): ClickType {
            val click = valueOfAnyCase(json.asString)

            if (click == null) {
                Utils.printError("Could not deserialize Click Type '${json.asString}'!")
                return ANY
            }

            return click
        }
    }
}
