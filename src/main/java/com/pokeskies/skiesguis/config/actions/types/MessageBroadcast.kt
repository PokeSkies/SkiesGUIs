package com.pokeskies.skiesguis.config.actions.types

import com.google.gson.annotations.JsonAdapter
import com.pokeskies.skiesguis.SkiesGUIs
import com.pokeskies.skiesguis.config.actions.Action
import com.pokeskies.skiesguis.config.actions.ActionType
import com.pokeskies.skiesguis.config.actions.ClickType
import com.pokeskies.skiesguis.config.requirements.RequirementOptions
import com.pokeskies.skiesguis.utils.FlexibleListAdaptorFactory
import com.pokeskies.skiesguis.utils.Utils
import net.minecraft.server.level.ServerPlayer

class MessageBroadcast(
    type: ActionType = ActionType.BROADCAST,
    click: List<ClickType> = listOf(ClickType.ANY),
    delay: Long = 0,
    chance: Double = 0.0,
    requirements: RequirementOptions? = RequirementOptions(),
    @JsonAdapter(FlexibleListAdaptorFactory::class)
    private val message: List<String> = emptyList()
) : Action(type, click, delay, chance, requirements) {
    override fun executeAction(player: ServerPlayer) {
        Utils.printDebug("Attempting to execute a ${type.identifier} Action: $this")
        if (SkiesGUIs.INSTANCE.adventure == null) {
            Utils.printError("There was an error while executing an action for player ${player.name}: Adventure was somehow null on message broadcast?")
            return
        }

        for (line in message) {
            SkiesGUIs.INSTANCE.adventure!!.all().sendMessage(Utils.deserializeText(Utils.parsePlaceholders(player, line)))
        }
    }

    override fun toString(): String {
        return "MessageBroadcast(type=$type, click=$click, requirements=$requirements, message=$message)"
    }
}
