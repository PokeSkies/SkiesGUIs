package com.pokeskies.skiesguis.config

import ca.landonjw.gooeylibs2.api.UIManager
import com.google.gson.annotations.SerializedName
import com.pokeskies.skiesguis.config.actions.Action
import com.pokeskies.skiesguis.config.requirements.RequirementOptions
import com.pokeskies.skiesguis.gui.ChestGUI
import net.minecraft.server.network.ServerPlayerEntity

class GuiConfig(
    val title: String = "",
    val size: Int = 6,
    @SerializedName("alias_commands")
    val aliasCommands: List<String> = listOf(),
    @SerializedName("open_requirements")
    val openRequirements: RequirementOptions? = null,
    @SerializedName("open_actions")
    val openActions: Map<String, Action> = emptyMap(),
    @SerializedName("close_actions")
    val closeActions: Map<String, Action> = emptyMap(),
    val items: Map<String, GuiItem> = emptyMap()
) {
    fun openGUI(player: ServerPlayerEntity) {
        if (!checkOpenRequirements(player)) {
            executeDenyActions(player)
            return
        }
        executeSuccessActions(player)
        executeOpenActions(player)
        UIManager.openUIForcefully(player, ChestGUI(player, this))
    }

    private fun checkOpenRequirements(player: ServerPlayerEntity): Boolean {
        if (openRequirements != null) {
            for (requirement in openRequirements.requirements) {
                if (!requirement.value.checkRequirements(player))
                    return false
            }
        }
        return true
    }

    private fun executeDenyActions(player: ServerPlayerEntity) {
        if (openRequirements != null) {
            for ((id, action) in openRequirements.denyActions) {
                action.attemptExecution(player)
            }
        }
    }

    private fun executeSuccessActions(player: ServerPlayerEntity) {
        if (openRequirements != null) {
            for ((id, action) in openRequirements.successActions) {
                action.attemptExecution(player)
            }
        }
    }

    private fun executeOpenActions(player: ServerPlayerEntity) {
        for (actionEntry in openActions) {
            actionEntry.value.attemptExecution(player)
        }
    }

    fun executeCloseActions(player: ServerPlayerEntity) {
        for (actionEntry in closeActions) {
            actionEntry.value.attemptExecution(player)
        }
    }

    override fun toString(): String {
        return "GuiConfig(title='$title', size=$size, alias_commands=$aliasCommands, open_requirements=$openRequirements, open_actions=$openActions, close_actions=$closeActions, items=$items)"
    }
}