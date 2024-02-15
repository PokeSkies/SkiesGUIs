package com.pokeskies.skiesguis.commands.subcommands

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.LiteralCommandNode
import com.pokeskies.skiesguis.config.ConfigManager
import com.pokeskies.skiesguis.utils.SubCommand
import com.pokeskies.skiesguis.utils.Utils
import me.lucko.fabric.api.permissions.v0.Permissions
import net.minecraft.command.CommandSource
import net.minecraft.command.argument.EntityArgumentType
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource

class OpenCommand : SubCommand {
    override fun build(): LiteralCommandNode<ServerCommandSource> {
        return CommandManager.literal("open")
            .requires(Permissions.require("skiesguis.command.open", 1))
            .then(CommandManager.argument("gui_id", StringArgumentType.string())
                .suggests { _, builder ->
                    CommandSource.suggestMatching(ConfigManager.GUIS.keys.stream(), builder)
                }
                .then(CommandManager.argument("player", EntityArgumentType.player())
                    .executes(Companion::openGUIPlayer)
                )
                .executes(Companion::openGUISelf)
            )
            .build()
    }

    companion object {
        private fun openGUISelf(ctx: CommandContext<ServerCommandSource>): Int {
            try {
                val player = ctx.source.player
                if (player != null) {
                    val guiID = StringArgumentType.getString(ctx, "gui_id")

                    val gui = ConfigManager.GUIS[guiID]
                    if (gui == null) {
                        ctx.source.sendMessage(
                            Utils.deserializeText("<red>Could not find a GUI with the ID $guiID!")
                        )
                        return 1
                    }

                    gui.openGUI(player, guiID)
                } else {
                    ctx.source.sendMessage(Utils.deserializeText("<red>Please provide a player argument!"))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return 1
        }

        private fun openGUIPlayer(ctx: CommandContext<ServerCommandSource>): Int {
            val player = EntityArgumentType.getPlayer(ctx, "player")
            val guiID = StringArgumentType.getString(ctx, "gui_id")

            val gui = ConfigManager.GUIS[guiID]
            if (gui == null) {
                ctx.source.sendMessage(Utils.deserializeText("<red>Could not find a GUI with the ID $guiID!"))
                return 1
            }

            gui.openGUI(player, guiID)
            return 1
        }
    }
}