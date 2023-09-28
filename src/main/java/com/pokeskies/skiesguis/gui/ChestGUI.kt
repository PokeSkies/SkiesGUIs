package com.pokeskies.skiesguis.gui

import ca.landonjw.gooeylibs2.api.data.UpdateEmitter
import ca.landonjw.gooeylibs2.api.page.Page
import ca.landonjw.gooeylibs2.api.template.Template
import ca.landonjw.gooeylibs2.api.template.types.ChestTemplate
import com.pokeskies.skiesguis.config.GuiConfig
import com.pokeskies.skiesguis.utils.Utils
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text

class ChestGUI(
    private val player: ServerPlayerEntity,
    private val guiId: String,
    private val config: GuiConfig
) : UpdateEmitter<Page?>(), Page {
    private val template: ChestTemplate =
        ChestTemplate.Builder(config.size)
            .build()

    init {
        refresh()
    }

    private fun refresh() {
        for ((itemId, itemConfig) in config.items) {
            val button = itemConfig.createButton()
                .onClick { ctx ->
                    for (action in itemConfig.actions) {
                        if (action.value.matchesClick(ctx.clickType)) {
                            action.value.execute(player)
                        }
                    }
                }
                .build()
            for (slot in itemConfig.slots) {
                template.set(slot, button)
            }
        }
    }

    override fun getTemplate(): Template {
        return template
    }

    override fun getTitle(): Text {
        return Utils.deseralizeText(config.title)
    }
}