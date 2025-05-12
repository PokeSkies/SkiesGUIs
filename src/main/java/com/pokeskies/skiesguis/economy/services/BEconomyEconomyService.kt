package com.pokeskies.skiesguis.economy.services

import com.pokeskies.skiesguis.economy.IEconomyService
import com.pokeskies.skiesguis.utils.Utils
import net.minecraft.server.level.ServerPlayer
import org.beconomy.api.BEconomy
import java.math.BigDecimal

class BEconomyEconomyService : IEconomyService {
    init {
        Utils.printInfo("BEconomy has been found and loaded for any Currency actions/requirements!")
    }

    override fun balance(player: ServerPlayer, currency: String) : Double {
        return BEconomy.getAPI().getBalance(player.uuid, currency).toDouble()
    }

    override fun withdraw(player: ServerPlayer, amount: Double, currency: String) : Boolean {
        return BEconomy.getAPI().subtractBalance(player.uuid, BigDecimal.valueOf(amount), currency)
    }

    override fun deposit(player: ServerPlayer, amount: Double, currency: String) : Boolean {
        BEconomy.getAPI().addBalance(player.uuid, BigDecimal.valueOf(amount), currency)
        return true
    }

    override fun set(player: ServerPlayer, amount: Double, currency: String) : Boolean {
        BEconomy.getAPI().setBalance(player.uuid, BigDecimal.valueOf(amount), currency)
        return true
    }
}
