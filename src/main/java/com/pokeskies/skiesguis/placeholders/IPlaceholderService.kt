package com.pokeskies.skiesguis.placeholders

import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text

interface IPlaceholderService {
    fun parsePlaceholders(player: ServerPlayerEntity, text: String): String
}