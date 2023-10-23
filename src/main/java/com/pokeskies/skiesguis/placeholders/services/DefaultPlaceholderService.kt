package com.pokeskies.skiesguis.placeholders.services

import com.pokeskies.skiesguis.placeholders.IPlaceholderService
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text

class DefaultPlaceholderService : IPlaceholderService {
    override fun parsePlaceholders(player: ServerPlayerEntity, text: String): String {
        return text.replace("%player%", player.name.string)
    }
}