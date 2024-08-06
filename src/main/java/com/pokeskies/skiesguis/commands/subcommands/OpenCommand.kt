package com.pokeskies.skiesguis.commands.subcommands

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.LiteralCommandNode
import com.pokeskies.skiesguis.config.ConfigManager
import com.pokeskies.skiesguis.utils.SubCommand
import com.pokeskies.skiesguis.utils.Utils
import me.lucko.fabric.api.permissions.v0.Permissions
import net.minecraft.commands.CommandSource
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.SharedSuggestionProvider
import net.minecraft.commands.arguments.EntityArgument

class OpenCommand : SubCommand {
    override fun build(): LiteralCommandNode<CommandSourceStack> {
        return Commands.literal("open")
            .requires(Permissions.require("skiesguis.command.open", 2))
            .then(Commands.argument("gui_id", StringArgumentType.string())
                .suggests { _, builder ->
                    SharedSuggestionProvider.suggest(ConfigManager.GUIS.keys.stream(), builder)
                }
                .then(
                    Commands.argument("player", EntityArgument.player())
                    .requires(Permissions.require("skiesguis.command.open.others", 2))
                    .executes(Companion::openGUIPlayer)
                )
                .executes(Companion::openGUISelf)
            )
            .build()
    }

    companion object {
        private fun openGUISelf(ctx: CommandContext<CommandSourceStack>): Int {
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

        private fun openGUIPlayer(ctx: CommandContext<CommandSourceStack>): Int {
            val player = EntityArgument.getPlayer(ctx, "player")
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
