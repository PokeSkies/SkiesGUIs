package com.pokeskies.skiesguis.config.actions.types

import com.pokeskies.skiesguis.SkiesGUIs
import com.pokeskies.skiesguis.config.actions.Action
import com.pokeskies.skiesguis.config.actions.ActionType
import com.pokeskies.skiesguis.config.actions.ClickType
import com.pokeskies.skiesguis.config.requirements.RequirementOptions
import com.pokeskies.skiesguis.utils.Utils
import net.minecraft.server.level.ServerPlayer

class CurrencySet(
    type: ActionType = ActionType.CURRENCY_SET,
    click: List<ClickType> = listOf(ClickType.ANY),
    delay: Long = 0,
    chance: Double = 0.0,
    requirements: RequirementOptions? = RequirementOptions(),
    private val currency: String = "",
    private val amount: Double = 0.0
) : Action(type, click, delay, chance, requirements) {
    override fun executeAction(player: ServerPlayer) {
        Utils.printDebug("Attempting to execute a ${type.identifier} Action: $this")

        val service = SkiesGUIs.INSTANCE.economyService
        if (service == null) {
            Utils.printError("Currency Set Action was executed but no valid Economy Service could be found.")
            return
        }

        service.set(player, amount, currency)
    }

    override fun toString(): String {
        return "CurrencySet(type=$type, click=$click, requirements=$requirements, currency=$currency, amount=$amount)"
    }
}
