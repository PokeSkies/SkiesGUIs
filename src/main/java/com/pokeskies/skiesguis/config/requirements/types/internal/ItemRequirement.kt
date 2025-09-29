package com.pokeskies.skiesguis.config.requirements.types.internal

import com.google.gson.annotations.SerializedName
import com.pokeskies.skiesguis.SkiesGUIs
import com.pokeskies.skiesguis.config.requirements.ComparisonType
import com.pokeskies.skiesguis.config.requirements.Requirement
import com.pokeskies.skiesguis.config.requirements.RequirementType
import com.pokeskies.skiesguis.utils.Utils
import net.minecraft.core.component.DataComponentPatch
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.ItemStack
import kotlin.jvm.optionals.getOrNull

class ItemRequirement(
    type: RequirementType = RequirementType.ITEM,
    comparison: ComparisonType = ComparisonType.EQUALS,
    val item: String = "",
    val amount: Int? = null,
    val nbt: CompoundTag? = null,
    @SerializedName("custom_model_data")
    val customModelData: Int? = null,
    val strict: Boolean = true
) : Requirement(type, comparison) {
    override fun checkRequirements(player: ServerPlayer): Boolean {
        if (!checkComparison()) return false

        val targetAmount = amount ?: 1
        var amountFound = 0

        for (itemStack in player.inventory.items) {
            if (!itemStack.isEmpty) {
                if (isItem(itemStack)) {
                    amountFound += itemStack.count
                }
            }
        }

        Utils.printDebug("[REQUIREMENT - ${type?.name}] Player(${player.gameProfile.name}), Items Found($amountFound): $this")

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
        val newItem = BuiltInRegistries.ITEM.getOptional(ResourceLocation.parse(item))
        if (newItem.isEmpty) {
            Utils.printDebug("[REQUIREMENT - ${type?.name}] Failed due to an empty or invalid item ID. Item ID: $item, returned: $newItem")
            return false
        }
        if (!checkItem.item.equals(newItem.get())) {
            Utils.printDebug("[REQUIREMENT - ${type?.name}] Failed due to item not matching. Looking for: ${newItem.get()}, but found: ${checkItem.item}")
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

            if (checkNBT != nbtCopy) {
                Utils.printDebug("[REQUIREMENT - ${type?.name}] Failed due to NBT not matching. Looking for: $nbtCopy, but found: $checkNBT")
                return false
            }
        }

        return true
    }

    override fun allowedComparisons(): List<ComparisonType> {
        return ComparisonType.entries
    }

    override fun toString(): String {
        return "ItemRequirement(comparison=$comparison, item=$item, amount=$amount, nbt=$nbt, strict=$strict)"
    }
}
