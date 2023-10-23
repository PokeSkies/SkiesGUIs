package com.pokeskies.skiesguis.placeholders

import net.minecraft.server.network.ServerPlayerEntity

interface IPlaceholderService {
    fun parsePlaceholders(player: ServerPlayerEntity, text: String): String
}