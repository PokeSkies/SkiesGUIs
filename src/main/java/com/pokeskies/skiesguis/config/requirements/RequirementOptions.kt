package com.pokeskies.skiesguis.config.requirements

import com.google.gson.annotations.SerializedName
import com.pokeskies.skiesguis.config.actions.Action
import net.minecraft.server.level.ServerPlayer

class RequirementOptions(
    val requirements: Map<String, Requirement> = emptyMap(),
    @SerializedName("deny_actions")
    val denyActions: Map<String, Action> = emptyMap(),
    @SerializedName("success_actions")
    val successActions: Map<String, Action> = emptyMap(),
    @SerializedName("minimum_requirements")
    val minimumRequirements: Int? = null,
    @SerializedName("stop_at_success")
    val stopAtSuccess: Boolean = false
) {
    fun checkRequirements(player: ServerPlayer): Boolean {
        var successes = 0
        for (requirement in requirements) {
            if (requirement.value.checkRequirements(player)) {
                successes++
                if (minimumRequirements != null && stopAtSuccess && successes >= minimumRequirements) {
                    return true
                }
            }
        }
        return if (minimumRequirements == null) successes == requirements.size else successes >= minimumRequirements
    }

    fun executeDenyActions(player: ServerPlayer) {
        for ((id, action) in denyActions) {
            action.attemptExecution(player)
        }
    }

    fun executeSuccessActions(player: ServerPlayer) {
        for ((id, action) in successActions) {
            action.attemptExecution(player)
        }
    }
    override fun toString(): String {
        return "RequirementOptions(requirements=$requirements, denyActions=$denyActions, successActions=$successActions)"
    }
}
