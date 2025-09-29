package com.pokeskies.skiesguis.config.requirements.types.internal

import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import com.pokeskies.skiesguis.config.requirements.ComparisonType
import com.pokeskies.skiesguis.config.requirements.PermissionMode
import com.pokeskies.skiesguis.config.requirements.Requirement
import com.pokeskies.skiesguis.config.requirements.RequirementType
import com.pokeskies.skiesguis.utils.FlexibleListAdaptorFactory
import com.pokeskies.skiesguis.utils.Utils
import net.minecraft.server.level.ServerPlayer

class PermissionRequirement(
    type: RequirementType = RequirementType.PERMISSION,
    comparison: ComparisonType = ComparisonType.EQUALS,
    @JsonAdapter(FlexibleListAdaptorFactory::class) @SerializedName("permissions",  alternate = ["permission"])
    private val permissions: List<String> = listOf(),
    private val mode: PermissionMode = PermissionMode.ALL,
) : Requirement(type, comparison) {
    override fun checkRequirements(player: ServerPlayer): Boolean {
        if (!checkComparison()) return false

        if (permissions.isEmpty()) {
            Utils.printError("[REQUIREMENT - ${type?.name}] Permission field was empty? Why?: $this")
            return false
        }

        val value = mode.check(permissions, player)

        Utils.printDebug("[REQUIREMENT - ${type?.name}] Player(${player.gameProfile.name}), Permission Check($value): $this")

        return if (comparison == ComparisonType.NOT_EQUALS) !value else value
    }

    override fun allowedComparisons(): List<ComparisonType> {
        return listOf(ComparisonType.EQUALS, ComparisonType.NOT_EQUALS)
    }

    override fun toString(): String {
        return "PermissionRequirement(comparison=$comparison, permission=$permissions, mode=$mode)"
    }
}
