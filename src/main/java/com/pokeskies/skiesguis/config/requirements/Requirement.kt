package com.pokeskies.skiesguis.config.requirements

import com.mojang.serialization.Codec
import net.minecraft.server.network.ServerPlayerEntity

abstract class Requirement {
    companion object {
        val CODEC: Codec<Requirement> = RequirementType.CODEC.dispatch("type", { it.getType() }, { it.codec })
    }

    abstract fun check(player: ServerPlayerEntity): Boolean

    abstract fun getType(): RequirementType<*>

    fun parsePlaceholders(player: ServerPlayerEntity, value: String): String {
        return value.replace("%player%", player.name.string)
    }
}