package com.pokeskies.skiesguis.config

import ca.landonjw.gooeylibs2.api.button.GooeyButton
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import com.pokeskies.skiesguis.config.actions.Action
import com.pokeskies.skiesguis.config.requirements.Requirement
import com.pokeskies.skiesguis.utils.Utils
import com.pokeskies.skiesguis.utils.optionalRecordCodec
import com.pokeskies.skiesguis.utils.recordCodec
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.registry.Registries
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import java.util.*

class GuiItem(
    val item: Item,
    val slots: List<Int>,
    val amount: Int,
    val name: Optional<String>,
    val lore: List<String>,
    val nbt: Optional<NbtCompound>,
    val priority: Int,
    val viewRequirements: Map<String, Requirement>,
    val actions: Map<String, Action>
) {
    companion object {
        val CODEC: Codec<GuiItem> = RecordCodecBuilder.create { instance ->
            instance.group(
                Registries.ITEM.codec.recordCodec("item", GuiItem::item),
                Codec.INT.listOf().optionalRecordCodec("slots", GuiItem::slots, listOf()),
                Codec.INT.optionalRecordCodec("amount", GuiItem::amount, 1),
                Codec.STRING.optionalFieldOf("name").forGetter { it.name },
                Codec.STRING.listOf().optionalRecordCodec("lore", GuiItem::lore, listOf()),
                NbtCompound.CODEC.optionalFieldOf("nbt").forGetter { it.nbt },
                Codec.INT.optionalRecordCodec("priority", GuiItem::priority, 1),
                Codec.unboundedMap(Codec.STRING, Requirement.CODEC)
                    .optionalRecordCodec("view_requirements", GuiItem::viewRequirements, emptyMap()),
                Codec.unboundedMap(Codec.STRING, Action.CODEC)
                    .optionalRecordCodec("actions", GuiItem::actions, emptyMap()),
            ).apply(instance) { item, slots, amount, name, lore, nbt, priority, viewRequirements, actions ->
                GuiItem(item, slots, amount, name, lore, nbt, priority, viewRequirements, actions)
            }
        }
    }

    fun createButton(): GooeyButton.Builder {
        val stack = ItemStack(item, amount)

        if (nbt.isPresent) {
            stack.nbt = nbt.get()
        }

        val builder = GooeyButton.builder().display(stack)

        if (name.isPresent)
            builder.title(Utils.deseralizeText(name.get()))

        if (lore.isNotEmpty()) {
            builder.lore(Text::class.java, lore.stream().map { Utils.deseralizeText(it) }.toList())
        }

        return builder
    }

    fun checkViewRequirements(player: ServerPlayerEntity): Boolean {
        for (requirement in viewRequirements) {
            if (!requirement.value.check(player))
                return false
        }
        return true
    }

    override fun toString(): String {
        return "GuiItem(item=$item, slots=$slots, amount=$amount, name=$name, lore=$lore, nbt=$nbt, priority=$priority, viewRequirements=$viewRequirements, actions=$actions)"
    }
}