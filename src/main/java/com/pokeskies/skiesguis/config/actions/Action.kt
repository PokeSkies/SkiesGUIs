package com.pokeskies.skiesguis.config.actions

import ca.landonjw.gooeylibs2.api.button.ButtonClick
import com.mojang.datafixers.Products
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import com.pokeskies.skiesguis.config.requirements.ClickRequirement
import com.pokeskies.skiesguis.utils.optionalRecordCodec
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.StringIdentifiable
import java.util.*

abstract class Action(
    val click: ClickType,
    val clickRequirements: Optional<ClickRequirement>
) {
    companion object {
        val CODEC: Codec<Action> = ActionType.CODEC.dispatch("type", { it.getType() }, { it.codec })

        fun <T : Action> actionCodec(instance: RecordCodecBuilder.Instance<T>): Products.P2<RecordCodecBuilder.Mu<T>, ClickType, Optional<ClickRequirement>> =
            instance.group(
                StringIdentifiable.createCodec { ClickType.values() }
                    .optionalRecordCodec("click", Action::click, ClickType.ANY),
                ClickRequirement.CODEC.optionalFieldOf("click_requirements").forGetter { it.clickRequirements },
            )
    }

    abstract fun execute(player: ServerPlayerEntity)

    abstract fun getType(): ActionType<*>

    fun matchesClick(buttonClick: ButtonClick): Boolean {
        return click.buttonClicks.contains(buttonClick)
    }

    fun checkClickRequirements(player: ServerPlayerEntity): Boolean {
        if (clickRequirements.isPresent) {
            for (requirement in clickRequirements.get().requirements) {
                if (!requirement.value.check(player)) {
                    return false
                }
            }
        }
        return true
    }

    fun executeDenyActions(player: ServerPlayerEntity) {
        if (clickRequirements.isPresent) {
            for ((id, action) in clickRequirements.get().denyActions) {
                action.execute(player)
            }
        }
    }

    fun executeSuccessActions(player: ServerPlayerEntity) {
        if (clickRequirements.isPresent) {
            for ((id, action) in clickRequirements.get().successActions) {
                action.execute(player)
            }
        }
    }

    fun parsePlaceholders(player: ServerPlayerEntity, value: String): String {
        return value.replace("%player%", player.name.string)
    }

    override fun toString(): String {
        return "Action(click=$click, clickRequirements=$clickRequirements)"
    }
}