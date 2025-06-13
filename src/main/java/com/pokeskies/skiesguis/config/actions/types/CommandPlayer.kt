package com.pokeskies.skiesguis.config.actions.types

import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
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
    @JsonAdapter(FlexibleListAdaptorFactory::class) @SerializedName("commands",  alternate = ["command"])
    private val commands: List<String> = emptyList(),
    @SerializedName("permission_level")
    private val permissionLevel: Int? = null
) : Action(type, click, delay, chance, requirements) {
    override fun executeAction(player: ServerPlayer) {
        val parsedCommands = commands.map { Utils.parsePlaceholders(player, it) }

        var source = player.createCommandSourceStack()
        if (permissionLevel != null) {
            source = source.withPermission(permissionLevel)
        }

        Utils.printDebug("[ACTION - ${type.name}] Player(${player.gameProfile.name}), Parsed Commands($parsedCommands): $this")

        for (command in parsedCommands) {
            SkiesGUIs.INSTANCE.server.commands.performPrefixedCommand(
                source,
                command
            )
        }
    }

    override fun toString(): String {
        return "CommandPlayer(click=$click, delay=$delay, chance=$chance, requirements=$requirements, " +
                "commands=$commands)"
    }
}
