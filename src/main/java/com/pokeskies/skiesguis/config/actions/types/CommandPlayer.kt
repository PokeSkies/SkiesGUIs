package com.pokeskies.skiesguis.config.actions.types

import com.google.gson.annotations.JsonAdapter
import com.pokeskies.skiesguis.SkiesGUIs
import com.pokeskies.skiesguis.config.actions.Action
import com.pokeskies.skiesguis.config.actions.ActionType
import com.pokeskies.skiesguis.config.actions.ClickType
import com.pokeskies.skiesguis.config.requirements.RequirementOptions
import com.pokeskies.skiesguis.utils.FlexibleListAdaptorFactory
import com.pokeskies.skiesguis.utils.Utils
import net.minecraft.server.level.ServerPlayer

class CommandPlayer(
    type: ActionType = ActionType.COMMAND_PLAYER,
    click: List<ClickType> = listOf(ClickType.ANY),
    delay: Long = 0,
    chance: Double = 0.0,
    requirements: RequirementOptions? = RequirementOptions(),
    @JsonAdapter(FlexibleListAdaptorFactory::class)
    private val commands: List<String> = emptyList()
) : Action(type, click, delay, chance, requirements) {
    override fun executeAction(player: ServerPlayer) {
        Utils.printDebug("Attempting to execute a ${type.identifier} Action: $this")
        if (SkiesGUIs.INSTANCE.server?.commands == null) {
            Utils.printError("There was an error while executing an action for player ${player.name}: Server was somehow null on command execution?")
            return
        }

        for (command in commands) {
            SkiesGUIs.INSTANCE.server?.commands?.performPrefixedCommand(
                player.createCommandSourceStack(),
                Utils.parsePlaceholders(player, command)
            )
        }
    }

    override fun toString(): String {
        return "CommandPlayer(type=$type, click=$click, requirements=$requirements, commands=$commands)"
    }
}
