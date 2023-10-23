package com.pokeskies.skiesguis.config.requirements.types.extensions.plan

import com.pokeskies.skiesguis.config.requirements.ComparisonType
import com.pokeskies.skiesguis.config.requirements.Requirement
import com.pokeskies.skiesguis.config.requirements.RequirementType
import com.pokeskies.skiesguis.utils.PlanExtensionHelper
import com.pokeskies.skiesguis.utils.Utils
import net.minecraft.server.network.ServerPlayerEntity

class PlanPlaytimeRequirement(
    type: RequirementType = RequirementType.PERMISSION,
    comparison: ComparisonType = ComparisonType.EQUALS,
    private val time: Long = 0
) : Requirement(type, comparison) {
    override fun checkRequirements(player: ServerPlayerEntity): Boolean {
        if (!checkComparison())
            return false

        val playtime = PlanExtensionHelper.getPlaytime(player.uuid)

        Utils.printDebug("Checking a ${type?.identifier} Requirement with playtime='$playtime': $this")

        return when (comparison) {
            ComparisonType.EQUALS -> playtime == time
            ComparisonType.NOT_EQUALS -> playtime != time
            ComparisonType.GREATER_THAN -> playtime > time
            ComparisonType.LESS_THAN -> playtime < time
            ComparisonType.GREATER_THAN_OR_EQUALS -> playtime >= time
            ComparisonType.LESS_THAN_OR_EQUALS -> playtime <= time
        }
    }

    override fun allowedComparisons(): List<ComparisonType> {
        return ComparisonType.values().toList()
    }

    override fun toString(): String {
        return "PlanPlaytimeRequirement(comparison=$comparison, time=$time)"
    }

}