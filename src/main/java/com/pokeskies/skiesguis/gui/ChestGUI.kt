package com.pokeskies.skiesguis.gui

import ca.landonjw.gooeylibs2.api.data.UpdateEmitter
import ca.landonjw.gooeylibs2.api.page.Page
import ca.landonjw.gooeylibs2.api.template.Template
import ca.landonjw.gooeylibs2.api.template.types.ChestTemplate
import com.pokeskies.skiesguis.config.GuiConfig
import com.pokeskies.skiesguis.config.GuiItem
import com.pokeskies.skiesguis.utils.Utils
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import java.util.*

class ChestGUI(
    private val player: ServerPlayerEntity,
    private val guiId: String,
    private val config: GuiConfig
) : UpdateEmitter<Page?>(), Page {
    private val template: ChestTemplate =
        ChestTemplate.Builder(config.size)
            .build()

    private val items: TreeMap<Int, TreeMap<Int, Map.Entry<String, GuiItem>>> = TreeMap()

    init {
        for (entry in config.items) {
            for (slot in entry.value.slots) {
                val priorities = items.getOrDefault(slot, TreeMap())
                priorities[entry.value.priority] = entry
                items[slot] = priorities
            }
        }
        refresh()
    }

    private fun refresh() {
        for ((slot, slotEntry) in items) {
            for ((priority, itemEntry) in slotEntry) {
                if (itemEntry.value.checkViewRequirements(player)) {
                    template.set(slot, itemEntry.value.createButton()
                        .onClick { ctx ->
                            for (actionEntry in itemEntry.value.actions) {
                                val action = actionEntry.value
                                if (action.matchesClick(ctx.clickType)) {
                                    if (action.checkClickRequirements(player)) {
                                        action.execute(player)
                                    } else {
                                        action.executeDenyActions(player)
                                    }
                                }
                            }
                        }
                        .build())

                    // Since the slot is being filled at the highest priority, all remaining entries are lower priority
                    break
                }
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