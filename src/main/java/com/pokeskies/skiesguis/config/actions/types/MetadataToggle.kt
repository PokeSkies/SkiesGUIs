package com.pokeskies.skiesguis.config.actions.types

import com.pokeskies.skiesguis.SkiesGUIs
import com.pokeskies.skiesguis.config.actions.Action
import com.pokeskies.skiesguis.config.actions.ActionType
import com.pokeskies.skiesguis.config.actions.GenericClickType
import com.pokeskies.skiesguis.config.requirements.RequirementOptions
import com.pokeskies.skiesguis.data.MetadataType
import com.pokeskies.skiesguis.gui.ChestGUI
import com.pokeskies.skiesguis.utils.Utils
import net.minecraft.server.level.ServerPlayer

class MetadataToggle(
    type: ActionType = ActionType.METADATA_TOGGLE,
    click: List<GenericClickType> = listOf(GenericClickType.ANY),
    delay: Long = 0,
    chance: Double = 0.0,
    requirements: RequirementOptions? = RequirementOptions(),
    private val key: String = ""
) : Action(type, click, delay, chance, requirements) {
    override fun executeAction(player: ServerPlayer, gui: ChestGUI) {
        Utils.printDebug("[ACTION - ${type.name}] Player(${player.gameProfile.name}): $this")

        val storage = SkiesGUIs.INSTANCE.storage ?: run {
            Utils.printError("[ACTION - ${type.name}] The storage system is not initialized!")
            return
        }

        if (key.isEmpty()) {
            Utils.printError("[ACTION - ${type.name}] The key '${key}' is null or empty! Could not set metadata.")
            return
        }

        val userData = storage.getUser(player)

        val entry = userData.metdadata[key] ?: run {
            Utils.printError("[ACTION - ${type.name}] No existing metadata found for key '${key}'. Cannot toggle non-existent metadata.")
            return
        }

        if (entry.type != MetadataType.BOOLEAN) {
            Utils.printError("[ACTION - ${type.name}] Metadata for key '${key}' is type '${entry.type}', not BOOLEAN. Cannot toggle non-boolean metadata.")
            return
        }

        entry.value = !(entry.value as Boolean)
        userData.metdadata[key] = entry
        if (!storage.saveUser(player, userData)) {
            Utils.printError("[ACTION - ${type.name}] Failed to save metadata for player ${player.name.string}.")
            return
        }
    }

    override fun toString(): String {
        return "MetadataToggle(click=$click, delay=$delay, chance=$chance, requirements=$requirements, " +
                "key=$key)"
    }
}
