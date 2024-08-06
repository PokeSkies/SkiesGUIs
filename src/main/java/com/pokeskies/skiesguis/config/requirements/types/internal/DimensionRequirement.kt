package com.pokeskies.skiesguis.config.requirements.types.internal

import com.pokeskies.skiesguis.config.requirements.ComparisonType
import com.pokeskies.skiesguis.config.requirements.Requirement
import com.pokeskies.skiesguis.config.requirements.RequirementType
import com.pokeskies.skiesguis.utils.Utils
import net.minecraft.server.level.ServerPlayer

class DimensionRequirement(
    type: RequirementType = RequirementType.DIMENSION,
    comparison: ComparisonType = ComparisonType.EQUALS,
    private val id: String = ""
) : Requirement(type, comparison) {
    override fun checkRequirements(player: ServerPlayer): Boolean {
        if (!checkComparison())
            return false

        Utils.printDebug("Checking a ${type?.identifier} Requirement with id='$id': $this")

        val value = id.equals(player.serverLevel().dimension().location().toString(), true)
        return if (comparison == ComparisonType.NOT_EQUALS) !value else value
    }

    override fun allowedComparisons(): List<ComparisonType> {
        return listOf(ComparisonType.EQUALS, ComparisonType.NOT_EQUALS)
    }

    override fun toString(): String {
        return "DimensionRequirement(comparison=$comparison, id='$id')"
    }
}
