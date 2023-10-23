package com.pokeskies.skiesguis.commands

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.serialization.JsonOps
import com.pokeskies.skiesguis.SkiesGUIs
import com.pokeskies.skiesguis.config.ConfigManager
import com.pokeskies.skiesguis.utils.Utils
import me.lucko.fabric.api.permissions.v0.Permissions
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.NamedTextColor
import net.minecraft.command.CommandSource
import net.minecraft.command.argument.EntityArgumentType
import net.minecraft.nbt.NbtCompound
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.util.Hand

class BaseCommands {
    companion object {
        /**
         * /skiesguis open <gui_id> [player] - open a GUI by ID for yourself or for another player
         * /skiesguis reload - reload the mod
         */
        fun register(dispatcher: CommandDispatcher<ServerCommandSource?>) {
            for (command in listOf("skiesguis", "guis", "gui", "sg")) {
                dispatcher.register(CommandManager.literal(command)
                    .then(CommandManager.literal("reload")
                        .requires(Permissions.require("skiesguis.command.reload", 4))
                        .executes(BaseCommands::reload)
                    )
                    .then(CommandManager.literal("printnbt")
                        .requires { obj: ServerCommandSource -> obj.isExecutedByPlayer }
                        .requires(Permissions.require("skiesguis.command.printnbt", 4))
                        .executes(BaseCommands::printNBT)
                    )
                    .then(CommandManager.literal("debug")
                        .requires(Permissions.require("skiesguis.command.debug", 4))
                        .executes(BaseCommands::debug)
                    )
                    .then(CommandManager.literal("open")
                        .then(CommandManager.argument("gui_id", StringArgumentType.string())
                            .suggests { _, builder ->
                                CommandSource.suggestMatching(ConfigManager.GUIS.keys.stream(), builder)
                            }
                            .then(CommandManager.argument("player", EntityArgumentType.player())
                                .requires(Permissions.require("skiesguis.command.open", 4))
                                .executes(BaseCommands::openGUIPlayer)
                            )
                            .requires { obj: ServerCommandSource -> obj.isExecutedByPlayer }
                            .requires(Permissions.require("skiesguis.command.open", 4))
                            .executes(BaseCommands::openGUISelf)
                        )
                    )
                )
            }
        }

        private fun reload(ctx: CommandContext<ServerCommandSource>): Int {
            SkiesGUIs.INSTANCE.reload()
            ctx.source.sendMessage(Utils.deserializeText("<green>Reloaded SkiesGUIs"))
            return 1
        }

        private fun printNBT(ctx: CommandContext<ServerCommandSource>): Int {
            val player = ctx.source.player
            if (player != null) {
                val mainHand = player.getStackInHand(Hand.MAIN_HAND)
                if (mainHand.isEmpty) {
                    player.sendMessage(Component.text("Hold something in your Main Hand to view it's NBT Data!").color(NamedTextColor.RED))
                    return 1
                }

                val nbt = mainHand.nbt
                if (nbt == null) {
                    player.sendMessage(Component.text("This item has no NBT Data!").color(NamedTextColor.RED))
                    return 1
                }

                val result = JsonOps.INSTANCE.withEncoder(NbtCompound.CODEC)
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
                        .append(mainHand.item.name.asComponent())
                        .color(NamedTextColor.GREEN)
                        .hoverEvent(HoverEvent.showText(builder.build()))
                        .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, jsonOutput))
                )
            }
            return 1
        }

        private fun debug(ctx: CommandContext<ServerCommandSource>): Int {
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

                    gui.openGUI(player)
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

            gui.openGUI(player)
            return 1
        }
    }
}