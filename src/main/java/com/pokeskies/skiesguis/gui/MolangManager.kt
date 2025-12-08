package com.pokeskies.skiesguis.gui

import com.bedrockk.molang.runtime.MoLangRuntime
import com.bedrockk.molang.runtime.MoParams
import com.bedrockk.molang.runtime.struct.QueryStruct
import com.bedrockk.molang.runtime.value.DoubleValue
import com.bedrockk.molang.runtime.value.StringValue
import com.cobblemon.mod.common.api.molang.MoLangFunctions.asMoLangValue
import com.cobblemon.mod.common.api.molang.MoLangFunctions.setup
import com.pokeskies.skiesguis.config.tooltips.TooltipBuilder
import com.pokeskies.skiesguis.config.tooltips.TooltipConfig
import com.pokeskies.skiesguis.utils.Utils
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.item.ItemStack

import java.util.*
import java.util.function.Function

class MolangManager(gui: ChestGUI) {
    val runtime: MoLangRuntime = MoLangRuntime().setup().also {
        it.environment.query.addFunction("gui") { guiMolangStruct(gui) }
    }

    fun guiMolangStruct(gui: ChestGUI): QueryStruct = QueryStruct(
        hashMapOf(
            "slots" to Function { params ->
                QueryStruct(
                    hashMapOf(
                        "size" to Function { DoubleValue(gui.items.size) },
                        "items" to Function { params ->
                            val map = hashMapOf<String, Function<MoParams, Any>>()
                            for ((slot, slotEntry) in gui.items) {
                                val slotMap = hashMapOf<String, Function<MoParams, Any>>()
                                for ((_, itemEntry) in slotEntry) {
                                    val guiItem = itemEntry.second
                                    slotMap[guiItem.item] = Function { params ->
                                        stackMolangValue(guiItem.createButton(gui.player).build().itemStack)
                                    }
                                }
                                map[slot.toString()] = Function { params ->
                                    QueryStruct(slotMap)
                                }
                            }
                            QueryStruct(map)
                        }
                    )
                )
            },
            "get_title" to Function { StringValue(gui.config.title) },
            "get_size" to Function { DoubleValue(gui.config.type.slots) },
            "get_id" to Function { StringValue(gui.guiId) },
            "set_slot" to Function { params ->
                val slot = params.getInt(0) ?: return@Function null
                val item = params.getString(1) ?: return@Function null
                val guiItem = gui.config.items[item] ?: return@Function null
                val priority = params.getInt(2) ?: return@Function null
                val priorities = gui.items.getOrDefault(slot, TreeMap())
                priorities[priority] = item to guiItem
                gui.items[slot] = priorities
                gui.refresh()
            },
            "remove_slot" to Function { params ->
                val slot = params.getInt(0) ?: return@Function null
                val priority = params.getInt(1) ?: return@Function null
                val slotEntry = gui.items[slot] ?: return@Function null
                slotEntry.remove(priority)
                if (slotEntry.isEmpty()) {
                    gui.items.remove(slot)
                }
                gui.refresh()
            },
            "clear_slots" to Function { params ->
                gui.items.clear()
                (0..gui.virtualSize).forEach { i -> gui.clearSlot(i) }
                gui.refresh()
            },
            "player" to Function { gui.player.asMoLangValue() },
            "set_title" to Function { params ->
                gui.title = params.getString(0)?.let { Utils.deserializeText(it) } ?: return@Function null
                gui.refresh()
            },
            "set_tooltip" to Function { params ->
                val slot = params.getInt(0) ?: return@Function null
                val tooltip = params.getString(1) ?: return@Function null
                val builder = TooltipConfig.TOOLTIPS[tooltip] ?: return@Function null
                gui.tooltipOverrides[slot] = TooltipBuilder(builder)
                gui.refresh()
            },
            "refresh" to Function { gui.refresh() }
        )
    )

    fun stackMolangValue(stack: ItemStack): QueryStruct {
        return QueryStruct(
            hashMapOf(
                "item" to Function { StringValue(BuiltInRegistries.ITEM.getKey(stack.item).asString()) },
                "count" to Function { DoubleValue(stack.count.toDouble()) }
            )
        )
    }
}