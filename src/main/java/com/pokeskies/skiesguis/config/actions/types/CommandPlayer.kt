package com.pokeskies.skiesguis.config.actions.types

import com.pokeskies.skiesguis.SkiesGUIs
import com.pokeskies.skiesguis.config.actions.Action
import com.pokeskies.skiesguis.config.actions.ActionType
import com.pokeskies.skiesguis.config.actions.ClickType
import com.pokeskies.skiesguis.config.requirements.RequirementOptions
import com.pokeskies.skiesguis.utils.Utils
import net.minecraft.server.network.ServerPlayerEntity

class CommandPlayer(
    type: ActionType = ActionType.COMMAND_PLAYER,
    click: ClickType = ClickType.ANY,
    delay: Long = 0,
    chance: Double = 0.0,
    requirements: RequirementOptions? = RequirementOptions(),
    private val commands: List<String> = emptyList()
) : Action(type, click, delay, chance, requirements) {
    override fun execute(player: ServerPlayerEntity) {
        Utils.debug("Attempting to execute a ${type.identifier} Action: $this")
        if (SkiesGUIs.INSTANCE.server?.commandManager == null) {
            Utils.error("There was an error while executing an action for player ${player.name}: Server was somehow null on command execution?")
            return
        }

        for (command in commands) {
            SkiesGUIs.INSTANCE.server?.commandManager?.executeWithPrefix(
                player.commandSource,
                Utils.parsePlaceholders(player, command)
            )
        }
    }

    override fun toString(): String {
        return "CommandPlayer(type=$type, click=$click, requirements=$requirements, commands=$commands)"
    }
}