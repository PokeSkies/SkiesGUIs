package com.pokeskies.skiesguis.config.requirements.types.internal

import com.google.gson.annotations.JsonAdapter
import com.pokeskies.skiesguis.config.requirements.ComparisonType
import com.pokeskies.skiesguis.config.requirements.Requirement
import com.pokeskies.skiesguis.config.requirements.RequirementType
import com.pokeskies.skiesguis.utils.FlexibleListAdaptorFactory
import com.pokeskies.skiesguis.utils.Utils
import net.minecraft.server.level.ServerPlayer

class PlaceholderRequirement(
    type: RequirementType = RequirementType.PLACEHOLDER,
    comparison: ComparisonType = ComparisonType.EQUALS,
    private val input: String = "",
    @JsonAdapter(FlexibleListAdaptorFactory::class)
    private val output: List<String> = emptyList(),
    private val strict: Boolean = false
) : Requirement(type, comparison) {
    override fun checkRequirements(player: ServerPlayer): Boolean {
        if (!checkComparison()) return false

        val parsed = Utils.parsePlaceholders(player, input)

        val result = output.any { it.equals(parsed, strict) }

        Utils.printDebug("[REQUIREMENT - ${type?.name}] Player(${player.gameProfile.name}), Parsed Input($parsed), Output Check($result): $this")

        return if (comparison == ComparisonType.EQUALS) result else !result
    }

    override fun allowedComparisons(): List<ComparisonType> {
        return listOf(ComparisonType.EQUALS, ComparisonType.NOT_EQUALS)
    }

    override fun toString(): String {
        return "PlaceholderRequirement(comparison=$comparison, input='$input', output='$output', strict=$strict)"
    }

}
