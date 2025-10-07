package com.pokeskies.skiesguis.config.requirements

import com.pokeskies.skiesguis.gui.ChestGUI
import com.pokeskies.skiesguis.utils.Utils
import net.minecraft.server.level.ServerPlayer

abstract class Requirement(
    val type: RequirementType? = null,
    val comparison: ComparisonType = ComparisonType.EQUALS
) {
    abstract fun checkRequirements(player: ServerPlayer, gui: ChestGUI): Boolean

    open fun allowedComparisons(): List<ComparisonType> {
        return emptyList()
    }

    fun checkComparison(): Boolean {
        if (!allowedComparisons().contains(comparison)) {
            Utils.printError("Error while executing a $type Requirement check! Comparison ${comparison.identifier} is not allowed: ${allowedComparisons().map { it.identifier }}")
            return false
        }
        return true
    }

    override fun toString(): String {
        return "Requirement(type=$type, comparison=$comparison)"
    }
}
