package com.pokeskies.skiesguis.config.actions

import ca.landonjw.gooeylibs2.api.button.ButtonClick
import net.minecraft.util.StringIdentifiable

enum class ClickType(val identifier: String, val buttonClicks: List<ButtonClick>) : StringIdentifiable {
    LEFT_CLICK("LEFT_CLICK", listOf(ButtonClick.LEFT_CLICK)),
    SHIFT_LEFT_CLICK("SHIFT_LEFT_CLICK", listOf(ButtonClick.SHIFT_LEFT_CLICK)),
    ANY_LEFT_CLICK("ANY_LEFT_CLICK", listOf(ButtonClick.LEFT_CLICK, ButtonClick.SHIFT_LEFT_CLICK)),

    RIGHT_CLICK("RIGHT_CLICK", listOf(ButtonClick.RIGHT_CLICK)),
    SHIFT_RIGHT_CLICK("SHIFT_RIGHT_CLICK", listOf(ButtonClick.SHIFT_RIGHT_CLICK)),
    ANY_RIGHT_CLICK("ANY_RIGHT_CLICK", listOf(ButtonClick.RIGHT_CLICK, ButtonClick.SHIFT_RIGHT_CLICK)),

    ANY_CLICK("ANY_CLICK", listOf(ButtonClick.LEFT_CLICK, ButtonClick.SHIFT_LEFT_CLICK, ButtonClick.RIGHT_CLICK, ButtonClick.SHIFT_RIGHT_CLICK)),
    ANY_MAIN_CLICK("ANY_MAIN_CLICK", listOf(ButtonClick.LEFT_CLICK, ButtonClick.RIGHT_CLICK)),
    ANY_SHIFT_CLICK("ANY_SHIFT_CLICK", listOf(ButtonClick.SHIFT_LEFT_CLICK, ButtonClick.SHIFT_RIGHT_CLICK)),

    MIDDLE_CLICK("MIDDLE_CLICK", listOf(ButtonClick.MIDDLE_CLICK)),
    THROW("THROW", listOf(ButtonClick.THROW)),

    ANY("ANY", ButtonClick.values().toList());

    override fun asString(): String {
        return this.identifier
    }
}