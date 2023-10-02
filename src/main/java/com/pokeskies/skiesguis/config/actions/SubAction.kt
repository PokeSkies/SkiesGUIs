package com.pokeskies.skiesguis.config.actions

import com.mojang.serialization.Codec
import com.pokeskies.skiesguis.config.requirements.ClickRequirement
import java.util.*

abstract class SubAction(
    click: ClickType
): Action(click, Optional.empty()) {
    companion object {
        val CODEC: Codec<SubAction> = ActionType.CODEC.dispatch("type", {it.getType() }, { it.codec as Codec<out SubAction>? })
    }
}