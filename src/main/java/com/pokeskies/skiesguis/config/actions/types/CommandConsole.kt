package com.pokeskies.skiesguis.config.actions.types

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import com.pokeskies.skiesguis.SkiesGUIs
import com.pokeskies.skiesguis.config.actions.Action
import com.pokeskies.skiesguis.config.actions.ActionType
import com.pokeskies.skiesguis.config.actions.ClickType
import com.pokeskies.skiesguis.utils.recordCodec
import net.minecraft.server.network.ServerPlayerEntity

class CommandConsole(
    click: ClickType,
    private val commands: List<String>
) : Action(click) {
    companion object {
        val CODEC: Codec<CommandConsole> = RecordCodecBuilder.create {
            actionCodec(it).and(
                Codec.STRING.listOf().recordCodec("commands", CommandConsole::commands)
            ).apply(it, ::CommandConsole)
        }
    }

    override fun execute(player: ServerPlayerEntity) {
        for (command in commands) {
            SkiesGUIs.INSTANCE.server?.commandManager?.executeWithPrefix(
                SkiesGUIs.INSTANCE.server?.commandSource,
                parsePlaceholders(player, command)
            )
        }
    }

    override fun getType(): ActionType<*> {
        return ActionType.COMMAND_CONSOLE
    }
}