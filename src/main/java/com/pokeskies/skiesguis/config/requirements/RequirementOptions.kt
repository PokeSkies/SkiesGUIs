package com.pokeskies.skiesguis.config.requirements

import com.google.gson.annotations.SerializedName
import com.pokeskies.skiesguis.config.actions.Action
import net.minecraft.server.level.ServerPlayer

class RequirementOptions(
    val requirements: Map<String, Requirement> = emptyMap(),
    @SerializedName("deny_actions")
    val denyActions: Map<String, Action> = emptyMap(),
    @SerializedName("success_actions")
    val successActions: Map<String, Action> = emptyMap()
) {
    fun checkRequirements(player: ServerPlayer): Boolean {
        for (requirement in requirements) {
            if (!requirement.value.checkRequirements(player))
                return false
        }
        return true
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
