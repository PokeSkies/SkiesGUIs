package com.pokeskies.skiesguis.economy.services

import com.pokeskies.skiesguis.economy.IEconomyService
import com.pokeskies.skiesguis.utils.Utils
import net.minecraft.server.network.ServerPlayerEntity
import tech.sethi.pebbleseconomy.PebblesEconomyInitializer

class PebblesEconomyService : IEconomyService {
    init {
        Utils.info("Pebbles Economy Service has been found and loaded for any Currency actions/requirements!")
    }

    override fun balance(player: ServerPlayerEntity, currency: String) : Double {
        return PebblesEconomyInitializer.economy.getBalance(player.uuid)
    }

    override fun withdraw(player: ServerPlayerEntity, amount: Double, currency: String) : Boolean {
        return PebblesEconomyInitializer.economy.withdraw(player.uuid, amount)
    }

    override fun deposit(player: ServerPlayerEntity, amount: Double, currency: String) : Boolean {
        PebblesEconomyInitializer.economy.deposit(player.uuid, amount)
        return true
    }

    override fun set(player: ServerPlayerEntity, amount: Double, currency: String) : Boolean {
        PebblesEconomyInitializer.economy.setBalance(player.uuid, amount)
        return true
    }
}