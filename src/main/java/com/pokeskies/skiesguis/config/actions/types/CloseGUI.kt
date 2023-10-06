package com.pokeskies.skiesguis.config.actions.types

import ca.landonjw.gooeylibs2.api.UIManager
import com.pokeskies.skiesguis.config.actions.Action
import com.pokeskies.skiesguis.config.actions.ActionType
import com.pokeskies.skiesguis.config.actions.ClickType
import com.pokeskies.skiesguis.config.requirements.RequirementOptions
import com.pokeskies.skiesguis.utils.Utils
import net.minecraft.server.network.ServerPlayerEntity

class CloseGUI(
    type: ActionType = ActionType.CLOSE_GUI,
    click: ClickType = ClickType.ANY,
    requirements: RequirementOptions? = null,
) : Action(type, click, requirements) {
    override fun execute(player: ServerPlayerEntity) {
        Utils.debug("Attempting to execute a ${type.identifier} Action: $this")
        UIManager.closeUI(player)
    }

    override fun toString(): String {
        return "CloseGUI(type=$type, click=$click, requirements=$requirements)"
    }
}