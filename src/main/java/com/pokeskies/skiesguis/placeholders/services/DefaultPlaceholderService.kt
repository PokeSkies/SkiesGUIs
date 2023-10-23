package com.pokeskies.skiesguis.placeholders.services

import com.pokeskies.skiesguis.placeholders.IPlaceholderService
import net.minecraft.server.network.ServerPlayerEntity

class DefaultPlaceholderService : IPlaceholderService {
    override fun parsePlaceholders(player: ServerPlayerEntity, text: String): String {
        return text.replace("%player%", player.name.string)
    }
}