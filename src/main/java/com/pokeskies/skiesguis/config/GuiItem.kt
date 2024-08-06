package com.pokeskies.skiesguis.config

import ca.landonjw.gooeylibs2.api.button.GooeyButton
import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import com.pokeskies.skiesguis.SkiesGUIs
import com.pokeskies.skiesguis.config.actions.Action
import com.pokeskies.skiesguis.config.requirements.RequirementOptions
import com.pokeskies.skiesguis.utils.FlexibleListAdaptorFactory
import com.pokeskies.skiesguis.utils.Utils
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.NbtUtils
import net.minecraft.nbt.StringTag
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import java.util.*

class GuiItem(
    val item: String = "",
    @JsonAdapter(FlexibleListAdaptorFactory::class)
    val slots: List<Int> = emptyList(),
    val amount: Int = 1,
    val name: String? = null,
    @JsonAdapter(FlexibleListAdaptorFactory::class)
    val lore: List<String> = emptyList(),
    val nbt: CompoundTag? = null,
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
    private fun getItemStack(player: ServerPlayer): ItemStack {
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
                        val targetPlayer = SkiesGUIs.INSTANCE.server?.playerList?.getPlayer(arg)
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
                val gameProfile = SkiesGUIs.INSTANCE.server?.profileCache?.get(uuid)
                if (gameProfile != null && gameProfile.isPresent) {
                    itemStack.orCreateNbt.put("SkullOwner", NbtUtils.writeGameProfile(CompoundTag(), gameProfile.get()))
                    return itemStack
                }
            }

            Utils.printError("Error while attempting to parse Player Head: $parsedItem")
            return itemStack
        }

        val newItem = BuiltInRegistries.ITEM.getOptional(ResourceLocation.parse(parsedItem))

        if (newItem.isEmpty) {
            Utils.printError("Error while getting Item, defaulting to Barrier: $parsedItem")
            return ItemStack(Items.BARRIER, amount)
        }

        return ItemStack(newItem.get(), amount)
    }

    fun createButton(player: ServerPlayer): GooeyButton.Builder {
        val stack = getItemStack(player)

        if (nbt != null) {
            // Parses the nbt and attempts to replace any placeholders
            val parsedNBT = nbt.copy()
            for (key in nbt.allKeys) {
                val element = nbt.get(key)
                if (element != null) {
                    if (element is StringTag) {
                        parsedNBT.putString(key, Utils.parsePlaceholders(player, element.asString))
                    } else if (element is ListTag) {
                        val parsed = ListTag()
                        for (entry in element) {
                            if (entry is StringTag) {
                                parsed.add(StringTag.valueOf(Utils.parsePlaceholders(player, entry.asString)))
                            } else {
                                parsed.add(entry)
                            }
                        }
                        parsedNBT.put(key, parsed)
                    }
                }
            }

            if (stack.nbt != null && !stack.nbt!!.isEmpty) {
                for (key in parsedNBT.allKeys) {
                    stack.nbt?.put(key, parsedNBT.get(key))
                }
            } else {
                stack.nbt = parsedNBT
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
            builder.lore(Component::class.java, parsedLore.stream().map { Utils.deserializeText(it) }.toList())
        }

        return builder
    }

    override fun toString(): String {
        return "GuiItem(item=$item, slots=$slots, amount=$amount, name=$name, lore=$lore, nbt=$nbt, priority=$priority, view_requirements=$viewRequirements, click_actions=$clickActions)"
    }
}
