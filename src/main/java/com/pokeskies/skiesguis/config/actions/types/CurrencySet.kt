package com.pokeskies.skiesguis.config.actions.types

import com.pokeskies.skiesguis.SkiesGUIs
import com.pokeskies.skiesguis.config.actions.Action
import com.pokeskies.skiesguis.config.actions.ActionType
import com.pokeskies.skiesguis.config.actions.ClickType
import com.pokeskies.skiesguis.config.requirements.RequirementOptions
import com.pokeskies.skiesguis.economy.EconomyType
import com.pokeskies.skiesguis.utils.Utils
import net.minecraft.server.level.ServerPlayer

class CurrencySet(
    type: ActionType = ActionType.CURRENCY_SET,
    click: List<ClickType> = listOf(ClickType.ANY),
    delay: Long = 0,
    chance: Double = 0.0,
    requirements: RequirementOptions? = RequirementOptions(),
    private val currency: String = "",
    private val amount: Double = 0.0,
    private val economy: EconomyType? = null
) : Action(type, click, delay, chance, requirements) {
    override fun executeAction(player: ServerPlayer) {
        val service = SkiesGUIs.INSTANCE.getEconomyServiceOrDefault(economy)
        if (service == null) {
            Utils.printError("[ACTION - CURRENCY_SET] No Economy Service could be found from '$economy'! Valid services are: ${SkiesGUIs.INSTANCE.getLoadedEconomyServices().keys}")
            return
        }

        Utils.printDebug("[ACTION - ${type.name}] Player(${player.gameProfile.name}): $this")

        service.set(player, amount, currency)
    }

    override fun toString(): String {
        return "CurrencySet(click=$click, delay=$delay, chance=$chance, requirements=$requirements, " +
                "currency=$currency, amount=$amount)"
    }
}
