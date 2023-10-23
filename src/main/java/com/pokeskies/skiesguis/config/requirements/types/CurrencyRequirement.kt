package com.pokeskies.skiesguis.config.requirements.types

import com.pokeskies.skiesguis.SkiesGUIs
import com.pokeskies.skiesguis.config.requirements.ComparisonType
import com.pokeskies.skiesguis.config.requirements.Requirement
import com.pokeskies.skiesguis.config.requirements.RequirementType
import com.pokeskies.skiesguis.utils.Utils
import net.minecraft.server.network.ServerPlayerEntity

class CurrencyRequirement(
    type: RequirementType = RequirementType.CURRENCY,
    comparison: ComparisonType = ComparisonType.EQUALS,
    private val currency: String = "",
    private val amount: Double = 0.0
) : Requirement(type, comparison) {
    override fun checkRequirements(player: ServerPlayerEntity): Boolean {
        if (!checkComparison())
            return false

        val service = SkiesGUIs.INSTANCE.economyService
        if (service == null) {
            Utils.printError("Currency Requirement was checked but no valid Economy Service could be found.")
            return false
        }

        val balance = service.balance(player, currency)

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
        return ComparisonType.values().toList()
    }

    override fun toString(): String {
        return "CurrencyRequirement(currency='$currency', amount=$amount)"
    }
}