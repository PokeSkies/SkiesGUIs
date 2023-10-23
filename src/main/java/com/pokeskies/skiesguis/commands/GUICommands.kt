package com.pokeskies.skiesguis.commands

import com.mojang.brigadier.CommandDispatcher
import com.pokeskies.skiesguis.config.ConfigManager
import com.pokeskies.skiesguis.utils.Utils
import me.lucko.fabric.api.permissions.v0.Permissions
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource

class GUICommands {
    fun register(dispatcher: CommandDispatcher<ServerCommandSource?>) {
        for (guiEntry in ConfigManager.GUIS) {
            for (command in guiEntry.value.aliasCommands) {
                dispatcher.register(CommandManager.literal(command)
                    .requires { obj: ServerCommandSource -> obj.isExecutedByPlayer }
                    .requires(Permissions.require("skiesguis.open.${guiEntry.key}"))
                    .executes { ctx ->
                        val player = ctx.source.player
                        if (player == null) {
                            ctx.source.sendMessage(
                                Component.text("Must be a player to run this command!")
                                    .color(NamedTextColor.RED)
                            )
                            return@executes 1
                        }
                        if (!Permissions.check(player, "skiesguis.open.${guiEntry.key}")) {
                            ctx.source.sendMessage(
                                Component.text("You don't have permission to run this command!")
                                    .color(NamedTextColor.RED)
                            )
                            return@executes 1
                        }

                        val gui = ConfigManager.GUIS[guiEntry.key]

                        if (gui == null) {
                            Utils.printError("There was an error while running the command '$command' for player '${player.name.string}'! " +
                                    "The GUI '${guiEntry.key}' returned null. Was it deleted, renamed, or changed?")
                            ctx.source.sendMessage(
                                Component.text("Error while attempting to open this GUI! Check the console for more information.")
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