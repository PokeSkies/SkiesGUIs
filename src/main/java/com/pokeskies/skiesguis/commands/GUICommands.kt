package com.pokeskies.skiesguis.commands

import com.mojang.brigadier.CommandDispatcher
import com.pokeskies.skiesguis.config.ConfigManager
import me.lucko.fabric.api.permissions.v0.Permissions
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource

class GUICommands {
    companion object {
        fun register(dispatcher: CommandDispatcher<ServerCommandSource?>) {
            for ((id, gui) in ConfigManager.GUIS) {
                for (command in gui.aliasCommands) {
                    dispatcher.register(CommandManager.literal(command)
                        .requires { obj: ServerCommandSource -> obj.isExecutedByPlayer }
                        .requires(Permissions.require("skiesguis.open.$id"))
                        .executes { ctx ->
                            val player = ctx.source.player
                            if (player == null) {
                                ctx.source.sendMessage(
                                    Component.text("Must be a player to run this command!")
                                        .color(NamedTextColor.RED)
                                )
                                return@executes 1
                            }
                            if (!Permissions.check(player, "skiesguis.open.$id")) {
                                ctx.source.sendMessage(
                                    Component.text("You don't have permission to run this command!")
                                        .color(NamedTextColor.RED)
                                )
                                return@executes 1
                            }
                            gui.openGUI(player)
                            return@executes 1
                        }
                    )
                }
            }
        }
    }
}