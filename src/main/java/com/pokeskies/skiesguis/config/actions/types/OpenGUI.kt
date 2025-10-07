package com.pokeskies.skiesguis.config.actions.types

import com.pokeskies.skiesguis.api.SkiesGUIsAPI
import com.pokeskies.skiesguis.config.actions.Action
import com.pokeskies.skiesguis.config.actions.ActionType
import com.pokeskies.skiesguis.config.actions.ClickType
import com.pokeskies.skiesguis.config.requirements.RequirementOptions
import com.pokeskies.skiesguis.gui.ChestGUI
import com.pokeskies.skiesguis.utils.Utils
import net.minecraft.server.level.ServerPlayer

class OpenGUI(
    type: ActionType = ActionType.OPEN_GUI,
    click: List<ClickType> = listOf(ClickType.ANY),
    delay: Long = 0,
    chance: Double = 0.0,
    requirements: RequirementOptions? = RequirementOptions(),
    private val id: String = ""
) : Action(type, click, delay, chance, requirements) {
    override fun executeAction(player: ServerPlayer, gui: ChestGUI) {
        val gui = SkiesGUIsAPI.getGUIConfig(id)
        if (gui == null) {
            Utils.printError("[ACTION - ${type.name}] There was an error while executing for player ${player.name}: Could not find a GUI with the ID $id!")
            return
        }

        Utils.printDebug("[ACTION - ${type.name}] Player(${player.gameProfile.name}), GUI($gui): $this")

        gui.openGUI(player, id)
    }

    override fun toString(): String {
        return "OpenGUI(click=$click, delay=$delay, chance=$chance, requirements=$requirements, id='$id')"
    }
}
