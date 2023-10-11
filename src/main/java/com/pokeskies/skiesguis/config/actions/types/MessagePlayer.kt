package com.pokeskies.skiesguis.config.actions.types

import com.pokeskies.skiesguis.config.actions.Action
import com.pokeskies.skiesguis.config.actions.ActionType
import com.pokeskies.skiesguis.config.actions.ClickType
import com.pokeskies.skiesguis.config.requirements.RequirementOptions
import com.pokeskies.skiesguis.utils.Utils
import net.minecraft.server.network.ServerPlayerEntity

class MessagePlayer(
    type: ActionType = ActionType.MESSAGE,
    click: ClickType = ClickType.ANY,
    delay: Long = 0,
    chance: Double = 0.0,
    requirements: RequirementOptions? = RequirementOptions(),
    private val message: List<String> = emptyList()
) : Action(type, click, delay, chance, requirements) {
    override fun execute(player: ServerPlayerEntity) {
        Utils.debug("Attempting to execute a ${type.identifier} Action: $this")
        for (line in message) {
            player.sendMessage(Utils.deseralizeText(parsePlaceholders(player, line)))
        }
    }

    override fun toString(): String {
        return "MessagePlayer(type=$type, click=$click, requirements=$requirements, message=$message)"
    }
}