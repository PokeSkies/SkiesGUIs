package com.pokeskies.skiesguis.placeholders.services

import com.pokeskies.skiesguis.placeholders.IPlaceholderService
import com.pokeskies.skiesguis.utils.Utils
import eu.pb4.placeholders.api.PlaceholderContext
import eu.pb4.placeholders.api.Placeholders
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text

class PlaceholderAPIService : IPlaceholderService {
    init {
        Utils.printInfo("PlaceholderAPI mod found! Enabling placeholder integration...")
    }
    override fun parsePlaceholders(player: ServerPlayerEntity, text: String): String {
        return Placeholders.parseText(Text.of(text), PlaceholderContext.of(player)).string
    }
}