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

class GiveItem(
    type: ActionType = ActionType.GIVE_ITEM,
    click: List<ClickType> = listOf(ClickType.ANY),
    delay: Long = 0,
    chance: Double = 0.0,
    requirements: RequirementOptions? = RequirementOptions(),
    val item: String = "",
    val amount: Int = 1,
    val nbt: CompoundTag? = null,
    @SerializedName("custom_model_data")
    val customModelData: Int? = null
) : Action(type, click, delay, chance, requirements) {
    override fun executeAction(player: ServerPlayer, gui: ChestGUI) {
        val newItem = BuiltInRegistries.ITEM.getOptional(ResourceLocation.parse(item))
        if (newItem.isEmpty) {
            Utils.printDebug("[ACTION - ${type.name}] Failed due to an empty or invalid item ID. Item ID: $item, returned: $newItem")
            return
        }
        val itemStack = ItemStack(newItem.get(), amount)

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
