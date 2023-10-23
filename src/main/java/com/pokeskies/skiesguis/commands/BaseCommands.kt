package com.pokeskies.skiesguis.commands

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.tree.LiteralCommandNode
import com.pokeskies.skiesguis.commands.subcommands.DebugCommand
import com.pokeskies.skiesguis.commands.subcommands.OpenCommand
import com.pokeskies.skiesguis.commands.subcommands.PrintNBTCommand
import com.pokeskies.skieskits.commands.subcommands.ReloadCommand
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource

class BaseCommands {
    private val aliases = listOf("skiesguis", "guis", "gui", "sg")

    fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        val rootCommands: List<LiteralCommandNode<ServerCommandSource>> = aliases.map {
            CommandManager.literal(it).build()
        }

        val subCommands: List<LiteralCommandNode<ServerCommandSource>> = listOf(
            OpenCommand().build(),
            ReloadCommand().build(),
            DebugCommand().build(),
            PrintNBTCommand().build(),
        )

        rootCommands.forEach { root ->
            subCommands.forEach { sub -> root.addChild(sub) }
            dispatcher.root.addChild(root)
        }
    }
}