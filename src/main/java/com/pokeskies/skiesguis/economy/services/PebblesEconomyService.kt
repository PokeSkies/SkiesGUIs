package com.pokeskies.skiesguis.economy.services

import com.pokeskies.skiesguis.economy.IEconomyService
import com.pokeskies.skiesguis.utils.Utils
import net.minecraft.server.level.ServerPlayer
import tech.sethi.pebbleseconomy.PebblesEconomyInitializer

class PebblesEconomyService : IEconomyService {
    init {
        Utils.printInfo("Pebbles Economy Service has been found and loaded for any Currency actions/requirements!")
    }

    override fun balance(player: ServerPlayer, currency: String) : Double {
        return PebblesEconomyInitializer.economy.getBalance(player.uuid)
    }

    override fun withdraw(player: ServerPlayer, amount: Double, currency: String) : Boolean {
        return PebblesEconomyInitializer.economy.withdraw(player.uuid, amount)
    }

    override fun deposit(player: ServerPlayer, amount: Double, currency: String) : Boolean {
        PebblesEconomyInitializer.economy.deposit(player.uuid, amount)
        return true
    }

    override fun set(player: ServerPlayer, amount: Double, currency: String) : Boolean {
        PebblesEconomyInitializer.economy.setBalance(player.uuid, amount)
        return true
    }
}
