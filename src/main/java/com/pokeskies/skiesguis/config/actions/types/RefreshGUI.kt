package com.pokeskies.skiesguis.config.actions.types

import com.pokeskies.skiesguis.config.actions.Action
import com.pokeskies.skiesguis.config.actions.ActionType
import com.pokeskies.skiesguis.config.actions.GenericClickType
import com.pokeskies.skiesguis.config.requirements.RequirementOptions
import com.pokeskies.skiesguis.gui.ChestGUI
import com.pokeskies.skiesguis.utils.Utils
import net.minecraft.server.level.ServerPlayer

class RefreshGUI(
    type: ActionType = ActionType.REFRESH_GUI,
    click: List<GenericClickType> = listOf(GenericClickType.ANY),
    delay: Long = 0,
    chance: Double = 0.0,
    requirements: RequirementOptions? = RequirementOptions(),
) : Action(type, click, delay, chance, requirements) {
    override fun executeAction(player: ServerPlayer, gui: ChestGUI) {
        Utils.printDebug("[ACTION - ${type.name}] Player(${player.gameProfile.name}): $this")
        gui.refresh()
    }

    override fun toString(): String {
        return "RefreshGUI(click=$click, delay=$delay, chance=$chance, requirements=$requirements)"
    }
}
