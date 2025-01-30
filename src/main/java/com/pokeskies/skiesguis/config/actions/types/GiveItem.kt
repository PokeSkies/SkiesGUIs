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

class GiveItem(
    type: ActionType = ActionType.GIVE_XP,
    click: List<ClickType> = listOf(ClickType.ANY),
    delay: Long = 0,
    chance: Double = 0.0,
    requirements: RequirementOptions? = RequirementOptions(),
    val item: Item = Items.BARRIER,
    val amount: Int = 1,
    val nbt: CompoundTag? = null,
    @SerializedName("custom_model_data")
    val customModelData: Int? = null
) : Action(type, click, delay, chance, requirements) {
    override fun executeAction(player: ServerPlayer) {
        val itemStack = ItemStack(item, amount)

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

        if (nbtCopy != null) {
            DataComponentPatch.CODEC.decode(SkiesGUIs.INSTANCE.nbtOpts, nbtCopy).result().ifPresent { result ->
                itemStack.applyComponents(result.first)
            }
        }

        Utils.printDebug("[ACTION - ${type.name}] Player(${player.gameProfile.name}), ItemStack(${itemStack}: $this")

        player.addItem(itemStack)
    }

    override fun toString(): String {
        return "GiveItem(click=$click, delay=$delay, chance=$chance, requirements=$requirements, " +
                "item=$item, amount=$amount, nbt=$nbt)"
    }

}
