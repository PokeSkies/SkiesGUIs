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
}

fun <A, B> Codec<A>.recordCodec(id: String, getter: Function<B, A>): RecordCodecBuilder<B, A> {
    return this.fieldOf(id).forGetter(getter)
}

fun <A, B> Codec<A>.optionalRecordCodec(id: String, getter: Function<B, A>, default: A): RecordCodecBuilder<B, A> {
    return this.fieldOf(id).orElse(default).forGetter(getter)
}