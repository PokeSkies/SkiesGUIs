package com.pokeskies.skiesguis.config.actions.types

import ca.landonjw.gooeylibs2.api.UIManager
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import com.pokeskies.skiesguis.config.actions.Action
import com.pokeskies.skiesguis.config.actions.ActionType
import com.pokeskies.skiesguis.config.actions.ClickType
import net.minecraft.server.network.ServerPlayerEntity

class CloseGUI(
    click: ClickType
) : Action(click) {
    companion object {
        val CODEC: Codec<CloseGUI> = RecordCodecBuilder.create {
            actionCodec(it).apply(it, ::CloseGUI)
        }
    }

    override fun execute(player: ServerPlayerEntity) {
        UIManager.closeUI(player)
    }

    override fun getType(): ActionType<*> {
        return ActionType.CLOSE_GUI
    }
}