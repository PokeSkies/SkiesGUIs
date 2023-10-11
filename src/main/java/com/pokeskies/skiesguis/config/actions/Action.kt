package com.pokeskies.skiesguis.config.actions

import ca.landonjw.gooeylibs2.api.button.ButtonClick
import ca.landonjw.gooeylibs2.api.tasks.Task
import com.pokeskies.skiesguis.config.requirements.RequirementOptions
import com.pokeskies.skiesguis.utils.Utils
import net.minecraft.server.network.ServerPlayerEntity
import kotlin.random.Random

abstract class Action(
    val type: ActionType,
    val click: ClickType = ClickType.ANY,
    val delay: Long = 0,
    val chance: Double = 0.0,
    val requirements: RequirementOptions? = RequirementOptions()
) {
    // Will do a chance check and then apply any delay
    open fun attemptExecution(player: ServerPlayerEntity) {
        if (chance > 0.0 && chance < 1.0) {
            val roll = Random.nextFloat()
            Utils.debug("Attempting chance roll for $type Action. Result is: $roll <= $chance = ${roll <= chance}.")
            if (roll > chance) {
                Utils.debug("Failed chance roll for $type Action.")
                return
            }
        }

        if (delay <= 0) {
            execute(player)
            return
        }
        Utils.debug("Delay found for $type Action. Waiting $delay ticks before execution.")

        Task.builder()
            .execute { task -> execute(player) }
            .delay(delay)
            .build()
    }

    abstract fun execute(player: ServerPlayerEntity)

    fun matchesClick(buttonClick: ButtonClick): Boolean {
        return click.buttonClicks.contains(buttonClick)
    }

    fun checkClickRequirements(player: ServerPlayerEntity): Boolean {
        if (requirements != null) {
            for (requirement in requirements.requirements) {
                if (!requirement.value.check(player)) {
                    return false
                }
            }
        }
        return true
    }

    fun executeDenyActions(player: ServerPlayerEntity) {
        if (requirements != null) {
            for ((id, action) in requirements.denyActions) {
                action.attemptExecution(player)
            }
        }
    }

    fun executeSuccessActions(player: ServerPlayerEntity) {
        if (requirements != null) {
            for ((id, action) in requirements.successActions) {
                action.attemptExecution(player)
            }
        }
    }

    fun parsePlaceholders(player: ServerPlayerEntity, value: String): String {
        return value.replace("%player%", player.name.string)
    }

    override fun toString(): String {
        return "Action(type=$type, click=$click, requirements=$requirements)"
    }
}