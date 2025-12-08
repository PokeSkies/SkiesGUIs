package com.pokeskies.skiesguis.config.actions.types

import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import com.pokeskies.skiesguis.SkiesGUIs
import com.pokeskies.skiesguis.config.actions.Action
import com.pokeskies.skiesguis.config.actions.ActionType
import com.pokeskies.skiesguis.config.actions.GenericClickType
import com.pokeskies.skiesguis.config.requirements.RequirementOptions
import com.pokeskies.skiesguis.gui.ChestGUI
import com.pokeskies.skiesguis.utils.FlexibleListAdaptorFactory
import com.pokeskies.skiesguis.utils.Utils
import net.minecraft.server.level.ServerPlayer

class CommandConsole(
    type: ActionType = ActionType.COMMAND_CONSOLE,
    click: List<GenericClickType> = listOf(GenericClickType.ANY),
    delay: Long = 0,
    chance: Double = 0.0,
    requirements: RequirementOptions? = RequirementOptions(),
    @JsonAdapter(FlexibleListAdaptorFactory::class) @SerializedName("commands",  alternate = ["command"])
    private val commands: List<String> = emptyList()
) : Action(type, click, delay, chance, requirements) {
    override fun executeAction(player: ServerPlayer, gui: ChestGUI) {
        val parsedCommands = commands.map { Utils.parsePlaceholders(player, it) }

        Utils.printDebug("[ACTION - ${type.name}] Player(${player.gameProfile.name}), Parsed Commands($parsedCommands): $this")

        for (command in parsedCommands) {
            SkiesGUIs.INSTANCE.server.commands.performPrefixedCommand(
                SkiesGUIs.INSTANCE.server.createCommandSourceStack(),
                command
            )
        }
    }

    override fun toString(): String {
        return "CommandConsole(click=$click, delay=$delay, chance=$chance, requirements=$requirements, " +
                "commands=$commands)"
    }
}
