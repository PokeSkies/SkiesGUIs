package com.pokeskies.skiesguis.config.actions.types

import com.google.gson.annotations.SerializedName
import com.pokeskies.skiesguis.SkiesGUIs
import com.pokeskies.skiesguis.config.actions.Action
import com.pokeskies.skiesguis.config.actions.ActionType
import com.pokeskies.skiesguis.config.actions.ClickType
import com.pokeskies.skiesguis.config.requirements.RequirementOptions
import com.pokeskies.skiesguis.utils.Utils
import net.minecraft.core.component.DataComponentPatch
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import kotlin.jvm.optionals.getOrNull

class TakeItem(
    type: ActionType = ActionType.GIVE_XP,
    click: List<ClickType> = listOf(ClickType.ANY),
    delay: Long = 0,
    chance: Double = 0.0,
    requirements: RequirementOptions? = RequirementOptions(),
    val item: Item = Items.BARRIER,
    val amount: Int = 1,
    val nbt: CompoundTag? = null,
    @SerializedName("custom_model_data")
    val customModelData: Int? = null,
    val strict: Boolean = true
) : Action(type, click, delay, chance, requirements) {
    override fun executeAction(player: ServerPlayer) {
        Utils.printDebug("Attempting to execute a ${type.identifier} Action: $this")
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
    }

    private fun isItem(checkItem: ItemStack): Boolean {
        if (!checkItem.item.equals(item)) {
            return false
        }

        var nbtCopy = nbt?.copy()

        if (customModelData != null) {
            if (nbtCopy != null) {
                nbtCopy.putInt("CustomModelData", customModelData)
            } else {
                val newNBT = CompoundTag()
                newNBT.putInt("CustomModelData", customModelData)
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
        return "TakeItem(item=$item, amount=$amount, nbt=$nbt, strict=$strict)"
    }
}
