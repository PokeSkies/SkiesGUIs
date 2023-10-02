package com.pokeskies.skiesguis.config.actions.types

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import com.pokeskies.skiesguis.config.actions.Action
import com.pokeskies.skiesguis.config.actions.ActionType
import com.pokeskies.skiesguis.config.actions.ClickType
import com.pokeskies.skiesguis.utils.optionalRecordCodec
import com.pokeskies.skiesguis.utils.recordCodec
import net.minecraft.server.network.ServerPlayerEntity
import java.util.*

class GiveXP(
    click: ClickType,
    private val amount: Int,
    private val level: Boolean
) : Action(click) {
    companion object {
        val CODEC: Codec<GiveXP> = RecordCodecBuilder.create {
            actionCodec(it).and(
                it.group(
                    Codec.INT.recordCodec("amount", GiveXP::amount),
                    Codec.BOOL.optionalRecordCodec("level", GiveXP::level, false),
                )
            ).apply(it, ::GiveXP)
        }
    }

    override fun execute(player: ServerPlayerEntity) {
        if (level) {
            player.addExperienceLevels(amount)
        } else {
            player.addExperience(amount)
        }
    }

    override fun getType(): ActionType<*> {
        return ActionType.GIVE_XP
    }
}