package com.pokeskies.skiesguis.economy.services

import com.pokeskies.skiesguis.economy.IEconomyService
import fr.harmex.cobbledollars.common.utils.extensions.cobbleDollars
import net.minecraft.server.level.ServerPlayer
import java.math.BigInteger

class CobbleDollarsEconomyService : IEconomyService {
    override fun balance(player: ServerPlayer, currency: String) : Double {
        return player.cobbleDollars.toDouble()
    }

    override fun withdraw(player: ServerPlayer, amount: Double, currency: String) : Boolean {
        player.cobbleDollars = BigInteger.valueOf(balance(player, currency).toLong()) - BigInteger.valueOf(amount.toLong())
        return true
    }

    override fun deposit(player: ServerPlayer, amount: Double, currency: String) : Boolean {
        player.cobbleDollars = BigInteger.valueOf(balance(player, currency).toLong()) + BigInteger.valueOf(amount.toLong())
        return true
    }

    override fun set(player: ServerPlayer, amount: Double, currency: String) : Boolean {
        player.cobbleDollars = BigInteger.valueOf(amount.toLong())
        return true
    }
}
