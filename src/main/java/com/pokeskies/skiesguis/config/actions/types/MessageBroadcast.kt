package com.pokeskies.skiesguis.config.actions.types

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import com.pokeskies.skiesguis.SkiesGUIs
import com.pokeskies.skiesguis.config.actions.Action
import com.pokeskies.skiesguis.config.actions.ActionType
import com.pokeskies.skiesguis.config.actions.ClickType
import com.pokeskies.skiesguis.utils.Utils
import com.pokeskies.skiesguis.utils.recordCodec
import net.minecraft.server.network.ServerPlayerEntity

class MessageBroadcast(
    click: ClickType,
    private val message: List<String>
) : Action(click) {
    companion object {
        val CODEC: Codec<MessageBroadcast> = RecordCodecBuilder.create {
            actionCodec(it).and(
                Codec.STRING.listOf().recordCodec("message", MessageBroadcast::message)
            ).apply(it, ::MessageBroadcast)
        }
    }

    override fun execute(player: ServerPlayerEntity) {
        if (SkiesGUIs.INSTANCE.adventure == null) {
            SkiesGUIs.LOGGER.error("There was an error while executing an action for player ${player.name}: Adventure was somehow null on message broadcast?")
            return
        }

        for (line in message) {
            SkiesGUIs.INSTANCE.adventure!!.all().sendMessage(Utils.deseralizeText(parsePlaceholders(player, line)))
        }
    }

    override fun getType(): ActionType<*> {
        return ActionType.BROADCAST
    }
}