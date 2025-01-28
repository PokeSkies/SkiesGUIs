package com.pokeskies.skiesguis.placeholders.services

import com.pokeskies.skiesguis.placeholders.IPlaceholderService
import net.minecraft.server.level.ServerPlayer

class DefaultPlaceholderService : IPlaceholderService {
    override fun parsePlaceholders(player: ServerPlayer, text: String): String {
        return text
            .replace("%player%", player.name.string)
            .replace("%player_uuid%", player.uuid.toString())
    }
}
