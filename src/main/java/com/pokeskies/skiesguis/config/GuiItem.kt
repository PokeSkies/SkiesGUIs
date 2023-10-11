package com.pokeskies.skiesguis.config

import ca.landonjw.gooeylibs2.api.button.GooeyButton
import com.google.gson.annotations.SerializedName
import com.pokeskies.skiesguis.config.actions.Action
import com.pokeskies.skiesguis.config.requirements.RequirementOptions
import com.pokeskies.skiesguis.utils.Utils
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.NbtCompound
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text

class GuiItem(
    val item: Item = Items.BARRIER,
    val slots: List<Int> = emptyList(),
    val amount: Int = 1,
    val name: String? = null,
    val lore: List<String> = emptyList(),
    val nbt: NbtCompound? = null,
    val priority: Int = 0,
    @SerializedName("view_requirements")
    val viewRequirements: RequirementOptions? = null,
    @SerializedName("click_actions")
    val clickActions: Map<String, Action> = emptyMap()
) {
    fun createButton(): GooeyButton.Builder {
        val stack = ItemStack(item, amount)

        if (nbt != null) {
            stack.nbt = nbt
        }

        val builder = GooeyButton.builder().display(stack)

        if (name != null)
            builder.title(Utils.deseralizeText(name))

        if (lore.isNotEmpty()) {
            builder.lore(Text::class.java, lore.stream().map { Utils.deseralizeText(it) }.toList())
        }

        return builder
    }

    fun checkViewRequirements(player: ServerPlayerEntity): Boolean {
        if (viewRequirements != null) {
            for (requirement in viewRequirements.requirements) {
                if (!requirement.value.check(player))
                    return false
            }
        }
        return true
    }

    fun executeDenyActions(player: ServerPlayerEntity) {
        if (viewRequirements != null) {
            for ((id, action) in viewRequirements.denyActions) {
                action.attemptExecution(player)
            }
        }
    }

    fun executeSuccessActions(player: ServerPlayerEntity) {
        if (viewRequirements != null) {
            for ((id, action) in viewRequirements.successActions) {
                action.attemptExecution(player)
            }
        }
    }

    override fun toString(): String {
        return "GuiItem(item=$item, slots=$slots, amount=$amount, name=$name, lore=$lore, nbt=$nbt, priority=$priority, view_requirements=$viewRequirements, click_actions=$clickActions)"
    }
}