package com.pokeskies.skiesguis.config

import com.pokeskies.skiesguis.economy.EconomyType

class MainConfig(
    var debug: Boolean = false,
    var economy: EconomyType = EconomyType.IMPACTOR
) {
    override fun toString(): String {
        return "MainConfig(debug=$debug, economy=$economy)"
    }
}