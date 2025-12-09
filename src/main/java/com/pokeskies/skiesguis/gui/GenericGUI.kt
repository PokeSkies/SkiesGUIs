package com.pokeskies.skiesguis.gui

import com.pokeskies.skiesguis.config.GuiConfig
import com.pokeskies.skiesguis.config.GuiItem
import com.pokeskies.skiesguis.config.tooltips.TooltipBuilder
import com.pokeskies.skiesguis.utils.Utils
import eu.pb4.sgui.api.gui.SimpleGui
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.component.ItemLore
import java.util.*

class GenericGUI(
    player: ServerPlayer,
    val guiId: String,
    val config: GuiConfig
): SimpleGui(config.type.type, player, config.clearInventory) {
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

        refresh()
    }

    fun refresh() {
        Utils.printDebug("[GUI] Refreshing GUI '$guiId' for player ${player.gameProfile.name}")

        if (config.clearInventory) {
            for (i in config.type.slots..<size) {
                clearSlot(i)
            }
        }

        for ((slot, slotEntry) in items) {
            for ((_, itemEntry) in slotEntry) {
                val guiItem = itemEntry.second
                if (guiItem.viewRequirements?.checkRequirements(player, this) != false) {
                    guiItem.viewRequirements?.executeSuccessActions(player, this)
                    setSlot(slot, guiItem.createButton(player).also {
                        if (tooltipOverrides[slot] != null) {
                            val tooltip = tooltipOverrides[slot]!!.buildTooltip(player)
                            it.setComponent(DataComponents.LORE, ItemLore(tooltip))
                        }
                    }.setCallback { clickType ->
                        if (guiItem.clickRequirements?.checkRequirements(player, this) != false) {
                            guiItem.clickRequirements?.executeSuccessActions(player, this)
                            for (actionEntry in guiItem.clickActions) {
                                val action = actionEntry.value
                                if (action.matchesClick(clickType)) {
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
                    }.build())

                    // Since the slot is being filled at the highest priority, all remaining entries are lower priority
                    break
                } else {
                    guiItem.viewRequirements.executeDenyActions(player, this)
                }
            }
        }
    }

    override fun close() {
        config.executeCloseActions(player, this)
    }

    override fun getTitle(): Component {
        return Utils.deserializeText(Utils.parsePlaceholders(player, config.title))
    }
}
