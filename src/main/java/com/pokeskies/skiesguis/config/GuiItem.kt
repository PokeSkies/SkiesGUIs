package com.pokeskies.skiesguis.config

import ca.landonjw.gooeylibs2.api.button.GooeyButton
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import com.pokeskies.skiesguis.config.actions.Action
import com.pokeskies.skiesguis.utils.Utils
import com.pokeskies.skiesguis.utils.optionalRecordCodec
import com.pokeskies.skiesguis.utils.recordCodec
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.registry.Registries
import net.minecraft.text.Text
import java.util.*

class GuiItem(
    val item: Item,
    val slots: List<Int>,
    val count: Int,
    val name: Optional<String>,
    val lore: List<String>,
    val nbt: Optional<NbtCompound>,
    val actions: Map<String, Action>
) {
    companion object {
        val CODEC: Codec<GuiItem> = RecordCodecBuilder.create { instance ->
            instance.group(
                Registries.ITEM.codec.recordCodec("item", GuiItem::item),
                Codec.INT.listOf().optionalRecordCodec("slots", GuiItem::slots, listOf()),
                Codec.INT.optionalRecordCodec("count", GuiItem::count, 1),
                Codec.STRING.optionalFieldOf("name").forGetter { it.name },
                Codec.STRING.listOf().optionalRecordCodec("lore", GuiItem::lore, listOf()),
                NbtCompound.CODEC.optionalFieldOf("nbt").forGetter { it.nbt },
                Codec.unboundedMap(Codec.STRING, Action.CODEC)
                    .optionalRecordCodec("actions", GuiItem::actions, emptyMap())
            ).apply(instance) { item, slots, count, name, lore, tag, actions ->
                GuiItem(item, slots, count, name, lore, tag, actions)
            }
        }
    }

    fun createButton(): GooeyButton.Builder {
        val stack = ItemStack(item, count)

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

    override fun toString(): String {
        return "GuiItem(item=$item, slots=$slots, count=$count, name=$name, lore=$lore, nbt=$nbt, actions=$actions)"
    }
}