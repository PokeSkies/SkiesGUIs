package com.pokeskies.skiesguis.config

import ca.landonjw.gooeylibs2.api.UIManager
import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import com.pokeskies.skiesguis.config.actions.Action
import com.pokeskies.skiesguis.config.requirements.RequirementOptions
import com.pokeskies.skiesguis.gui.ChestGUI
import com.pokeskies.skiesguis.utils.FlexibleListAdaptorFactory
import com.pokeskies.skiesguis.utils.Scheduler
import net.minecraft.server.level.ServerPlayer

class GuiConfig(
    val title: String = "",
    val size: Int = 6,
    @JsonAdapter(FlexibleListAdaptorFactory::class)
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
    fun openGUI(player: ServerPlayer, id: String) {
        if (openRequirements?.checkRequirements(player) == false) {
            openRequirements.executeDenyActions(player)
            return
        }
        val gui = ChestGUI(player, id, this)
        openRequirements?.executeSuccessActions(player)
        UIManager.openUIForcefully(player, gui)
        Scheduler.scheduleTask(1, Scheduler.DelayedAction({ executeOpenActions(player) }))
    }

    private fun executeOpenActions(player: ServerPlayer) {
        for (actionEntry in openActions) {
            actionEntry.value.attemptExecution(player)
        }
    }

    fun executeCloseActions(player: ServerPlayer) {
        for (actionEntry in closeActions) {
            actionEntry.value.attemptExecution(player)
        }
    }

    override fun toString(): String {
        return "GuiConfig(title='$title', size=$size, alias_commands=$aliasCommands, open_requirements=$openRequirements, open_actions=$openActions, close_actions=$closeActions, items=$items)"
    }
}
