package com.pokeskies.skiesguis.config.requirements.types.internal

import com.pokeskies.skiesguis.config.requirements.ComparisonType
import com.pokeskies.skiesguis.config.requirements.Requirement
import com.pokeskies.skiesguis.config.requirements.RequirementType
import com.pokeskies.skiesguis.gui.ChestGUI
import com.pokeskies.skiesguis.utils.Utils
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer

class AdvancementRequirement(
    type: RequirementType = RequirementType.ADVANCEMENT,
    comparison: ComparisonType = ComparisonType.GREATER_THAN_OR_EQUALS,
    private val advancement: String = "",
    private val progress: Float = 1.0F
) : Requirement(type, comparison) {
    override fun checkRequirements(player: ServerPlayer, gui: ChestGUI): Boolean {
        if (!checkComparison()) return false

        if (advancement.isEmpty()) {
            Utils.printError("[REQUIREMENT - ${type?.name}] Advancement ID is empty.")
            return false
        }

        val advHolder = player.server.advancements.get(ResourceLocation.parse(advancement))
        if (advHolder == null) {
            Utils.printError("[REQUIREMENT - ${type?.name}] Advancement with ID '$advancement' does not exist.")
            return false
        }

        val advProgress = player.advancements.progress[advHolder]
        if (advProgress == null) {
            Utils.printError("[REQUIREMENT - ${type?.name}] Could not retrieve progress for advancement '$advancement' for player '${player.gameProfile.name}'.")
            return false
        }

        Utils.printDebug("[REQUIREMENT - ${type?.name}] Player(${player.gameProfile.name}), Advancement($advHolder), Progress(${advProgress.percent}): $this")

        return when (comparison) {
            ComparisonType.EQUALS -> advProgress.percent == progress
            ComparisonType.NOT_EQUALS -> advProgress.percent != progress
            ComparisonType.GREATER_THAN -> advProgress.percent > progress
            ComparisonType.LESS_THAN -> advProgress.percent < progress
            ComparisonType.GREATER_THAN_OR_EQUALS -> advProgress.percent >= progress
            ComparisonType.LESS_THAN_OR_EQUALS -> advProgress.percent <= progress
        }
    }

    override fun allowedComparisons(): List<ComparisonType> {
        return ComparisonType.entries
    }

    override fun toString(): String {
        return "AdvancementRequirement(comparison=$comparison, advancement='$advancement', progress=$progress)"
    }

}
