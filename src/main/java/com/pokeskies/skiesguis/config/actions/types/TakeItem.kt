package com.pokeskies.skiesguis.config.actions.types

import com.google.gson.annotations.SerializedName
import com.pokeskies.skiesguis.SkiesGUIs
import com.pokeskies.skiesguis.config.actions.Action
import com.pokeskies.skiesguis.config.actions.ActionType
import com.pokeskies.skiesguis.config.actions.ClickType
import com.pokeskies.skiesguis.config.requirements.RequirementOptions
import com.pokeskies.skiesguis.gui.ChestGUI
import com.pokeskies.skiesguis.utils.Utils
import net.minecraft.core.component.DataComponentPatch
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.ItemStack
import kotlin.jvm.optionals.getOrNull

class TakeItem(
    type: ActionType = ActionType.TAKE_ITEM,
    click: List<ClickType> = listOf(ClickType.ANY),
    delay: Long = 0,
    chance: Double = 0.0,
    requirements: RequirementOptions? = RequirementOptions(),
    val item: String = "",
    val amount: Int = 1,
    val nbt: CompoundTag? = null,
    @SerializedName("custom_model_data")
    val customModelData: Int? = null,
    val strict: Boolean = true
) : Action(type, click, delay, chance, requirements) {
    override fun executeAction(player: ServerPlayer, gui: ChestGUI) {
        var removed = 0
        for ((i, stack) in player.inventory.items.withIndex()) {
            if (!stack.isEmpty) {
                if (isItem(stack)) {
                    val stackSize = stack.count
                    if (removed + stackSize >= amount) {
                        player.inventory.items[i].shrink(amount - removed)
                        break
                    } else {
                        player.inventory.items[i].shrink(stackSize)
                    }
                    removed += stackSize
                }
            }
        }
        Utils.printDebug("[ACTION - ${type.name}] Player(${player.gameProfile.name}), Items Removed ($removed): $this")
    }

    private fun isItem(checkItem: ItemStack): Boolean {
        val newItem = BuiltInRegistries.ITEM.getOptional(ResourceLocation.parse(item))
        if (newItem.isEmpty) {
            Utils.printDebug("[ACTION - ${type.name}] Failed due to an empty or invalid item ID. Item ID: $item, returned: $newItem")
            return false
        }
        if (!checkItem.item.equals(newItem.get())) {
            return false
        }

        var nbtCopy = nbt?.copy()

        if (customModelData != null) {
            if (nbtCopy != null) {
                nbtCopy.putInt("minecraft:custom_model_data", customModelData)
            } else {
                val newNBT = CompoundTag()
                newNBT.putInt("minecraft:custom_model_data", customModelData)
                nbtCopy = newNBT
            }
        }

        if (strict && nbtCopy != null) {
            val checkNBT = DataComponentPatch.CODEC.encodeStart(SkiesGUIs.INSTANCE.nbtOpts, checkItem.componentsPatch).result().getOrNull() ?: return false

            if (checkNBT != nbtCopy)
                return false
        }

        return true
    }

    override fun toString(): String {
        return "TakeItem(click=$click, delay=$delay, chance=$chance, requirements=$requirements, " +
                "item=$item, amount=$amount, nbt=$nbt, strict=$strict)"
    }
}
