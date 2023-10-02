package com.pokeskies.skiesguis.config.requirements

import com.mojang.datafixers.Products
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import com.pokeskies.skiesguis.SkiesGUIs
import com.pokeskies.skiesguis.config.actions.Action
import com.pokeskies.skiesguis.config.actions.ClickType
import com.pokeskies.skiesguis.utils.optionalRecordCodec
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.StringIdentifiable
import java.util.*

abstract class Requirement(
    val comparison: ComparisonType
) {
    companion object {
        val CODEC: Codec<Requirement> = RequirementType.CODEC.dispatch("type", { it.getType() }, { it.codec })

        fun <T : Requirement> requirementCodec(instance: RecordCodecBuilder.Instance<T>): Products.P1<RecordCodecBuilder.Mu<T>, ComparisonType> =
            instance.group(
                StringIdentifiable.createCodec { ComparisonType.values() }
                    .optionalRecordCodec("comparison", Requirement::comparison, ComparisonType.EQUALS),
            )
    }

    abstract fun check(player: ServerPlayerEntity): Boolean

    abstract fun getType(): RequirementType<*>

    open fun getAllowedComparisons(): List<ComparisonType> {
        return emptyList()
    }

    fun parsePlaceholders(player: ServerPlayerEntity, value: String): String {
        return value.replace("%player%", player.name.string)
    }

    fun checkComparison(): Boolean {
        if (!getAllowedComparisons().contains(comparison)) {
            SkiesGUIs.LOGGER.error("Error while executing a Requirement check! Comparison ${comparison.identifier} is not allowed: ${getAllowedComparisons().map { it.identifier }}")
            return false
        }
        return true
    }
}