package com.pokeskies.skiesguis.config

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import com.pokeskies.skiesguis.utils.recordCodec

class MainConfig(
    var debug: Boolean
) {
    companion object {
        val CODEC: Codec<MainConfig> = RecordCodecBuilder.create { instance ->
            instance.group(
                Codec.BOOL.recordCodec("debug", MainConfig::debug)
            ).apply(instance) { debug ->
                MainConfig(debug)
            }
        }
    }

    override fun toString(): String {
        return "MainConfig(debug=$debug)"
    }
}