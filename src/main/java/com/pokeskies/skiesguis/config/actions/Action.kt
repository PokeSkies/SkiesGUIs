package com.pokeskies.skiesguis.config.actions

import ca.landonjw.gooeylibs2.api.button.ButtonClick
import com.google.gson.annotations.SerializedName
import com.pokeskies.skiesguis.config.requirements.RequirementOptions
import net.minecraft.server.network.ServerPlayerEntity

abstract class Action(
    val type: ActionType,
    val click: ClickType = ClickType.ANY,
    val requirements: RequirementOptions? = null
) {
    abstract fun execute(player: ServerPlayerEntity)

    fun matchesClick(buttonClick: ButtonClick): Boolean {
        return click.buttonClicks.contains(buttonClick)
    }

    fun checkClickRequirements(player: ServerPlayerEntity): Boolean {
        if (requirements != null) {
            for (requirement in requirements.requirements) {
                if (!requirement.value.check(player)) {
                    return false
                }
            }
        }
        return true
    }

    fun executeDenyActions(player: ServerPlayerEntity) {
        if (requirements != null) {
            for ((id, action) in requirements.denyActions) {
                action.execute(player)
            }
        }
    }

    fun executeSuccessActions(player: ServerPlayerEntity) {
        if (requirements != null) {
            for ((id, action) in requirements.successActions) {
                action.execute(player)
            }
        }
    }

    fun parsePlaceholders(player: ServerPlayerEntity, value: String): String {
        return value.replace("%player%", player.name.string)
    }

    override fun toString(): String {
        return "Action(type=$type, click=$click, requirements=$requirements)"
    }
}