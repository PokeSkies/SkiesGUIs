package com.pokeskies.skiesguis.config.actions.types

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import com.pokeskies.skiesguis.config.actions.Action
import com.pokeskies.skiesguis.config.actions.ActionType
import com.pokeskies.skiesguis.config.actions.ClickType
import com.pokeskies.skiesguis.utils.optionalRecordCodec
import com.pokeskies.skiesguis.utils.recordCodec
import net.minecraft.registry.Registries
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundEvent

class PlaySound(
    click: ClickType,
    private val sound: SoundEvent,
    private val volume: Float,
    private val pitch: Float
) : Action(click) {
    companion object {
        val CODEC: Codec<PlaySound> = RecordCodecBuilder.create {
            actionCodec(it).and(
                it.group(
                    Registries.SOUND_EVENT.codec.recordCodec("sound", PlaySound::sound),
                    Codec.FLOAT.optionalRecordCodec("volume", PlaySound::volume, 1.0F),
                    Codec.FLOAT.optionalRecordCodec("pitch", PlaySound::pitch, 1.0F),
                )
            ).apply(it, ::PlaySound)
        }
    }

    override fun execute(player: ServerPlayerEntity) {
        player.playSound(sound, volume, pitch)
    }

    override fun getType(): ActionType<*> {
        return ActionType.PLAYSOUND
    }
}