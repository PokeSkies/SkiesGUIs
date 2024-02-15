package com.pokeskies.skiesguis.commands.subcommands

import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.LiteralCommandNode
import com.pokeskies.skiesguis.SkiesGUIs
import com.pokeskies.skiesguis.utils.SubCommand
import me.lucko.fabric.api.permissions.v0.Permissions
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource

class DebugCommand : SubCommand {
    override fun build(): LiteralCommandNode<ServerCommandSource> {
        return CommandManager.literal("debug")
            .requires(Permissions.require("skiesguis.command.debug", 1))
            .executes(Companion::debug)
            .build()
    }

    companion object {
        fun debug(ctx: CommandContext<ServerCommandSource>): Int {
            val newMode = !SkiesGUIs.INSTANCE.configManager.config.debug
            SkiesGUIs.INSTANCE.configManager.config.debug = newMode
            SkiesGUIs.INSTANCE.configManager.saveFile("config.json", SkiesGUIs.INSTANCE.configManager.config)

            if (newMode) {
                ctx.source.sendMessage(Component.text("Debug mode has been enabled!").color(NamedTextColor.GREEN))
            } else {
                ctx.source.sendMessage(Component.text("Debug mode has been disabled!").color(NamedTextColor.RED))
            }
            return 1
        }
    }
}