package com.pokeskies.skiesguis.config.requirements.types.internal

import com.google.gson.annotations.SerializedName
import com.pokeskies.skiesguis.SkiesGUIs
import com.pokeskies.skiesguis.config.requirements.ComparisonType
import com.pokeskies.skiesguis.config.requirements.Requirement
import com.pokeskies.skiesguis.config.requirements.RequirementType
import com.pokeskies.skiesguis.data.MetadataType
import com.pokeskies.skiesguis.gui.GenericGUI
import com.pokeskies.skiesguis.utils.Utils
import net.minecraft.server.level.ServerPlayer

class MetadataRequirement(
    type: RequirementType = RequirementType.PLACEHOLDER,
    comparison: ComparisonType = ComparisonType.EQUALS,
    @SerializedName("meta_type")
    private val metaType: MetadataType? = null,
    private val key: String = "",
    private val value: String = ""
) : Requirement(type, comparison) {
    override fun checkRequirements(player: ServerPlayer, gui: GenericGUI): Boolean {
        if (!checkComparison()) return false

        Utils.printDebug("[REQUIREMENT - ${type?.name}] Player(${player.gameProfile.name}), Metadata Type($metaType): $this")

        val storage = SkiesGUIs.INSTANCE.storage ?: run {
            Utils.printError("[REQUIREMENT - ${type?.name}] The storage system is not initialized!")
            return false
        }

        if (metaType == null) {
            Utils.printError("[REQUIREMENT - ${type?.name}] The metadata type provided is not valid! Valid types are: ${MetadataType.entries}")
            return false
        }

        if (key.isEmpty()) {
            Utils.printError("[REQUIREMENT - ${type?.name}] The key '${key}' is null or empty! Could not check metadata.")
            return false
        }

        if (value.isEmpty()) {
            Utils.printError("[REQUIREMENT - ${type?.name}] The value '${value}' is null or empty! Could not check metadata.")
            return false
        }

        val (expected, error) = metaType.parseString(value)
        if (expected == null) {
            Utils.printError("[REQUIREMENT - ${type?.name}] Failed to parse value '$value' as type ${metaType.name}: $error")
            return false
        }

        val userData = storage.getUser(player)

        val entry = userData.metdadata[key]

        if (entry == null) {
            Utils.printDebug("[REQUIREMENT - ${type?.name}] No metadata found for key '$key' for player ${player.name.string}.")
            return false
        }

        if (entry.type != metaType) {
            Utils.printDebug("[REQUIREMENT - ${type?.name}] Metadata type mismatch for key '$key' for ${player.name.string}. Expected: ${metaType.name}, Found: ${entry.type.name}.")
            return false
        }

        if (entry.value == null) {
            Utils.printDebug("[REQUIREMENT - ${type?.name}] Saved metadata '$key' returned null for ${player.name.string}.")
            return false
        }

        val actual = entry.value!!

        val result = when (metaType) {
            MetadataType.STRING -> {
                val exp = expected as String
                val act = actual as String
                when (comparison) {
                    ComparisonType.EQUALS -> act == exp
                    ComparisonType.NOT_EQUALS -> act != exp
                    else -> {
                        // fallback to lexicographic comparison for inequality operators
                        val lex = act.compareTo(exp)
                        when (comparison) {
                            ComparisonType.GREATER_THAN -> lex > 0
                            ComparisonType.GREATER_THAN_OR_EQUALS -> lex >= 0
                            ComparisonType.LESS_THAN -> lex < 0
                            ComparisonType.LESS_THAN_OR_EQUALS -> lex <= 0
                            else -> {
                                Utils.printError("[REQUIREMENT - ${type?.name}] Unsupported comparison '$comparison' for STRING metadata.")
                                false
                            }
                        }
                    }
                }
            }
            MetadataType.INTEGER, MetadataType.DOUBLE, MetadataType.LONG -> {
                val expNum = asDouble(expected)
                val actNum = asDouble(actual)
                if (expNum == null || actNum == null) {
                    Utils.printError("[REQUIREMENT - ${type?.name}] Could not convert values to numbers for comparison on key '$key' for player ${player.name.string}.")
                    return false
                }
                when (comparison) {
                    ComparisonType.EQUALS -> actNum == expNum
                    ComparisonType.NOT_EQUALS -> actNum != expNum
                    ComparisonType.GREATER_THAN -> actNum > expNum
                    ComparisonType.GREATER_THAN_OR_EQUALS -> actNum >= expNum
                    ComparisonType.LESS_THAN -> actNum < expNum
                    ComparisonType.LESS_THAN_OR_EQUALS -> actNum <= expNum
                }
            }
            MetadataType.BOOLEAN -> {
                val expB = when (expected) {
                    is Boolean -> expected
                    is String -> expected.toBoolean()
                    else -> false
                }
                val actB = when (actual) {
                    is Boolean -> actual
                    is String -> actual.toBoolean()
                    else -> false
                }
                when (comparison) {
                    ComparisonType.EQUALS -> actB == expB
                    ComparisonType.NOT_EQUALS -> actB != expB
                    else -> {
                        Utils.printError("[REQUIREMENT - ${type?.name}] Unsupported comparison '$comparison' for BOOLEAN metadata.")
                        false
                    }
                }
            }
        }

        if (!result) {
            Utils.printDebug("[REQUIREMENT - ${type?.name}] Comparison failed for key '$key'. Expected: $expected, Actual: $actual, Comparison: $comparison")
        } else {
            Utils.printDebug("[REQUIREMENT - ${type?.name}] Comparison succeeded for key '$key'.")
        }

        return result
    }

    override fun allowedComparisons(): List<ComparisonType> {
        return ComparisonType.entries
    }

    override fun toString(): String {
        return "MetadataRequirement(comparison=$comparison, meta_type=$metaType, key='$key', value='$value')"
    }

    fun asDouble(v: Any): Double? = when (v) {
        is Number -> v.toDouble()
        is String -> v.toDoubleOrNull()
        else -> null
    }
}
