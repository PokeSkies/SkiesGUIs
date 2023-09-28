package com.pokeskies.skiesguis.utils

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import com.pokeskies.skiesguis.SkiesGUIs
import net.kyori.adventure.text.minimessage.MiniMessage
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtString
import net.minecraft.text.Text
import java.util.function.Function

object Utils {
    val miniMessage: MiniMessage = MiniMessage.miniMessage()

    fun deseralizeText(text: String): Text {
        return SkiesGUIs.INSTANCE.adventure!!.toNative(miniMessage.deserialize(text))
    }

    fun processItemStack(itemStack: ItemStack): ItemStack {
        itemStack.setCustomName(
            SkiesGUIs.INSTANCE.adventure!!
                .toNative(MiniMessage.miniMessage().deserialize(itemStack.name.string))
        )

        val displayNBT = itemStack.getSubNbt(ItemStack.DISPLAY_KEY)
        if (displayNBT != null && displayNBT.contains(ItemStack.LORE_KEY)) {
            val nbtList = displayNBT.getList(ItemStack.LORE_KEY, 8)
            for (i in 0 until nbtList.size) {
                val text: Text? = Text.Serializer.fromJson(nbtList.getString(i))
                if (text != null) {
                    nbtList[i] = NbtString.of(
                        Text.Serializer.toJson(
                            SkiesGUIs.INSTANCE.adventure!!
                                .toNative(MiniMessage.miniMessage().deserialize(text.string))
                        )
                    )
                }
            }
            displayNBT.put(ItemStack.LORE_KEY, nbtList)
            itemStack.setSubNbt(ItemStack.DISPLAY_KEY, displayNBT)
        }

        return itemStack
    }
}

fun <A, B> Codec<A>.recordCodec(id: String, getter: Function<B, A>): RecordCodecBuilder<B, A> {
    return this.fieldOf(id).forGetter(getter)
}

fun <A, B> Codec<A>.optionalRecordCodec(id: String, getter: Function<B, A>, default: A): RecordCodecBuilder<B, A> {
    return this.fieldOf(id).orElse(default).forGetter(getter)
}