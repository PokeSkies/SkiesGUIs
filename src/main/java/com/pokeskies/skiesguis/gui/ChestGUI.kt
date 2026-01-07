package com.pokeskies.skiesguis.gui

import ca.landonjw.gooeylibs2.api.UIManager
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
import com.pokeskies.skiesguis.config.tooltips.TooltipBuilder
import com.pokeskies.skiesguis.utils.Utils
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.component.ItemLore
import java.util.*

class ChestGUI(
    val player: ServerPlayer,
    val guiId: String,
    val config: GuiConfig
) : UpdateEmitter<Page>(), Page {
    private val controller = InventoryController()
    private val template: ChestTemplate =
        ChestTemplate.Builder(config.size)
            .build()
    private val playerInventory: InventoryTemplate = InventoryTemplate.builder().build()
    var title = config.title

    val tooltipOverrides: MutableMap<Int, TooltipBuilder> = mutableMapOf()
    val manager: MolangManager? by lazy {
        if (FabricLoader.getInstance().isModLoaded("cobblemon")) {
            MolangManager(this)
        } else {
            null
        }
    }

    // This is a MAP (key=SLOT INDEX, value='MAP(key=PRIORITY, value=GUI ITEM ENTRY)')
    val items: TreeMap<Int, TreeMap<Int, Pair<String, GuiItem>>> = TreeMap()

    init {
        for (entry in config.items) {
            for (slot in entry.value.slots) {
                val priorities = items.getOrDefault(slot, TreeMap())
                priorities[entry.value.priority] = entry.key to entry.value
                items[slot] = priorities
            }
        }
    }

    fun open() {
        controller.subscribe(this, Runnable { refresh() })
        SkiesGUIs.INSTANCE.inventoryControllers[player.uuid] = controller

        refresh()

        UIManager.openUIForcefully(player, this)
    }

    fun refresh() {
        Utils.printDebug("[GUI] Refreshing GUI '$guiId' for player ${player.gameProfile.name}")
        update()
        // Just to keep the player's inventory up to date
        for ((i, stack) in player.inventory.items.withIndex()) {
            playerInventory.set(convertIndex(i), GooeyButton.builder().display(stack).build())
        }

        for ((slot, slotEntry) in items) {
            for ((_, itemEntry) in slotEntry) {
                val guiItem = itemEntry.second
                if (guiItem.viewRequirements?.checkRequirements(player, this) != false) {
                    guiItem.viewRequirements?.executeSuccessActions(player, this)
                    template[slot] = guiItem.createButton(player).also {
                        if (tooltipOverrides[slot] != null) {
                            val tooltip = tooltipOverrides[slot]!!.buildTooltip(player)
                            it.with(DataComponents.LORE, ItemLore(tooltip))
                        }
                    }.onClick { ctx ->
                        if (guiItem.clickRequirements?.checkRequirements(player, this) != false) {
                            guiItem.clickRequirements?.executeSuccessActions(player, this)
                            for (actionEntry in guiItem.clickActions) {
                                val action = actionEntry.value
                                if (action.matchesClick(ctx.clickType)) {
                                    if (action.requirements?.checkRequirements(player, this) != false) {
                                        action.attemptExecution(player, this)
                                        action.requirements?.executeSuccessActions(player, this)
                                    } else {
                                        action.requirements.executeDenyActions(player, this)
                                    }
                                }
                            }
                        } else {
                            guiItem.clickRequirements.executeDenyActions(player, this)
                        }
                    }.build()

                    // Since the slot is being filled at the highest priority, all remaining entries are lower priority
                    break
                } else {
                    guiItem.viewRequirements.executeDenyActions(player, this)
                }
            }
        }
    }

    private fun convertIndex(index: Int): Int {
        return if (index < 9) 27 + index else index - 9
    }

    override fun onClose(action: PageAction) {
        config.executeCloseActions(player, this)
        SkiesGUIs.INSTANCE.inventoryControllers.remove(player.uuid, controller)
    }

    override fun getTemplate(): Template {
        return template
    }

    override fun getInventoryTemplate(): Optional<InventoryTemplate> {
        return Optional.of(playerInventory)
    }

    override fun getTitle(): Component {
        return Utils.deserializeText(Utils.parsePlaceholders(player, title))
    }

    class InventoryController : UpdateEmitter<ChestGUI?>()
}
