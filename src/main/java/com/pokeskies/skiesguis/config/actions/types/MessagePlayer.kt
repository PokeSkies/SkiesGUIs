package com.pokeskies.skiesguis.config.actions.types

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import com.pokeskies.skiesguis.config.actions.Action
import com.pokeskies.skiesguis.config.actions.ActionType
import com.pokeskies.skiesguis.config.actions.ClickType
import com.pokeskies.skiesguis.config.requirements.ClickRequirement
import com.pokeskies.skiesguis.config.requirements.Requirement
import com.pokeskies.skiesguis.utils.Utils
import com.pokeskies.skiesguis.utils.recordCodec
import net.minecraft.server.network.ServerPlayerEntity
import java.util.*

class MessagePlayer(
    click: ClickType,
    clickRequirements: Optional<ClickRequirement>,
    private val message: List<String>
) : Action(click, clickRequirements) {
    companion object {
        val CODEC: Codec<MessagePlayer> = RecordCodecBuilder.create {
            actionCodec(it).and(
                Codec.STRING.listOf().recordCodec("message", MessagePlayer::message)
            ).apply(it, ::MessagePlayer)
        }
    }

    override fun execute(player: ServerPlayerEntity) {
        for (line in message) {
            player.sendMessage(Utils.deseralizeText(parsePlaceholders(player, line)))
        }
    }

    override fun getType(): ActionType<*> {
        return ActionType.MESSAGE
    }
}