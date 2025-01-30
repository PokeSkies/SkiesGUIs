package com.pokeskies.skiesguis.api

import com.pokeskies.skiesguis.config.ConfigManager
import com.pokeskies.skiesguis.config.GuiConfig
import net.minecraft.server.level.ServerPlayer

object SkiesGUIsAPI {
    fun getGUIConfig(id: String): GuiConfig? {
        return ConfigManager.GUIS[id]
    }

    fun attemptGUIOpen(player: ServerPlayer, id: String) {
        getGUIConfig(id)?.openGUI(player, id)
    }
}
