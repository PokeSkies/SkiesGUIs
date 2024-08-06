package com.pokeskies.skiesguis.commands.subcommands

import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.LiteralCommandNode
import com.mojang.serialization.JsonOps
import com.pokeskies.skiesguis.SkiesGUIs
import com.pokeskies.skiesguis.utils.SubCommand
import me.lucko.fabric.api.permissions.v0.Permissions
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.NamedTextColor
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.InteractionHand

class PrintNBTCommand : SubCommand {
    override fun build(): LiteralCommandNode<CommandSourceStack> {
        return Commands.literal("printnbt")
            .requires(Permissions.require("skiesguis.command.printnbt", 2))
            .executes(Companion::printnbt)
            .build()
    }

    companion object {
        fun printnbt(ctx: CommandContext<CommandSourceStack>): Int {
            val player = ctx.source.player
            if (player != null) {
                val mainHand = player.getItemInHand(InteractionHand.MAIN_HAND)
                if (mainHand.isEmpty) {
                    player.sendMessage(Component.text("Hold something in your Main Hand to view it's NBT Data!").color(NamedTextColor.RED))
                    return 1
                }

                val nbt = mainHand.nbt
                if (nbt == null) {
                    player.sendMessage(Component.text("This item has no NBT Data!").color(NamedTextColor.RED))
                    return 1
                }

                val result = JsonOps.INSTANCE.withEncoder(CompoundTag.CODEC)
                    .apply(nbt)
                    .result()
                if (result.isEmpty) {
                    player.sendMessage(Component.text("There was an error while encoding this item's NBT!").color(NamedTextColor.RED))
                    return 1
                }

                val jsonOutput = SkiesGUIs.INSTANCE.configManager.gson.toJson(result.get())

                val builder: TextComponent.Builder = Component.text()
                jsonOutput.split("\n").forEach { builder.append(Component.text(it)).appendNewline() }

                player.sendMessage(
                    Component.text("Click to copy the NBT of ")
                        .append(mainHand.item.getName(mainHand))
                        .color(NamedTextColor.GREEN)
                        .hoverEvent(HoverEvent.showText(builder.build()))
                        .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, jsonOutput))
                )
            }
            return 1
        }
    }
}
