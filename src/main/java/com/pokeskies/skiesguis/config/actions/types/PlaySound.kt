package com.pokeskies.skiesguis.config.actions.types

import com.pokeskies.skiesguis.config.actions.Action
import com.pokeskies.skiesguis.config.actions.ActionType
import com.pokeskies.skiesguis.config.actions.ClickType
import com.pokeskies.skiesguis.config.requirements.RequirementOptions
import com.pokeskies.skiesguis.utils.Utils
import net.minecraft.core.Holder
import net.minecraft.network.protocol.game.ClientboundSoundPacket
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundSource

class PlaySound(
    type: ActionType = ActionType.PLAYSOUND,
    click: List<ClickType> = listOf(ClickType.ANY),
    delay: Long = 0,
    chance: Double = 0.0,
    requirements: RequirementOptions? = RequirementOptions(),
    private val sound: String = "",
    private val source: String? = null,
    private val volume: Float = 1.0F,
    private val pitch: Float = 1.0F
) : Action(type, click, delay, chance, requirements) {
    override fun executeAction(player: ServerPlayer) {
        if (sound.isEmpty()) {
            Utils.printError("[ACTION - ${type.name}] There was an error while executing for player ${player.name}: Sound ID was empty")
            return
        }

        val holder = Holder.direct(SoundEvent.createVariableRangeEvent(ResourceLocation.parse(sound)))

        var category = if (source == null) SoundSource.MASTER else SoundSource.entries.firstOrNull { it.name.equals(source, true) }
        if (category == null) {
            Utils.printError("[ACTION - ${type.name}] There was an error while executing for player ${player.name}: Sound Source '$source' was not found, defaulting to MASTER")
            category = SoundSource.MASTER
        }

        Utils.printDebug("[ACTION - ${type.name}] Player(${player.gameProfile.name}), Holder($holder), Category($category): $this")

        player.connection.send(
            ClientboundSoundPacket(
                holder,
                category,
                player.x,
                player.y,
                player.z,
                volume,
                pitch,
                player.serverLevel().getRandom().nextLong()
            )
        )
    }

    override fun toString(): String {
        return "PlaySound(click=$click, delay=$delay, chance=$chance, requirements=$requirements, " +
                "sound='$sound', source=$source, volume=$volume, pitch=$pitch)"
    }
}
