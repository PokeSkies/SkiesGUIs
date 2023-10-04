package com.pokeskies.skiesguis.config.actions.types

import com.pokeskies.skiesguis.config.actions.Action
import com.pokeskies.skiesguis.config.actions.ActionType
import com.pokeskies.skiesguis.config.actions.ClickType
import com.pokeskies.skiesguis.config.requirements.RequirementOptions
import com.pokeskies.skiesguis.utils.Utils
import net.minecraft.server.network.ServerPlayerEntity

class GiveXP(
    type: ActionType = ActionType.GIVE_XP,
    click: ClickType = ClickType.ANY,
    clickRequirements: RequirementOptions? = null,
    private val amount: Int,
    private val level: Boolean = false
) : Action(type, click, clickRequirements) {
    override fun execute(player: ServerPlayerEntity) {
        Utils.debug("Attempting to execute a ${type.identifier} Action: $this")
        if (level) {
            player.addExperienceLevels(amount)
        } else {
            player.addExperience(amount)
        }
    }

    override fun toString(): String {
        return "GiveXP(type=$type, click=$click, clickRequirements=$clickRequirements, amount=$amount, level=$level)"
    }
}