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

class GiveItem(
    type: ActionType = ActionType.GIVE_XP,
    click: List<ClickType> = listOf(ClickType.ANY),
    delay: Long = 0,
    chance: Double = 0.0,
    requirements: RequirementOptions? = RequirementOptions(),
    val item: Item = Items.BARRIER,
    val amount: Int = 1,
    val nbt: NbtCompound? = null,
    @SerializedName("custom_model_data")
    val customModelData: Int? = null
) : Action(type, click, delay, chance, requirements) {
    override fun executeAction(player: ServerPlayerEntity) {
        Utils.printDebug("Attempting to execute a ${type.identifier} Action: $this")
        val itemStack = ItemStack(item, amount)

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

        if (nbtCopy != null) {
            itemStack.nbt = nbtCopy
        }

        player.giveItemStack(itemStack)
    }

    override fun toString(): String {
        return "GiveItem(item=$item, amount=$amount, nbt=$nbt)"
    }

}