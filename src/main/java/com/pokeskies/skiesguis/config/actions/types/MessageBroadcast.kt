package com.pokeskies.skiesguis.config.actions.types

import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import com.pokeskies.skiesguis.SkiesGUIs
import com.pokeskies.skiesguis.config.actions.Action
import com.pokeskies.skiesguis.config.actions.ActionType
import com.pokeskies.skiesguis.config.actions.GenericClickType
import com.pokeskies.skiesguis.config.requirements.RequirementOptions
import com.pokeskies.skiesguis.gui.ChestGUI
import com.pokeskies.skiesguis.utils.FlexibleListAdaptorFactory
import com.pokeskies.skiesguis.utils.Utils
import net.minecraft.server.level.ServerPlayer

class MessageBroadcast(
    type: ActionType = ActionType.BROADCAST,
    click: List<GenericClickType> = listOf(GenericClickType.ANY),
    delay: Long = 0,
    chance: Double = 0.0,
    requirements: RequirementOptions? = RequirementOptions(),
    @JsonAdapter(FlexibleListAdaptorFactory::class) @SerializedName("message",  alternate = ["messages"])
    private val message: List<String> = emptyList()
) : Action(type, click, delay, chance, requirements) {
    override fun executeAction(player: ServerPlayer, gui: ChestGUI) {
        if (SkiesGUIs.INSTANCE.adventure == null) {
            Utils.printError("[ACTION - ${type.name}] There was an error while executing for player ${player.gameProfile.name}: Adventure is null")
            return
        }

        val parsedMessages = message.map { Utils.parsePlaceholders(player, it) }

        Utils.printDebug("[ACTION - ${type.name}] Player(${player.gameProfile.name}), Parsed Messages($parsedMessages): $this")

        for (line in parsedMessages) {
            SkiesGUIs.INSTANCE.adventure!!.all().sendMessage(Utils.deserializeText(line))
        }
    }

    override fun toString(): String {
        return "MessageBroadcast(click=$click, delay=$delay, chance=$chance, requirements=$requirements, " +
                "message=$message)"
    }
}
