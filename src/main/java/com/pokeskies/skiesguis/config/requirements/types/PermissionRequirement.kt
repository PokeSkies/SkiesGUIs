package com.pokeskies.skiesguis.config.requirements.types

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import com.pokeskies.skiesguis.config.requirements.ComparisonType
import com.pokeskies.skiesguis.config.requirements.Requirement
import com.pokeskies.skiesguis.config.requirements.RequirementType
import com.pokeskies.skiesguis.utils.optionalRecordCodec
import me.lucko.fabric.api.permissions.v0.Permissions
import net.minecraft.server.network.ServerPlayerEntity

class PermissionRequirement(
    comparison: ComparisonType,
    private val permission: String
) : Requirement(comparison) {
    companion object {
        val CODEC: Codec<PermissionRequirement> = RecordCodecBuilder.create {
            requirementCodec(it).and(
                Codec.STRING.optionalRecordCodec("permission", PermissionRequirement::permission, "")
            ).apply(it, ::PermissionRequirement)
        }
    }

    override fun check(player: ServerPlayerEntity): Boolean {
        if (!checkComparison())
            return false

        if (permission.isNotEmpty()) {
            val value = Permissions.check(player, permission)
            return if (comparison == ComparisonType.NOT_EQUALS) !value else value
        }

        return true
    }

    override fun getType(): RequirementType<*> {
        return RequirementType.PERMISSION
    }

    override fun getAllowedComparisons(): List<ComparisonType> {
        return listOf(ComparisonType.EQUALS, ComparisonType.NOT_EQUALS)
    }
}