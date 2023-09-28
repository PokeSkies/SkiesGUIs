package com.pokeskies.skiesguis.config

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import com.pokeskies.skiesguis.utils.recordCodec

class GuiConfig(
    val title: String,
    val size: Int,
    val items: Map<String, GuiItem>
) {
    companion object {
        val CODEC: Codec<GuiConfig> = RecordCodecBuilder.create { instance ->
            instance.group(
                Codec.STRING.recordCodec("title", GuiConfig::title),
                Codec.INT.recordCodec("size", GuiConfig::size),
                Codec.unboundedMap(Codec.STRING, GuiItem.CODEC).recordCodec("items", GuiConfig::items),
            ).apply(instance) { title, size, items ->
                GuiConfig(title, size, items)
            }
        }
    }

    override fun toString(): String {
        return "GuiConfig(title='$title', size=$size, items=$items)"
    }
}