package com.pokeskies.skiesguis.config.actions.types

import com.pokeskies.skiesguis.config.actions.Action
import com.pokeskies.skiesguis.config.actions.ActionType
import com.pokeskies.skiesguis.config.actions.ClickType
import com.pokeskies.skiesguis.config.requirements.RequirementOptions
import com.pokeskies.skiesguis.utils.Utils
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundSource

class PlaySound(
    type: ActionType = ActionType.PLAYSOUND,
    click: List<ClickType> = listOf(ClickType.ANY),
    delay: Long = 0,
    chance: Double = 0.0,
    requirements: RequirementOptions? = RequirementOptions(),
    private val sound: String = "",
    private val volume: Float = 1.0F,
    private val pitch: Float = 1.0F
) : Action(type, click, delay, chance, requirements) {
    override fun executeAction(player: ServerPlayer) {
        Utils.printDebug("Attempting to execute a ${type.identifier} Action: $this")
        if (sound.isEmpty()) {
            Utils.printError("There was an error while executing a Sound Action for player ${player.name}: Sound ID was empty")
            return
        }

        val soundEvent = BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse(sound))
        if (soundEvent == null) {
            Utils.printError("There was an error while executing a Sound Action for player ${player.name}: Could not find a sound event with the ID $sound")
            return
        }

        player.playNotifySound(soundEvent, SoundSource.MASTER, volume, pitch)
    }

    override fun toString(): String {
        return "PlaySound(type=$type, click=$click, requirements=$requirements, sound=$sound, volume=$volume, pitch=$pitch)"
    }
}
