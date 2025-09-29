package com.pokeskies.skiesguis.config.tooltips

import com.bedrockk.molang.runtime.MoLangRuntime
import com.bedrockk.molang.runtime.struct.QueryStruct
import com.cobblemon.mod.common.api.molang.MoLangFunctions.addFunctions
import com.cobblemon.mod.common.api.molang.MoLangFunctions.asMoLangValue
import com.cobblemon.mod.common.api.molang.MoLangFunctions.setup
import com.cobblemon.mod.common.util.asExpressionLike
import com.cobblemon.mod.common.util.resolveBoolean
import com.cobblemon.mod.common.util.resolveString
import com.pokeskies.skiesguis.utils.parseToNative
import net.minecraft.network.chat.MutableComponent
import net.minecraft.server.level.ServerPlayer

class TooltipBuilder(private val tooltipConfig: TooltipConfig? = null) {
    private val runtime = MoLangRuntime().setup()
    private val queryStruct = runtime.environment.getStruct("query") as QueryStruct


    fun buildTooltip(player: ServerPlayer): List<MutableComponent> {
        queryStruct.addFunctions(
            mapOf(
                "player" to java.util.function.Function { player.asMoLangValue() }
            )
        )
        val conditionResult = tooltipConfig?.condition?.let {
            runtime.resolveBoolean(it.asExpressionLike())
        } ?: true

        return if (conditionResult) {
            tooltipConfig?.successScript?.let { generateTooltip(it) } ?: emptyList()
        } else {
            tooltipConfig?.failScript?.let { generateTooltip(it) } ?: emptyList()
        }
    }

    private fun generateTooltip(script: List<String>): List<MutableComponent> {
        return if (script.isNotEmpty()) {
            val tooltip = mutableListOf<MutableComponent>()
            script.forEach { line ->
                val resolvedLine = runtime.resolveString(line.asExpressionLike())
                tooltip.add(resolvedLine.parseToNative() as MutableComponent)
            }
            tooltip
        } else {
            emptyList()
        }
    }
}

data class TooltipConfig(
    val condition: String = "1.0;",
    val successScript: List<String> = emptyList(),
    val failScript: List<String> = emptyList()
) {
    companion object {
        val TOOLTIPS: MutableMap<String, TooltipConfig> = mutableMapOf()
    }
}
