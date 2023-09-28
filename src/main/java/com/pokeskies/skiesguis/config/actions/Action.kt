package com.pokeskies.skiesguis.config.actions

import ca.landonjw.gooeylibs2.api.button.ButtonClick
import com.mojang.datafixers.Products
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import com.pokeskies.skiesguis.utils.optionalRecordCodec
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.StringIdentifiable

abstract class Action(
    val click: ClickType
) {
    companion object {
        val CODEC: Codec<Action> = ActionType.CODEC.dispatch("type", { it.getType() }, { it.codec })

        fun <T : Action> actionCodec(instance: RecordCodecBuilder.Instance<T>): Products.P1<RecordCodecBuilder.Mu<T>, ClickType> =
            instance.group(
                StringIdentifiable.createCodec { ClickType.values() }
                    .optionalRecordCodec("click", Action::click, ClickType.ANY),
            )
    }

    abstract fun execute(player: ServerPlayerEntity)

    abstract fun getType(): ActionType<*>

    fun matchesClick(buttonClick: ButtonClick): Boolean {
        return click.buttonClicks.contains(buttonClick)
    }

    fun parsePlaceholders(player: ServerPlayerEntity, value: String): String {
        return value.replace("%player%", player.name.string)
    }
}