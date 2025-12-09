package com.pokeskies.skiesguis.config.actions.types

import com.pokeskies.skiesguis.SkiesGUIs
import com.pokeskies.skiesguis.config.actions.Action
import com.pokeskies.skiesguis.config.actions.ActionType
import com.pokeskies.skiesguis.gui.GenericClickType
import com.pokeskies.skiesguis.config.requirements.RequirementOptions
import com.pokeskies.skiesguis.data.MetadataType
import com.pokeskies.skiesguis.gui.GenericGUI
import com.pokeskies.skiesguis.utils.Utils
import net.minecraft.server.level.ServerPlayer

class MetadataShift(
    type: ActionType = ActionType.METADATA_SHIFT,
    click: List<GenericClickType> = listOf(GenericClickType.ANY),
    delay: Long = 0,
    chance: Double = 0.0,
    requirements: RequirementOptions? = RequirementOptions(),
    private val key: String = "",
    private val amount: String = ""
) : Action(type, click, delay, chance, requirements) {
    override fun executeAction(player: ServerPlayer, gui: GenericGUI) {
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
            Utils.printError("[ACTION - ${type.name}] No existing metadata found for key '${key}'. Cannot shift non-existent metadata.")
            return
        }

        if (entry.type != MetadataType.INTEGER && entry.type != MetadataType.DOUBLE && entry.type != MetadataType.LONG) {
            Utils.printError("[ACTION - ${type.name}] Metadata for key '${key}' is type '${entry.type}', not BOOLEAN. Cannot toggle non-boolean metadata.")
            return
        }

        if (!entry.type.isValid(amount)) {
            Utils.printError("[ACTION - ${type.name}] Value '$amount' is not a valid ${entry.type.name} amount to shift by.")
            return
        }

        entry.value = when (entry.type) {
            MetadataType.INTEGER -> (entry.value as Int + (amount.toIntOrNull() ?: 0))
            MetadataType.LONG -> (entry.value as Long + (amount.toLongOrNull() ?: 0))
            MetadataType.DOUBLE -> (entry.value as Double + (amount.toDoubleOrNull() ?: 0.0))
            else -> entry.value
        }

        entry.value = !(entry.value as Boolean)
        userData.metdadata[key] = entry
        if (!storage.saveUser(player, userData)) {
            Utils.printError("[ACTION - ${type.name}] Failed to save metadata for player ${player.name.string}.")
            return
        }
    }

    override fun toString(): String {
        return "MetadataShift(click=$click, delay=$delay, chance=$chance, requirements=$requirements, " +
                "key=$key)"
    }
}
