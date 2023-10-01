package com.pokeskies.skiesguis.config.requirements.types

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import com.pokeskies.skiesguis.config.requirements.Requirement
import com.pokeskies.skiesguis.config.requirements.RequirementType
import com.pokeskies.skiesguis.utils.optionalRecordCodec
import me.lucko.fabric.api.permissions.v0.Permissions
import net.minecraft.server.network.ServerPlayerEntity

class Permission(
    private val permission: String
) : Requirement() {
    companion object {
        val CODEC: Codec<Permission> = RecordCodecBuilder.create {
            it.group(
                Codec.STRING.optionalRecordCodec("permission", Permission::permission, "")
            ).apply(it, ::Permission)
        }
    }

    override fun check(player: ServerPlayerEntity): Boolean {
        if (permission.isNotEmpty()) {
            return Permissions.check(player, permission)
        }
        return true
    }

    override fun getType(): RequirementType<*> {
        return RequirementType.PERMISSION
    }
}