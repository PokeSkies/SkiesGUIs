package com.pokeskies.skiesguis.config.actions.types

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import com.pokeskies.skiesguis.SkiesGUIs
import com.pokeskies.skiesguis.config.actions.Action
import com.pokeskies.skiesguis.config.actions.ActionType
import com.pokeskies.skiesguis.config.actions.ClickType
import com.pokeskies.skiesguis.config.requirements.ClickRequirement
import com.pokeskies.skiesguis.utils.Utils
import com.pokeskies.skiesguis.utils.recordCodec
import net.minecraft.server.network.ServerPlayerEntity
import java.util.*

class CommandConsole(
    click: ClickType,
    clickRequirements: Optional<ClickRequirement>,
    private val commands: List<String>
) : Action(click, clickRequirements) {
    companion object {
        val CODEC: Codec<CommandConsole> = RecordCodecBuilder.create {
            actionCodec(it).and(
                Codec.STRING.listOf().recordCodec("commands", CommandConsole::commands)
            ).apply(it, ::CommandConsole)
        }
    }

    override fun execute(player: ServerPlayerEntity) {
        Utils.debug("Attempting to execute a ${getType().id} Action: $this")
        if (SkiesGUIs.INSTANCE.server?.commandManager == null) {
            SkiesGUIs.LOGGER.error("There was an error while executing an action for player ${player.name}: Server was somehow null on command execution?")
            return
        }

        for (command in commands) {
            SkiesGUIs.INSTANCE.server!!.commandManager.executeWithPrefix(
                SkiesGUIs.INSTANCE.server!!.commandSource,
                parsePlaceholders(player, command)
            )
        }
    }

    override fun getType(): ActionType<*> {
        return ActionType.COMMAND_CONSOLE
    }

    override fun toString(): String {
        return "CommandConsole(commands=$commands)"
    }
}