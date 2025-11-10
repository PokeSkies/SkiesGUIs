package com.pokeskies.skiesguis.config.requirements.types.internal

import com.pokeskies.skiesguis.config.requirements.ComparisonType
import com.pokeskies.skiesguis.config.requirements.Requirement
import com.pokeskies.skiesguis.config.requirements.RequirementType
import com.pokeskies.skiesguis.gui.ChestGUI
import com.pokeskies.skiesguis.utils.Utils
import net.minecraft.server.level.ServerPlayer

class XPRequirement(
    type: RequirementType = RequirementType.XP,
    comparison: ComparisonType = ComparisonType.GREATER_THAN_OR_EQUALS,
    private val level: Boolean = true,
    private val amount: Int = 0,
) : Requirement(type, comparison) {
    override fun checkRequirements(player: ServerPlayer, gui: ChestGUI): Boolean {
        if (!checkComparison()) return false

        val experience = if (level) player.experienceLevel else player.totalExperience

        Utils.printDebug("[REQUIREMENT - ${type?.name}] Player(${player.gameProfile.name}), Player Experience($experience): $this")

        return when (comparison) {
            ComparisonType.EQUALS -> experience == amount
            ComparisonType.NOT_EQUALS -> experience != amount
            ComparisonType.GREATER_THAN -> experience > amount
            ComparisonType.LESS_THAN -> experience < amount
            ComparisonType.GREATER_THAN_OR_EQUALS -> experience >= amount
            ComparisonType.LESS_THAN_OR_EQUALS -> experience <= amount
        }
    }

    override fun allowedComparisons(): List<ComparisonType> {
        return ComparisonType.entries
    }

    override fun toString(): String {
        return "XPRequirement(level=$level, amount=$amount)"
    }
}
