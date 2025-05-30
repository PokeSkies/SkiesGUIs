package com.pokeskies.skiesguis.config.actions

import ca.landonjw.gooeylibs2.api.button.ButtonClick
import ca.landonjw.gooeylibs2.api.tasks.Task
import com.google.gson.annotations.JsonAdapter
import com.pokeskies.skiesguis.config.requirements.RequirementOptions
import com.pokeskies.skiesguis.utils.FlexibleListAdaptorFactory
import com.pokeskies.skiesguis.utils.Utils
import net.minecraft.server.level.ServerPlayer
import kotlin.random.Random

abstract class Action(
    val type: ActionType,
    @JsonAdapter(FlexibleListAdaptorFactory::class)
    val click: List<ClickType> = listOf(ClickType.ANY),
    val delay: Long = 0,
    val chance: Double = 0.0,
    val requirements: RequirementOptions? = RequirementOptions()
) {
    // Will do a chance check and then apply any delay
    open fun attemptExecution(player: ServerPlayer) {
        if (chance > 0.0 && chance < 1.0) {
            val roll = Random.nextFloat()
            Utils.printDebug("Attempting chance roll for $type Action. Result is: $roll <= $chance = ${roll <= chance}.")
            if (roll > chance) {
                Utils.printDebug("Failed chance roll for $type Action.")
                return
            }
        }

        if (delay <= 0) {
            executeAction(player)
            return
        }
        Utils.printDebug("Delay found for $type Action. Waiting $delay ticks before execution.")

        Task.builder()
            .execute { task ->
                player.server.executeIfPossible {
                    executeAction(player)
                }
            }
            .delay(delay)
            .build()
    }

    abstract fun executeAction(player: ServerPlayer)

    fun matchesClick(buttonClick: ButtonClick): Boolean {
        return click.any { it.buttonClicks.contains(buttonClick) }
    }

    override fun toString(): String {
        return "Action(type=$type, click=$click, requirements=$requirements)"
    }
}
