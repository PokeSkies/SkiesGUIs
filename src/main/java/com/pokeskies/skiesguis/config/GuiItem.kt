package com.pokeskies.skiesguis.config

import ca.landonjw.gooeylibs2.api.button.GooeyButton
import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import com.pokeskies.skiesguis.config.actions.Action
import com.pokeskies.skiesguis.config.requirements.RequirementOptions
import com.pokeskies.skiesguis.utils.FlexibleListAdaptorFactory
import com.pokeskies.skiesguis.utils.Utils
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtList
import net.minecraft.nbt.NbtString
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text

class GuiItem(
    val item: Item = Items.BARRIER,
    @JsonAdapter(FlexibleListAdaptorFactory::class)
    val slots: List<Int> = emptyList(),
    val amount: Int = 1,
    val name: String? = null,
    @JsonAdapter(FlexibleListAdaptorFactory::class)
    val lore: List<String> = emptyList(),
    val nbt: NbtCompound? = null,
    val priority: Int = 0,
    @SerializedName("view_requirements")
    val viewRequirements: RequirementOptions? = null,
    @SerializedName("click_actions")
    val clickActions: Map<String, Action> = emptyMap(),
    @SerializedName("click_requirements")
    val clickRequirements: RequirementOptions? = null
) {
    fun createButton(player: ServerPlayerEntity): GooeyButton.Builder {
        val stack = ItemStack(item, amount)

        if (nbt != null) {
            // Parses the nbt and attempts to replace any placeholders
            for (key in ArrayList(nbt.keys)) {
                val element = nbt.get(key)
                if (element != null) {
                    if (element is NbtString) {
                        nbt.putString(key, Utils.parsePlaceholders(player, element.asString()))
                    } else if (element is NbtList) {
                        val parsed = NbtList()
                        for (entry in element) {
                            if (entry is NbtString) {
                                parsed.add(NbtString.of(Utils.parsePlaceholders(player, entry.asString())))
                            } else {
                                parsed.add(entry)
                            }
                        }
                        nbt.put(key, parsed)
                    }
                }
            }

            stack.nbt = nbt
        }

        val builder = GooeyButton.builder().display(stack)

        if (name != null)
            builder.title(Utils.deserializeText(Utils.parsePlaceholders(player, name)))

        if (lore.isNotEmpty()) {
            builder.lore(Text::class.java, lore.stream().map { Utils.deserializeText(Utils.parsePlaceholders(player, it)) }.toList())
        }

        return builder
    }

    override fun toString(): String {
        return "GuiItem(item=$item, slots=$slots, amount=$amount, name=$name, lore=$lore, nbt=$nbt, priority=$priority, view_requirements=$viewRequirements, click_actions=$clickActions)"
    }
}