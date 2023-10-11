package com.pokeskies.skiesguis.config.actions.types

import com.pokeskies.skiesguis.SkiesGUIs
import com.pokeskies.skiesguis.config.actions.Action
import com.pokeskies.skiesguis.config.actions.ActionType
import com.pokeskies.skiesguis.config.actions.ClickType
import com.pokeskies.skiesguis.config.requirements.RequirementOptions
import com.pokeskies.skiesguis.utils.Utils
import net.minecraft.server.network.ServerPlayerEntity

class CurrencyDeposit(
    type: ActionType = ActionType.CURRENCY_DEPOSIT,
    click: ClickType = ClickType.ANY,
    delay: Long = 0,
    chance: Double = 0.0,
    requirements: RequirementOptions? = RequirementOptions(),
    private val currency: String = "",
    private val amount: Double = 0.0
) : Action(type, click, delay, chance, requirements) {
    override fun execute(player: ServerPlayerEntity) {
        Utils.debug("Attempting to execute a ${type.identifier} Action: $this")

        val service = SkiesGUIs.INSTANCE.economyService
        if (service == null) {
            Utils.error("Currency Deposit Action was executed but no valid Economy Service could be found.")
            return
        }

        service.deposit(player, amount, currency)
    }

    override fun toString(): String {
        return "CurrencyDeposit(type=$type, click=$click, requirements=$requirements, currency=$currency, amount=$amount)"
    }
}