package com.pokeskies.skiesguis.placeholders

import net.fabricmc.loader.api.FabricLoader

enum class PlaceholderMod(val modId: String) {
    IMPACTOR("impactor"),
    PLACEHOLDERAPI("placeholder-api"),
    MINIPLACEHOLDERS("miniplaceholders");

    fun isModPresent() : Boolean {
        return FabricLoader.getInstance().isModLoaded(modId)
    }
}