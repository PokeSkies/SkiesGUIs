package com.pokeskies.skiesguis.gui

import com.cobblemon.mod.common.util.isInt
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import net.minecraft.world.inventory.MenuType

enum class InventoryType(val type: MenuType<*>, val slots: Int) {
    GENERIC_9x1(MenuType.GENERIC_9x1, 9),
    GENERIC_9x2(MenuType.GENERIC_9x2, 18),
    GENERIC_9x3(MenuType.GENERIC_9x3, 27),
    GENERIC_9x4(MenuType.GENERIC_9x4, 36),
    GENERIC_9x5(MenuType.GENERIC_9x5, 45),
    GENERIC_9x6(MenuType.GENERIC_9x6, 54),
    GENERIC_3x3(MenuType.GENERIC_3x3, 9),
    CRAFTER_3x3(MenuType.CRAFTER_3x3, 9),
    ANVIL(MenuType.ANVIL, 3),
    BEACON(MenuType.BEACON, 1),
    BLAST_FURNACE(MenuType.BLAST_FURNACE, 3),
    BREWING_STAND(MenuType.BREWING_STAND, 5),
    CRAFTING(MenuType.CRAFTING, 10),
    ENCHANTMENT(MenuType.ENCHANTMENT, 2),
    FURNACE(MenuType.FURNACE, 3),
    GRINDSTONE(MenuType.GRINDSTONE, 3),
    HOPPER(MenuType.HOPPER, 5),
    LOOM(MenuType.LOOM, 4),
    MERCHANT(MenuType.MERCHANT, 3),
    SHULKER_BOX(MenuType.SHULKER_BOX, 27),
    SMITHING(MenuType.SMITHING, 4),
    SMOKER(MenuType.SMOKER, 3),
    CARTOGRAPHY_TABLE(MenuType.CARTOGRAPHY_TABLE, 3),
    STONECUTTER(MenuType.STONECUTTER, 2);

    class Adapter : TypeAdapter<InventoryType>() {
        override fun write(out: JsonWriter, value: InventoryType?) {
            out.value(value?.name)
        }

        override fun read(reader: JsonReader): InventoryType {
            val value = reader.nextString()
            if (value.isInt()) {
                val rows = value.toInt()
                return when (rows) {
                    1 -> GENERIC_9x1
                    2 -> GENERIC_9x2
                    3 -> GENERIC_9x3
                    4 -> GENERIC_9x4
                    5 -> GENERIC_9x5
                    6 -> GENERIC_9x6
                    else -> GENERIC_9x6
                }
            }
            return InventoryType.entries.find { it.name.equals(value, true) }
                ?: GENERIC_9x6
        }
    }
}
