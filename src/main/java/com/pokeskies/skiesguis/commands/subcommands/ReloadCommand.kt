package com.pokeskies.skiesguis.commands.subcommands

import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.LiteralCommandNode
import com.pokeskies.skiesguis.SkiesGUIs
import com.pokeskies.skiesguis.utils.SubCommand
import com.pokeskies.skiesguis.utils.Utils
import me.lucko.fabric.api.permissions.v0.Permissions
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource

class ReloadCommand : SubCommand {
    override fun build(): LiteralCommandNode<ServerCommandSource> {
        return CommandManager.literal("reload")
            .requires(Permissions.require("skiesguis.command.reload", 2))
            .executes(Companion::reload)
            .build()
    }

    companion object {
        fun reload(ctx: CommandContext<ServerCommandSource>): Int {
            SkiesGUIs.INSTANCE.reload()
            ctx.source.sendMessage(Utils.deserializeText("<green>Reloaded SkiesGUIs"))
            return 1
        }
    }
}