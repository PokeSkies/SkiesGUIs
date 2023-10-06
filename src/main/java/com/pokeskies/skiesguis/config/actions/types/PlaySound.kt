package com.pokeskies.skiesguis.config.actions.types

import com.pokeskies.skiesguis.config.actions.Action
import com.pokeskies.skiesguis.config.actions.ActionType
import com.pokeskies.skiesguis.config.actions.ClickType
import com.pokeskies.skiesguis.config.requirements.RequirementOptions
import com.pokeskies.skiesguis.utils.Utils
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent

class PlaySound(
    type: ActionType = ActionType.PLAYSOUND,
    click: ClickType = ClickType.ANY,
    private val sound: SoundEvent,
    private val volume: Float = 1F,
    private val pitch: Float = 1F
    requirements: RequirementOptions? = null,
) : Action(type, click, requirements) {
    override fun execute(player: ServerPlayerEntity) {
        Utils.debug("Attempting to execute a ${type.identifier} Action: $this")
        player.playSound(sound, SoundCategory.MASTER, volume, pitch)
    }

    override fun toString(): String {
        return "PlaySound(type=$type, click=$click, requirements=$requirements, sound=$sound, volume=$volume, pitch=$pitch)"
    }
}