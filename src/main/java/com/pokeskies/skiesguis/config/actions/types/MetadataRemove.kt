package com.pokeskies.skiesguis.config.actions.types

import com.pokeskies.skiesguis.SkiesGUIs
import com.pokeskies.skiesguis.config.actions.Action
import com.pokeskies.skiesguis.config.actions.ActionType
import com.pokeskies.skiesguis.gui.GenericClickType
import com.pokeskies.skiesguis.config.requirements.RequirementOptions
import com.pokeskies.skiesguis.gui.GenericGUI
import com.pokeskies.skiesguis.utils.Utils
import net.minecraft.server.level.ServerPlayer

class MetadataRemove(
    type: ActionType = ActionType.METADATA_REMOVE,
    click: List<GenericClickType> = listOf(GenericClickType.ANY),
    delay: Long = 0,
    chance: Double = 0.0,
    requirements: RequirementOptions? = RequirementOptions(),
    private val key: String = ""
) : Action(type, click, delay, chance, requirements) {
    override fun executeAction(player: ServerPlayer, gui: GenericGUI) {
        Utils.printDebug("[ACTION - ${type.name}] Player(${player.gameProfile.name}): $this")

        val storage = SkiesGUIs.INSTANCE.storage ?: run {
            Utils.printError("[ACTION - ${type.name}] The storage system is not initialized!")
            return
        }

        if (key.isEmpty()) {
            Utils.printError("[ACTION - ${type.name}] The key '${key}' is null or empty! Could not remove metadata.")
            return
        }

        val userData = storage.getUser(player)
        userData.metdadata.remove(key)
        if (!storage.saveUser(player, userData)) {
            Utils.printError("[ACTION - ${type.name}] Failed to remove metadata for player ${player.name.string}.")
            return
        }
    }

    override fun toString(): String {
        return "MetadataRemove(click=$click, delay=$delay, chance=$chance, requirements=$requirements, " +
                "key=$key)"
    }
}
