package com.pokeskies.skiesguis.config.requirements.types.internal

import com.google.gson.annotations.SerializedName
import com.pokeskies.skiesguis.config.requirements.ComparisonType
import com.pokeskies.skiesguis.config.requirements.Requirement
import com.pokeskies.skiesguis.config.requirements.RequirementType
import com.pokeskies.skiesguis.utils.Utils
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.NbtCompound
import net.minecraft.server.network.ServerPlayerEntity

class ItemRequirement(
    type: RequirementType = RequirementType.ITEM,
    comparison: ComparisonType = ComparisonType.EQUALS,
    val item: Item = Items.BARRIER,
    val amount: Int? = null,
    val nbt: NbtCompound? = null,
    @SerializedName("custom_model_data")
    val customModelData: Int? = null,
    val strict: Boolean = true
) : Requirement(type, comparison) {
    override fun checkRequirements(player: ServerPlayerEntity): Boolean {
        if (!checkComparison())
            return false

        val targetAmount = amount ?: 1
        var amountFound = 0

        for (itemStack in player.inventory.main) {
            if (!itemStack.isEmpty) {
                if (isItem(itemStack)) {
                    amountFound += itemStack.count
                }
            }
        }

        Utils.printDebug("Checking a ${type?.identifier} Requirement with items found='$amountFound': $this")

        return when (comparison) {
            ComparisonType.EQUALS -> {
                if (amount != null) {
                    return amountFound == amount
                } else {
                    return amountFound >= 1
                }
            }
            ComparisonType.NOT_EQUALS -> {
                if (amount != null) {
                    return amountFound != amount
                } else {
                    return amountFound == 0
                }
            }
            ComparisonType.GREATER_THAN -> amountFound > targetAmount
            ComparisonType.LESS_THAN -> amountFound < targetAmount
            ComparisonType.GREATER_THAN_OR_EQUALS -> amountFound >= targetAmount
            ComparisonType.LESS_THAN_OR_EQUALS -> amountFound <= targetAmount
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

            if (checkNBT != nbtCopy) {
                Utils.printDebug("Item Requirement failed due to NBT not matching. Looking for: $nbtCopy, but found: $checkNBT")
                return false
            }
        }

        return true
    }

    override fun allowedComparisons(): List<ComparisonType> {
        return ComparisonType.values().toList()
    }

    override fun toString(): String {
        return "ItemRequirement(comparison=$comparison, item=$item, amount=$amount, nbt=$nbt, strict=$strict)"
    }
}