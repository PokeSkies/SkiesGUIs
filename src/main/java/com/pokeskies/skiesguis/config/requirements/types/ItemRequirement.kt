package com.pokeskies.skiesguis.config.requirements.types

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import com.pokeskies.skiesguis.config.requirements.ComparisonType
import com.pokeskies.skiesguis.config.requirements.Requirement
import com.pokeskies.skiesguis.config.requirements.RequirementType
import com.pokeskies.skiesguis.utils.Utils
import com.pokeskies.skiesguis.utils.recordCodec
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.registry.Registries
import net.minecraft.server.network.ServerPlayerEntity
import java.util.*
import kotlin.jvm.optionals.getOrDefault

class ItemRequirement(
    comparison: ComparisonType,
    val item: Item,
    val amount: Optional<Int>,
    val nbt: Optional<NbtCompound>,
) : Requirement(comparison) {
    companion object {
        val CODEC: Codec<ItemRequirement> = RecordCodecBuilder.create { instance ->
            requirementCodec(instance).and(
                instance.group(
                    Registries.ITEM.codec.recordCodec("item", ItemRequirement::item),
                    Codec.INT.optionalFieldOf("amount").forGetter { it.amount },
                    NbtCompound.CODEC.optionalFieldOf("nbt").forGetter { it.nbt },
                )
            ).apply(instance, ::ItemRequirement)
        }
    }

    override fun check(player: ServerPlayerEntity): Boolean {
        if (!checkComparison())
            return false

        val targetAmount = amount.getOrDefault(1)
        var amountFound = 0

        for (itemStack in player.inventory.main) {
            if (!itemStack.isEmpty) {
                if (isItem(itemStack)) {
                    amountFound += itemStack.count
                }
            }
        }

        return when (comparison) {
            ComparisonType.EQUALS -> {
                if (amount.isPresent) {
                    return amountFound == amount.get()
                } else {
                    return amountFound > 1
                }
            }
            ComparisonType.NOT_EQUALS -> {
                if (amount.isPresent) {
                    return amountFound != amount.get()
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

        if (nbt.isPresent) {
            val checkNBT = checkItem.nbt ?: return false

            if (checkNBT != nbt.get())
                return false
        }

        return true
    }

    override fun getType(): RequirementType<*> {
        return RequirementType.PERMISSION
    }

    override fun getAllowedComparisons(): List<ComparisonType> {
        return ComparisonType.values().toList()
    }
}