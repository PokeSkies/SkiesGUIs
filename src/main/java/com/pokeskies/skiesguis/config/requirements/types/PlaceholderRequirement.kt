package com.pokeskies.skiesguis.config.requirements.types

import com.pokeskies.skiesguis.config.requirements.ComparisonType
import com.pokeskies.skiesguis.config.requirements.Requirement
import com.pokeskies.skiesguis.config.requirements.RequirementType
import com.pokeskies.skiesguis.utils.Utils
import me.lucko.fabric.api.permissions.v0.Permissions
import net.minecraft.server.network.ServerPlayerEntity

class PlaceholderRequirement(
    type: RequirementType = RequirementType.PERMISSION,
    comparison: ComparisonType = ComparisonType.EQUALS,
    private val input: String = "",
    private val output: String = "",
    private val strict: Boolean = false
) : Requirement(type, comparison) {
    override fun checkRequirements(player: ServerPlayerEntity): Boolean {
        if (!checkComparison())
            return false

        val result = Utils.parsePlaceholders(player, input).equals(output, strict)

        println("The result of ${Utils.parsePlaceholders(player, input)} == $output is $result")

        return if (comparison == ComparisonType.EQUALS) result else !result
    }

    override fun allowedComparisons(): List<ComparisonType> {
        return listOf(ComparisonType.EQUALS, ComparisonType.NOT_EQUALS)
    }

    override fun toString(): String {
        return "PlaceholderRequirement(input='$input', output='$output', strict=$strict)"
    }

}