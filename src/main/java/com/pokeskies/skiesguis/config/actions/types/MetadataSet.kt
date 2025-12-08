package com.pokeskies.skiesguis.config.actions.types

import com.google.gson.annotations.SerializedName
import com.pokeskies.skiesguis.SkiesGUIs
import com.pokeskies.skiesguis.config.actions.Action
import com.pokeskies.skiesguis.config.actions.ActionType
import com.pokeskies.skiesguis.config.actions.GenericClickType
import com.pokeskies.skiesguis.config.requirements.RequirementOptions
import com.pokeskies.skiesguis.data.MetadataType
import com.pokeskies.skiesguis.data.MetadataValue
import com.pokeskies.skiesguis.gui.ChestGUI
import com.pokeskies.skiesguis.utils.Utils
import net.minecraft.server.level.ServerPlayer

class MetadataSet(
    type: ActionType = ActionType.METADATA_SET,
    click: List<GenericClickType> = listOf(GenericClickType.ANY),
    delay: Long = 0,
    chance: Double = 0.0,
    requirements: RequirementOptions? = RequirementOptions(),
    @SerializedName("meta_type")
    private val metaType: MetadataType? = null,
    private val key: String = "",
    private val value: String = ""
) : Action(type, click, delay, chance, requirements) {
    override fun executeAction(player: ServerPlayer, gui: ChestGUI) {
        Utils.printDebug("[ACTION - ${type.name}] Player(${player.gameProfile.name}): $this")

        val storage = SkiesGUIs.INSTANCE.storage ?: run {
            Utils.printError("[ACTION - ${type.name}] The storage system is not initialized!")
            return
        }

        if (metaType == null) {
            Utils.printError("[ACTION - ${type.name}] The metadata type provided is not valid! Valid types are: ${MetadataType.entries}")
            return
        }

        if (key.isEmpty()) {
            Utils.printError("[ACTION - ${type.name}] The key '${key}' is null or empty! Could not set metadata.")
            return
        }

        if (value.isEmpty()) {
            Utils.printError("[ACTION - ${type.name}] The value '${value}' is null or empty! Could not set metadata.")
            return
        }

        val (any, error) = metaType.parseString(value)
        if (any == null) {
            Utils.printError("[ACTION - ${type.name}] Failed to parse value '$value' as type ${metaType.name}: $error")
            return
        }

        val userData = storage.getUser(player)

        userData.metdadata[key] = MetadataValue(metaType, any)
        if (!storage.saveUser(player, userData)) {
            Utils.printError("[ACTION - ${type.name}] Failed to save metadata for player ${player.name.string}.")
            return
        }
    }

    override fun toString(): String {
        return "MetadataSet(click=$click, delay=$delay, chance=$chance, requirements=$requirements, " +
                "meta_type=$metaType, key=$key, value=$value)"
    }
}
