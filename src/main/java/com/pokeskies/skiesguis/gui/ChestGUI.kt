package com.pokeskies.skiesguis.gui

import ca.landonjw.gooeylibs2.api.button.GooeyButton
import ca.landonjw.gooeylibs2.api.data.UpdateEmitter
import ca.landonjw.gooeylibs2.api.page.Page
import ca.landonjw.gooeylibs2.api.page.PageAction
import ca.landonjw.gooeylibs2.api.template.Template
import ca.landonjw.gooeylibs2.api.template.types.ChestTemplate
import ca.landonjw.gooeylibs2.api.template.types.InventoryTemplate
import com.pokeskies.skiesguis.SkiesGUIs
import com.pokeskies.skiesguis.config.GuiConfig
import com.pokeskies.skiesguis.config.GuiItem
import com.pokeskies.skiesguis.utils.Utils
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import java.util.*

class ChestGUI(
    private val player: ServerPlayer,
    private val guiId: String,
    private val config: GuiConfig
) : UpdateEmitter<Page?>(), Page {
    private val controller = InventoryController()
    private val template: ChestTemplate =
        ChestTemplate.Builder(config.size)
            .build()
    private val playerInventory: InventoryTemplate

    // This is a MAP (key=SLOT INDEX, value='MAP(key=PRIORITY, value=GUI ITEM ENTRY)')
    private val items: TreeMap<Int, TreeMap<Int, Map.Entry<String, GuiItem>>> = TreeMap()

    init {
        controller.subscribe(this, Runnable { refresh() })
        SkiesGUIs.INSTANCE.inventoryControllers[player.uuid] = controller
        for (entry in config.items) {
            for (slot in entry.value.slots) {
                val priorities = items.getOrDefault(slot, TreeMap())
                priorities[entry.value.priority] = entry
                items[slot] = priorities
            }
        }
        playerInventory = InventoryTemplate.builder().build();
        refresh()
    }

    private fun refresh() {
        Utils.printDebug("Executing refresh of GUI '$guiId' for player ${player.name.string}")
        update()
        // Just to keep the player's inventory up to date
        for ((i, stack) in player.inventory.items.withIndex()) {
            playerInventory.set(convertIndex(i), GooeyButton.builder().display(stack).build())
        }

        for ((slot, slotEntry) in items) {
            for ((_, itemEntry) in slotEntry) {
                val guiItem = itemEntry.value
                if (guiItem.viewRequirements?.checkRequirements(player) != false) {
                    guiItem.viewRequirements?.executeSuccessActions(player)
                    template.set(slot, guiItem.createButton(player)
                        .onClick { ctx ->
                            if (guiItem.clickRequirements?.checkRequirements(player) != false) {
                                guiItem.clickRequirements?.executeSuccessActions(player)
                                for (actionEntry in guiItem.clickActions) {
                                    val action = actionEntry.value
                                    if (action.matchesClick(ctx.clickType)) {
                                        if (action.requirements?.checkRequirements(player) != false) {
                                            action.attemptExecution(player)
                                            action.requirements?.executeSuccessActions(player)
                                        } else {
                                            action.requirements.executeDenyActions(player)
                                        }
                                    }
                                }
                            } else {
                                guiItem.clickRequirements.executeDenyActions(player)
                            }
                        }
                        .build())

                    // Since the slot is being filled at the highest priority, all remaining entries are lower priority
                    break
                } else {
                    guiItem.viewRequirements.executeDenyActions(player)
                }
            }
        }
    }

    private fun convertIndex(index: Int): Int {
        return if (index < 9) 27 + index else index - 9
    }

    override fun onClose(action: PageAction) {
        config.executeCloseActions(player)
        SkiesGUIs.INSTANCE.inventoryControllers.remove(player.uuid, controller)
    }

    override fun getTemplate(): Template {
        return template
    }

    override fun getInventoryTemplate(): Optional<InventoryTemplate> {
        return Optional.of(playerInventory)
    }

    override fun getTitle(): Component {
        return Utils.deserializeText(Utils.parsePlaceholders(player, config.title))
    }

    class InventoryController: UpdateEmitter<ChestGUI?>()
}
