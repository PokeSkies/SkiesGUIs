package com.pokeskies.skiesguis.economy.services

import com.pokeskies.skiesguis.economy.IEconomyService
import com.pokeskies.skiesguis.utils.Utils
import fr.harmex.cobbledollars.common.utils.extensions.addCobbleDollars
import fr.harmex.cobbledollars.common.utils.extensions.getCobbleDollars
import fr.harmex.cobbledollars.common.utils.extensions.setCobbleDollars
import net.minecraft.server.level.ServerPlayer
import java.math.BigInteger

class CobbleDollarsEconomyService : IEconomyService {
    init {
        Utils.printInfo("CobbleDollars has been found and loaded for any Currency actions/requirements!")
    }

    override fun balance(player: ServerPlayer, currency: String) : Double {
        return player.getCobbleDollars().toDouble()
    }

    override fun withdraw(player: ServerPlayer, amount: Double, currency: String) : Boolean {
        player.addCobbleDollars(BigInteger.valueOf(-amount.toLong()))
        return true
    }

    override fun deposit(player: ServerPlayer, amount: Double, currency: String) : Boolean {
        player.addCobbleDollars(BigInteger.valueOf(amount.toLong()))
        return true
    }

    override fun set(player: ServerPlayer, amount: Double, currency: String) : Boolean {
        player.setCobbleDollars(BigInteger.valueOf(amount.toLong()))
        return true
    }
}
