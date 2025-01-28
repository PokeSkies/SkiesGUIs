package com.pokeskies.skiesguis.commands

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.tree.LiteralCommandNode
import com.pokeskies.skiesguis.commands.subcommands.DebugCommand
import com.pokeskies.skiesguis.commands.subcommands.OpenCommand
import com.pokeskies.skiesguis.commands.subcommands.PrintNBTCommand
import com.pokeskies.skiesguis.commands.subcommands.ReloadCommand
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands

class BaseCommands {
    private val aliases = listOf("skiesguis", "guis", "gui", "sg")

    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        val rootCommands: List<LiteralCommandNode<CommandSourceStack>> = aliases.map {
            Commands.literal(it).build()
        }

        val subCommands: List<LiteralCommandNode<CommandSourceStack>> = listOf(
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
