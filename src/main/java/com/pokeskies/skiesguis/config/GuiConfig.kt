package com.pokeskies.skiesguis.config

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import com.pokeskies.skiesguis.utils.recordCodec

class GuiConfig(
    val title: String = "",
    val size: Int = 6,
    val items: Map<String, GuiItem> = emptyMap()
) {
    override fun toString(): String {
        return "GuiConfig(title='$title', size=$size, items=$items)"
    }
}