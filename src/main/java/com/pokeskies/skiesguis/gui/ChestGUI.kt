package com.pokeskies.skiesguis.gui

import ca.landonjw.gooeylibs2.api.data.UpdateEmitter
import ca.landonjw.gooeylibs2.api.page.Page
import ca.landonjw.gooeylibs2.api.page.PageAction
import ca.landonjw.gooeylibs2.api.template.Template
import ca.landonjw.gooeylibs2.api.template.types.ChestTemplate
import com.pokeskies.skiesguis.SkiesGUIs
import com.pokeskies.skiesguis.config.GuiConfig
import com.pokeskies.skiesguis.config.GuiItem
import com.pokeskies.skiesguis.utils.Utils
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import java.util.*

class ChestGUI(
    private val player: ServerPlayerEntity,
    private val config: GuiConfig
) : UpdateEmitter<Page?>(), Page {
    private val template: ChestTemplate =
        ChestTemplate.Builder(config.size)
            .build()

    // This is a MAP (key=SLOT INDEX, value='MAP(key=PRIORITY, value=GUI ITEM ENTRY)')
    private val items: TreeMap<Int, TreeMap<Int, Map.Entry<String, GuiItem>>> = TreeMap()

    init {
        val controller = InventoryController()
        controller.subscribe(this, Runnable { refresh() })
        SkiesGUIs.INSTANCE.inventoryControllers[player.uuid] = controller
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
            for ((_, itemEntry) in slotEntry) {
                val guiItem = itemEntry.value
                if (guiItem.checkViewRequirements(player)) {
                    guiItem.executeSuccessActions(player)
                    template.set(slot, guiItem.createButton(player)
                        .onClick { ctx ->
                            for (actionEntry in guiItem.clickActions) {
                                val action = actionEntry.value
                                if (action.matchesClick(ctx.clickType)) {
                                    if (action.checkRequirements(player)) {
                                        action.executeSuccessActions(player)
                                        action.attemptExecution(player)
                                    } else {
                                        action.executeDenyActions(player)
                                    }
                                }
                            }
                        }
                        .build())

                    // Since the slot is being filled at the highest priority, all remaining entries are lower priority
                    break
                } else {
                    guiItem.executeDenyActions(player)
                }
            }
        }
    }

    override fun onClose(action: PageAction) {
        config.executeCloseActions(player)
        SkiesGUIs.INSTANCE.inventoryControllers.remove(player.uuid)
    }

    override fun getTemplate(): Template {
        return template
    }

    override fun getTitle(): Text {
        return Utils.deserializeText(Utils.parsePlaceholders(player, config.title))
    }

    class InventoryController: UpdateEmitter<ChestGUI?>()
}