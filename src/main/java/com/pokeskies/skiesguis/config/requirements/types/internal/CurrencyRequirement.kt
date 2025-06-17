package com.pokeskies.skiesguis.config.requirements.types.internal

import com.pokeskies.skiesguis.SkiesGUIs
import com.pokeskies.skiesguis.config.requirements.ComparisonType
import com.pokeskies.skiesguis.config.requirements.Requirement
import com.pokeskies.skiesguis.config.requirements.RequirementType
import com.pokeskies.skiesguis.economy.EconomyType
import com.pokeskies.skiesguis.utils.Utils
import net.minecraft.server.level.ServerPlayer

class CurrencyRequirement(
    type: RequirementType = RequirementType.CURRENCY,
    comparison: ComparisonType = ComparisonType.GREATER_THAN_OR_EQUALS,
    private val currency: String = "",
    private val amount: Double = 0.0,
    private val economy: EconomyType? = null
) : Requirement(type, comparison) {
    override fun checkRequirements(player: ServerPlayer): Boolean {
        if (!checkComparison()) return false

        val service = SkiesGUIs.INSTANCE.getEconomyServiceOrDefault(economy)
        if (service == null) {
            Utils.printError("[REQUIREMENT - ${type?.name}] No Economy Service could be found from '$economy'! Valid services are: ${SkiesGUIs.INSTANCE.getLoadedEconomyServices().keys}")
            return false
        }

        val balance = service.balance(player, currency)

        Utils.printDebug("[REQUIREMENT - ${type?.name}] Player(${player.gameProfile.name}), Player Balance($balance): $this")

        return when (comparison) {
            ComparisonType.EQUALS -> balance == amount
            ComparisonType.NOT_EQUALS -> balance != amount
            ComparisonType.GREATER_THAN -> balance > amount
            ComparisonType.LESS_THAN -> balance < amount
            ComparisonType.GREATER_THAN_OR_EQUALS -> balance >= amount
            ComparisonType.LESS_THAN_OR_EQUALS -> balance <= amount
        }
    }

    override fun allowedComparisons(): List<ComparisonType> {
        return ComparisonType.entries
    }

    override fun toString(): String {
        return "CurrencyRequirement(comparison=$comparison, currency='$currency', amount=$amount, economy=$economy)"
    }
}
