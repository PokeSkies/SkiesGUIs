package com.pokeskies.skiesguis.config.actions.types

import com.pokeskies.skiesguis.config.actions.Action
import com.pokeskies.skiesguis.config.actions.ActionType
import com.pokeskies.skiesguis.config.actions.ClickType
import com.pokeskies.skiesguis.config.requirements.RequirementOptions
import com.pokeskies.skiesguis.utils.Utils
import net.fabricmc.fabric.impl.biome.modification.BuiltInRegistryKeys
import net.minecraft.registry.BuiltinRegistries
import net.minecraft.registry.Registries
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.util.Identifier

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
    override fun executeAction(player: ServerPlayerEntity) {
        Utils.printDebug("Attempting to execute a ${type.identifier} Action: $this")
        if (sound.isEmpty()) {
            Utils.printError("There was an error while executing a Sound Action for player ${player.name}: Sound ID was empty")
            return
        }
        player.playSound(SoundEvent.of(Identifier(sound)), SoundCategory.MASTER, volume, pitch)
    }

    override fun toString(): String {
        return "PlaySound(type=$type, click=$click, requirements=$requirements, sound=$sound, volume=$volume, pitch=$pitch)"
    }
}