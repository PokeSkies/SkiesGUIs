package com.pokeskies.skiesguis.config.actions.types

import ca.landonjw.gooeylibs2.api.UIManager
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import com.pokeskies.skiesguis.config.actions.Action
import com.pokeskies.skiesguis.config.actions.ActionType
import com.pokeskies.skiesguis.config.actions.ClickType
import com.pokeskies.skiesguis.config.requirements.ClickRequirement
import com.pokeskies.skiesguis.utils.Utils
import net.minecraft.server.network.ServerPlayerEntity
import java.util.*

class CloseGUI(
    click: ClickType,
    clickRequirements: Optional<ClickRequirement>,
) : Action(click, clickRequirements) {
    companion object {
        val CODEC: Codec<CloseGUI> = RecordCodecBuilder.create {
            actionCodec(it).apply(it, ::CloseGUI)
        }
    }

    override fun execute(player: ServerPlayerEntity) {
        Utils.debug("Attempting to execute a ${getType().id} Action: $this")
        UIManager.closeUI(player)
    }

    override fun getType(): ActionType<*> {
        return ActionType.CLOSE_GUI
    }

    override fun toString(): String {
        return "CloseGUI()"
    }
}