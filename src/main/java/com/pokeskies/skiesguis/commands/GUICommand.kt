package com.pokeskies.skiesguis.commands

import ca.landonjw.gooeylibs2.api.UIManager
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.pokeskies.skiesguis.SkiesGUIs
import com.pokeskies.skiesguis.config.ConfigManager
import com.pokeskies.skiesguis.gui.ChestGUI
import com.pokeskies.skiesguis.utils.Utils
import me.lucko.fabric.api.permissions.v0.Permissions
import net.minecraft.command.CommandSource
import net.minecraft.command.argument.EntityArgumentType
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource

class GUICommand {
    companion object {
        /**
         * /skiesguis open <gui_id> [player] - open a GUI by ID for yourself or for another player
         * /skiesguis reload - reload the mod
         */
        fun register(dispatcher: CommandDispatcher<ServerCommandSource?>) {
            for (command in listOf("skiesguis", "guis", "gui", "sg")) {
                dispatcher.register(CommandManager.literal(command)
                    .then(
                        CommandManager.literal("reload")
                            .requires(Permissions.require("skiesguis.command.reload", 4))
                            .executes(GUICommand::reload)
                    )
                    .then(CommandManager.literal("open")
                        .requires(Permissions.require("skiesguis.command.open", 4))
                        .then(CommandManager.argument("gui_id", StringArgumentType.string())
                            .suggests { _, builder ->
                                CommandSource.suggestMatching(ConfigManager.GUIS.keys.stream(), builder)
                            }
                            .then(
                                CommandManager.argument("player", EntityArgumentType.player())
                                    .executes(GUICommand::openGUIPlayer)
                            )
                            .requires { obj: ServerCommandSource -> obj.isExecutedByPlayer }
                            .executes(GUICommand::openGUISelf)
                        )
                    )
                )
            }
        }

        private fun reload(ctx: CommandContext<ServerCommandSource>): Int {
            SkiesGUIs.INSTANCE.reload()
            ctx.source.sendMessage(Utils.deseralizeText("<green>Reloaded SkiesGUIs"))
            return 1
        }

        private fun openGUISelf(ctx: CommandContext<ServerCommandSource>): Int {
            try {
                val player = ctx.source.player
                if (player != null) {
                    val guiID = StringArgumentType.getString(ctx, "gui_id")

                    val guiConfig = ConfigManager.GUIS[guiID]
                    if (guiConfig == null) {
                        ctx.source.sendMessage(
                            Utils.deseralizeText("<red>Could not find a GUI with the ID $guiID!")
                        )
                        return 1
                    }

                    UIManager.openUIForcefully(player, ChestGUI(player, guiID, guiConfig))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return 1
        }

        private fun openGUIPlayer(ctx: CommandContext<ServerCommandSource>): Int {
            val player = EntityArgumentType.getPlayer(ctx, "player")
            val guiID = StringArgumentType.getString(ctx, "gui_id")

            val guiConfig = ConfigManager.GUIS[guiID]
            if (guiConfig == null) {
                ctx.source.sendMessage(Utils.deseralizeText("<red>Could not find a GUI with the ID $guiID!"))
                return 1
            }

            UIManager.openUIForcefully(player, ChestGUI(player, guiID, guiConfig))
            return 1
        }
    }
}