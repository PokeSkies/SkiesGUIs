package com.pokeskies.skiesguis.config.actions.types

import com.google.gson.annotations.SerializedName
import com.pokeskies.skiesguis.config.actions.Action
import com.pokeskies.skiesguis.config.actions.ActionType
import com.pokeskies.skiesguis.config.actions.ClickType
import com.pokeskies.skiesguis.config.requirements.RequirementOptions
import com.pokeskies.skiesguis.utils.Utils
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents

class PlaySound(
    type: ActionType = ActionType.PLAYSOUND,
    click: ClickType = ClickType.ANY,
    requirements: RequirementOptions? = null,
    private val sound: SoundEvent? = null,
    private val volume: Float = 1.0F,
    private val pitch: Float = 1.0F
) : Action(type, click, requirements) {
    override fun execute(player: ServerPlayerEntity) {
        Utils.debug("Attempting to execute a ${type.identifier} Action: $this")
        if (sound == null) {
            Utils.error("There was an error while executing a Sound Action for player ${player.name}: Sound was somehow null?")
            return
        }
        player.playSound(sound, SoundCategory.MASTER, volume, pitch)
    }

    override fun toString(): String {
        return "PlaySound(type=$type, click=$click, requirements=$requirements, sound=$sound, volume=$volume, pitch=$pitch)"
    }
}