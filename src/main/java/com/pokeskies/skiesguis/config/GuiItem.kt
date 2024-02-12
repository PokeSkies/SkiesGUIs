package com.pokeskies.skiesguis.config

import ca.landonjw.gooeylibs2.api.button.GooeyButton
import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import com.pokeskies.skiesguis.SkiesGUIs
import com.pokeskies.skiesguis.config.actions.Action
import com.pokeskies.skiesguis.config.requirements.RequirementOptions
import com.pokeskies.skiesguis.utils.FlexibleListAdaptorFactory
import com.pokeskies.skiesguis.utils.Utils
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtHelper
import net.minecraft.nbt.NbtList
import net.minecraft.nbt.NbtString
import net.minecraft.registry.Registries
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import java.util.*

class GuiItem(
    val item: String = "",
    @JsonAdapter(FlexibleListAdaptorFactory::class)
    val slots: List<Int> = emptyList(),
    val amount: Int = 1,
    val name: String? = null,
    @JsonAdapter(FlexibleListAdaptorFactory::class)
    val lore: List<String> = emptyList(),
    val nbt: NbtCompound? = null,
    val priority: Int = 0,
    @SerializedName("custom_model_data")
    val customModelData: Int? = null,
    @SerializedName("view_requirements")
    val viewRequirements: RequirementOptions? = null,
    @SerializedName("click_actions")
    val clickActions: Map<String, Action> = emptyMap(),
    @SerializedName("click_requirements")
    val clickRequirements: RequirementOptions? = null
) {
    private fun getItemStack(player: ServerPlayerEntity): ItemStack {
        if (item.isEmpty()) return ItemStack(Items.BARRIER, amount)

        val parsedItem = Utils.parsePlaceholders(player, item)

        // Handles player head parsing
        if (parsedItem.contains("playerhead", true)) {
            var uuid: UUID? = null
            if (parsedItem.contains("-")) {
                val arg = parsedItem.replace("playerhead-", "")
                if (arg.isNotEmpty()) {
                    if (arg.contains("-")) {
                        try {
                            uuid = UUID.fromString(arg)
                        } catch (_: Exception) {}
                    } else {
                        val targetPlayer = SkiesGUIs.INSTANCE.server?.playerManager?.getPlayer(arg)
                        if (targetPlayer != null) {
                            uuid = targetPlayer.uuid
                        }
                    }
                }
            } else {
                uuid = player.uuid
            }
            val itemStack = ItemStack(Items.PLAYER_HEAD, amount)
            if (uuid != null) {
                val gameProfile = SkiesGUIs.INSTANCE.server?.userCache?.getByUuid(uuid)
                if (gameProfile != null && gameProfile.isPresent) {
                    itemStack.orCreateNbt.put("SkullOwner", NbtHelper.writeGameProfile(NbtCompound(), gameProfile.get()))
                    return itemStack
                }
            }

            Utils.printError("Error while attempting to parse Player Head: $parsedItem")
            return itemStack
        }

        val newItem = Registries.ITEM.get(Identifier(parsedItem))

        if (Registries.ITEM.defaultId == Registries.ITEM.getId(newItem)) {
            Utils.printError("Error while getting Item, defaulting to Barrier: $parsedItem")
            return ItemStack(Items.BARRIER, amount)
        }

        return ItemStack(newItem, amount)
    }

    fun createButton(player: ServerPlayerEntity): GooeyButton.Builder {
        val stack = getItemStack(player)

        if (nbt != null) {
            // Parses the nbt and attempts to replace any placeholders
            val parsedNBT = nbt.copy()
            for (key in nbt.keys) {
                val element = nbt.get(key)
                if (element != null) {
                    if (element is NbtString) {
                        parsedNBT.putString(key, Utils.parsePlaceholders(player, element.asString()))
                    } else if (element is NbtList) {
                        val parsed = NbtList()
                        for (entry in element) {
                            if (entry is NbtString) {
                                parsed.add(NbtString.of(Utils.parsePlaceholders(player, entry.asString())))
                            } else {
                                parsed.add(entry)
                            }
                        }
                        parsedNBT.put(key, parsed)
                    }
                }
            }

            if (stack.nbt != null && !stack.nbt?.isEmpty!!) {
                for (key in nbt.keys) {
                    stack.nbt?.put(key, nbt.get(key))
                }
            } else {
                stack.nbt = nbt
            }
        }

        if (customModelData != null) {
            stack.orCreateNbt.putInt("CustomModelData", customModelData)
        }

        val builder = GooeyButton.builder().display(stack)

        if (name != null)
            builder.title(Utils.deserializeText(Utils.parsePlaceholders(player, name)))

        if (lore.isNotEmpty()) {
            val parsedLore: MutableList<String> = mutableListOf()
            for (line in lore.stream().map { Utils.parsePlaceholders(player, it) }.toList()) {
                if (line.contains("\n")) {
                    line.split("\n").forEach { parsedLore.add(it) }
                } else {
                    parsedLore.add(line)
                }
            }
            builder.lore(Text::class.java, parsedLore.stream().map { Utils.deserializeText(it) }.toList())
        }

        return builder
    }

    override fun toString(): String {
        return "GuiItem(item=$item, slots=$slots, amount=$amount, name=$name, lore=$lore, nbt=$nbt, priority=$priority, view_requirements=$viewRequirements, click_actions=$clickActions)"
    }
}