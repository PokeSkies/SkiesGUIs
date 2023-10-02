package com.pokeskies.skiesguis.config.actions.types

import ca.landonjw.gooeylibs2.api.UIManager
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import com.pokeskies.skiesguis.SkiesGUIs
import com.pokeskies.skiesguis.config.ConfigManager
import com.pokeskies.skiesguis.config.actions.Action
import com.pokeskies.skiesguis.config.actions.ActionType
import com.pokeskies.skiesguis.config.actions.ClickType
import com.pokeskies.skiesguis.config.requirements.ClickRequirement
import com.pokeskies.skiesguis.gui.ChestGUI
import com.pokeskies.skiesguis.utils.Utils
import com.pokeskies.skiesguis.utils.recordCodec
import net.minecraft.server.network.ServerPlayerEntity
import java.util.*

class OpenGUI(
    click: ClickType,
    clickRequirements: Optional<ClickRequirement>,
    private val id: String
) : Action(click, clickRequirements) {
    companion object {
        val CODEC: Codec<OpenGUI> = RecordCodecBuilder.create {
            actionCodec(it).and(
                Codec.STRING.recordCodec("id", OpenGUI::id),
            ).apply(it, ::OpenGUI)
        }
    }

    override fun execute(player: ServerPlayerEntity) {
        Utils.debug("Attempting to execute a ${getType().id} Action: $this")
        val guiConfig = ConfigManager.GUIS[id]
        if (guiConfig == null) {
            SkiesGUIs.LOGGER.error("There was an error while executing an action for player ${player.name}: Could not find a GUI with the ID $id!")
            return
        }

        UIManager.openUIForcefully(player, ChestGUI(player, id, guiConfig))
    }

    override fun getType(): ActionType<*> {
        return ActionType.OPEN_GUI
    }

    override fun toString(): String {
        return "OpenGUI(id='$id')"
    }
}