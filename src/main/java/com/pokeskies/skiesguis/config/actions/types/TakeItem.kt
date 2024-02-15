package com.pokeskies.skiesguis.config.actions.types

import com.google.gson.annotations.SerializedName
import com.pokeskies.skiesguis.config.actions.Action
import com.pokeskies.skiesguis.config.actions.ActionType
import com.pokeskies.skiesguis.config.actions.ClickType
import com.pokeskies.skiesguis.config.requirements.RequirementOptions
import com.pokeskies.skiesguis.utils.Utils
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.NbtCompound
import net.minecraft.server.network.ServerPlayerEntity

class TakeItem(
    type: ActionType = ActionType.GIVE_XP,
    click: List<ClickType> = listOf(ClickType.ANY),
    delay: Long = 0,
    chance: Double = 0.0,
    requirements: RequirementOptions? = RequirementOptions(),
    val item: Item = Items.BARRIER,
    val amount: Int = 1,
    val nbt: NbtCompound? = null,
    @SerializedName("custom_model_data")
    val customModelData: Int? = null,
    val strict: Boolean = true
) : Action(type, click, delay, chance, requirements) {
    override fun executeAction(player: ServerPlayerEntity) {
        Utils.printDebug("Attempting to execute a ${type.identifier} Action: $this")
        var removed = 0
        for ((i, stack) in player.inventory.main.withIndex()) {
            if (!stack.isEmpty) {
                if (isItem(stack)) {
                    val stackSize = stack.count
                    if (removed + stackSize >= amount) {
                        player.inventory.main[i].decrement(amount - removed)
                        break
                    } else {
                        player.inventory.main[i].decrement(stackSize)
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
                val newNBT = NbtCompound()
                newNBT.putInt("CustomModelData", customModelData)
                nbtCopy = newNBT
            }
        }

        if (strict && nbtCopy != null) {
            val checkNBT = checkItem.nbt ?: return false

            if (checkNBT != nbtCopy)
                return false
        }

        return true
    }

    override fun toString(): String {
        return "TakeItem(item=$item, amount=$amount, nbt=$nbt, strict=$strict)"
    }
}