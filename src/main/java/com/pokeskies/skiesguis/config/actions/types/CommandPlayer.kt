package com.pokeskies.skiesguis.config.actions.types

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import com.pokeskies.skiesguis.SkiesGUIs
import com.pokeskies.skiesguis.config.actions.Action
import com.pokeskies.skiesguis.config.actions.ActionType
import com.pokeskies.skiesguis.config.actions.ClickType
import com.pokeskies.skiesguis.utils.recordCodec
import net.minecraft.server.network.ServerPlayerEntity

class CommandPlayer(
    click: ClickType,
    private val commands: List<String>
) : Action(click) {
    companion object {
        val CODEC: Codec<CommandPlayer> = RecordCodecBuilder.create {
            actionCodec(it).and(
                Codec.STRING.listOf().recordCodec("commands", CommandPlayer::commands)
            ).apply(it, ::CommandPlayer)
        }
    }

    override fun execute(player: ServerPlayerEntity) {
        if (SkiesGUIs.INSTANCE.server?.commandManager == null) {
            SkiesGUIs.LOGGER.error("There was an error while executing an action for player ${player.name}: Server was somehow null on command execution?")
            return
        }

        for (command in commands) {
            SkiesGUIs.INSTANCE.server?.commandManager?.executeWithPrefix(
                player.commandSource,
                parsePlaceholders(player, command)
            )
        }
    }

    override fun getType(): ActionType<*> {
        return ActionType.COMMAND_PLAYER
    }
}