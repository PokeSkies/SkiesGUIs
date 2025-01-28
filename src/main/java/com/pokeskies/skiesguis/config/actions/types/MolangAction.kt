package com.pokeskies.skiesguis.config.actions.types

import ca.landonjw.gooeylibs2.api.container.GooeyContainer
import com.cobblemon.mod.common.util.asExpressionLike
import com.cobblemon.mod.common.util.resolve
import com.google.gson.annotations.JsonAdapter
import com.pokeskies.skiesguis.config.actions.Action
import com.pokeskies.skiesguis.config.actions.ActionType
import com.pokeskies.skiesguis.config.actions.ClickType
import com.pokeskies.skiesguis.config.requirements.RequirementOptions
import com.pokeskies.skiesguis.gui.ChestGUI
import com.pokeskies.skiesguis.utils.FlexibleListAdaptorFactory
import net.minecraft.server.level.ServerPlayer

class MolangAction(
    type: ActionType = ActionType.MOLANG,
    click: List<ClickType> = listOf(ClickType.ANY),
    delay: Long = 0,
    chance: Double = 0.0,
    requirements: RequirementOptions? = RequirementOptions(),
    @JsonAdapter(FlexibleListAdaptorFactory::class)
    val script: List<String> = listOf()
) : Action(
    type,
    click,
    delay,
    chance,
    requirements
) {
    override fun executeAction(player: ServerPlayer) {
        val screen = if (player.containerMenu is GooeyContainer && ((player.containerMenu!! as GooeyContainer).page is ChestGUI)) {
            (player.containerMenu!! as GooeyContainer).page as ChestGUI
        } else {
            return
        }
        screen.manager?.runtime?.resolve(script.asExpressionLike())
    }
}