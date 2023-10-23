package com.pokeskies.skiesguis.config.requirements

import com.pokeskies.skiesguis.SkiesGUIs
import com.pokeskies.skiesguis.utils.Utils
import net.minecraft.server.network.ServerPlayerEntity

abstract class Requirement(
    val type: RequirementType? = null,
    val comparison: ComparisonType = ComparisonType.EQUALS
) {
    abstract fun check(player: ServerPlayerEntity): Boolean

    open fun getAllowedComparisons(): List<ComparisonType> {
        return emptyList()
    }

    fun checkComparison(): Boolean {
        if (!getAllowedComparisons().contains(comparison)) {
            Utils.error("Error while executing a Requirement check! Comparison ${comparison.identifier} is not allowed: ${getAllowedComparisons().map { it.identifier }}")
            return false
        }
        return true
    }

    override fun toString(): String {
        return "Requirement(type=$type, comparison=$comparison)"
    }
}