package com.pokeskies.skiesguis.config.requirements

import com.mojang.datafixers.Products
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import com.pokeskies.skiesguis.SkiesGUIs
import com.pokeskies.skiesguis.utils.Utils
import com.pokeskies.skiesguis.utils.optionalRecordCodec
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.StringIdentifiable

abstract class Requirement(
    val type: RequirementType? = null,
    val comparison: ComparisonType = ComparisonType.EQUALS
) {
    abstract fun check(player: ServerPlayerEntity): Boolean

    open fun getAllowedComparisons(): List<ComparisonType> {
        return emptyList()
    }

    fun parsePlaceholders(player: ServerPlayerEntity, value: String): String {
        return value.replace("%player%", player.name.string)
    }

    fun checkComparison(): Boolean {
        if (!getAllowedComparisons().contains(comparison)) {
            Utils.error("Error while executing a Requirement check! Comparison ${comparison.identifier} is not allowed: ${getAllowedComparisons().map { it.identifier }}")
            return false
        }
        return true
    }

    override fun toString(): String {
        return "Requirement(type=$type, comparison=$comparison)"
    }
}