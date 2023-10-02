package com.pokeskies.skiesguis.config.actions

import com.mojang.serialization.Codec
import com.pokeskies.skiesguis.config.requirements.ClickRequirement
import java.util.*

abstract class SubAction(
    click: ClickType,
    clickRequirements: Optional<ClickRequirement>
): Action(click, clickRequirements) {
    companion object {
        val CODEC: Codec<Action> = ActionType.CODEC.dispatch("type", {it.getType() }, { it.codec })
    }
}