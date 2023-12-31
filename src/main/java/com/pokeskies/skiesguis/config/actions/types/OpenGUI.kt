package com.pokeskies.skiesguis.config.actions.types

import com.pokeskies.skiesguis.config.ConfigManager
import com.pokeskies.skiesguis.config.actions.Action
import com.pokeskies.skiesguis.config.actions.ActionType
import com.pokeskies.skiesguis.config.actions.ClickType
import com.pokeskies.skiesguis.config.requirements.RequirementOptions
import com.pokeskies.skiesguis.utils.Utils
import net.minecraft.server.network.ServerPlayerEntity

class OpenGUI(
    type: ActionType = ActionType.OPEN_GUI,
    click: List<ClickType> = listOf(ClickType.ANY),
    delay: Long = 0,
    chance: Double = 0.0,
    requirements: RequirementOptions? = RequirementOptions(),
    private val id: String = ""
) : Action(type, click, delay, chance, requirements) {
    override fun executeAction(player: ServerPlayerEntity) {
        Utils.printDebug("Attempting to execute a ${type.identifier} Action: $this")
        val gui = ConfigManager.GUIS[id]
        if (gui == null) {
            Utils.printError("There was an error while executing an action for player ${player.name}: Could not find a GUI with the ID $id!")
            return
        }

        gui.openGUI(player, id)
    }

    override fun toString(): String {
        return "OpenGUI(type=$type, click=$click, requirements=$requirements, id='$id')"
    }
}