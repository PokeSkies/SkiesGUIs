package com.pokeskies.skiesguis.config

import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import com.pokeskies.skiesguis.config.actions.Action
import com.pokeskies.skiesguis.gui.InventoryType
import com.pokeskies.skiesguis.config.requirements.RequirementOptions
import com.pokeskies.skiesguis.gui.GenericGUI
import com.pokeskies.skiesguis.utils.FlexibleListAdaptorFactory
import com.pokeskies.skiesguis.utils.Scheduler
import me.lucko.fabric.api.permissions.v0.Permissions
import net.minecraft.commands.CommandSourceStack
import net.minecraft.server.level.ServerPlayer

class GuiConfig(
    val title: String = "",
    @SerializedName("type", alternate = ["size"])
    val type: InventoryType = InventoryType.GENERIC_9x6,
    @SerializedName("alias_permission")
    val aliasPermission: String? = null,
    @JsonAdapter(FlexibleListAdaptorFactory::class)
    @SerializedName("alias_commands")
    val aliasCommands: List<String> = listOf(),
    @SerializedName("open_requirements")
    val openRequirements: RequirementOptions? = null,
    @SerializedName("open_actions")
    val openActions: Map<String, Action> = emptyMap(),
    @SerializedName("close_actions")
    val closeActions: Map<String, Action> = emptyMap(),
    val items: Map<String, GuiItem> = emptyMap(),
    @SerializedName("clear_inventory")
    val clearInventory: Boolean = false
) {
    lateinit var id: String

    fun openGUI(player: ServerPlayer, id: String) {
        val gui = GenericGUI(player, id, this)
        if (openRequirements?.checkRequirements(player, gui) == false) {
            openRequirements.executeDenyActions(player, gui)
            return
        }
        openRequirements?.executeSuccessActions(player, gui)
        gui.open()
        Scheduler.scheduleTask(1, Scheduler.DelayedAction({ executeOpenActions(player, gui) }))
    }

    fun hasAliasPermission(ctx: CommandSourceStack): Boolean {
        if (aliasPermission == null) return Permissions.check(ctx, "skiesguis.open.$id", 2)
        return Permissions.check(ctx, aliasPermission, 2)
    }

    private fun executeOpenActions(player: ServerPlayer, gui: GenericGUI) {
        for (actionEntry in openActions) {
            actionEntry.value.attemptExecution(player, gui)
        }
    }

    fun executeCloseActions(player: ServerPlayer, gui: GenericGUI) {
        for (actionEntry in closeActions) {
            actionEntry.value.attemptExecution(player, gui)
        }
    }

    override fun toString(): String {
        return "GuiConfig(title='$title', type=$type, alias_commands=$aliasCommands, open_requirements=$openRequirements, open_actions=$openActions, close_actions=$closeActions, items=$items)"
    }
}
