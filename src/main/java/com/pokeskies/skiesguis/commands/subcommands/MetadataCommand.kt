package com.pokeskies.skiesguis.commands.subcommands

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.LiteralCommandNode
import com.pokeskies.skiesguis.SkiesGUIs
import com.pokeskies.skiesguis.data.MetadataType
import com.pokeskies.skiesguis.data.MetadataValue
import com.pokeskies.skiesguis.utils.SubCommand
import me.lucko.fabric.api.permissions.v0.Permissions
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.SharedSuggestionProvider
import net.minecraft.commands.arguments.EntityArgument
import net.minecraft.server.level.ServerPlayer

class MetadataCommand : SubCommand {
    override fun build(): LiteralCommandNode<CommandSourceStack> {
        return Commands.literal("metadata")
            .requires(Permissions.require("skiesguis.command.metadata", 2))
            .then(Commands.argument("player", EntityArgument.player())
                .then(Commands.literal("list")
                    .then(Commands.argument("type", StringArgumentType.string())
                        .suggests { _, builder ->
                            SharedSuggestionProvider.suggest(MetadataType.entries.map { it.name }.toList(), builder)
                        }
                        .executes { ctx ->
                            executeList(
                                ctx,
                                EntityArgument.getPlayer(ctx, "player"),
                                MetadataType.valueOf(StringArgumentType.getString(ctx, "type"))
                            )
                        }
                    )
                    .executes { ctx ->
                        executeList(
                            ctx,
                            EntityArgument.getPlayer(ctx, "player")
                        )
                    }
                )
                .then(Commands.literal("get")
                    .then(Commands.argument("key", StringArgumentType.string())
                        .executes { ctx ->
                            executeGet(
                                ctx,
                                EntityArgument.getPlayer(ctx, "player"),
                                StringArgumentType.getString(ctx, "key")
                            )
                        }
                    )
                )
                .then(Commands.literal("set")
                    .then(Commands.argument("key", StringArgumentType.string())
                        .then(Commands.argument("type", StringArgumentType.string())
                            .suggests { _, builder ->
                                SharedSuggestionProvider.suggest(MetadataType.entries.map { it.name }.toList(), builder)
                            }
                            .then(Commands.argument("value", StringArgumentType.greedyString())
                                .executes { ctx ->
                                    executeSet(
                                        ctx,
                                        EntityArgument.getPlayer(ctx, "player"),
                                        StringArgumentType.getString(ctx, "key"),
                                        MetadataType.valueOf(StringArgumentType.getString(ctx, "type")),
                                        StringArgumentType.getString(ctx, "value")
                                    )
                                }
                            )
                        )
                    )
                )
                .then(Commands.literal("remove")
                    .then(Commands.argument("key", StringArgumentType.string())
                        .executes { ctx ->
                            executeRemove(
                                ctx,
                                EntityArgument.getPlayer(ctx, "player"),
                                StringArgumentType.getString(ctx, "key")
                            )
                        }
                    )
                )
                .then(Commands.literal("clear")
                    .executes { ctx ->
                        executeClear(
                            ctx,
                            EntityArgument.getPlayer(ctx, "player")
                        )
                    }
                )
                .then(Commands.literal("toggle")
                    .then(Commands.argument("key", StringArgumentType.string())
                        .executes { ctx ->
                            executeToggle(
                                ctx,
                                EntityArgument.getPlayer(ctx, "player"),
                                StringArgumentType.getString(ctx, "key")
                            )
                        }
                    )
                )
                .then(Commands.literal("shift")
                    .then(Commands.argument("key", StringArgumentType.string())
                        .then(Commands.argument("amount", StringArgumentType.string())
                            .executes { ctx ->
                                executeShift(
                                    ctx,
                                    EntityArgument.getPlayer(ctx, "player"),
                                    StringArgumentType.getString(ctx, "key"),
                                    StringArgumentType.getString(ctx, "amount")
                                )
                            }
                        )
                    )
                )
            )
            .build()
    }

    companion object {
        fun executeList(ctx: CommandContext<CommandSourceStack>, target: ServerPlayer, type: MetadataType? = null): Int {
            val storage = SkiesGUIs.INSTANCE.storage ?: run {
                ctx.source.sendMessage(Component.text("The storage system is not initialized. Please check the console for errors!", NamedTextColor.RED))
                return 0
            }
            storage.getUserAsync(target).thenApply { userData ->
                var metadata: Map<String, MetadataValue> = userData.metdadata
                if (type != null) {
                    metadata = metadata.filter { it.value.type == type }
                }

                if (metadata.isEmpty()) {
                    ctx.source.sendMessage(Component.text("No metadata found for player ${target.name.string}${if (type != null) " of type ${type.name}" else ""}.",
                        NamedTextColor.RED))
                    return@thenApply
                }

                ctx.source.sendMessage(
                    Component.text("Metadata List Query for ", NamedTextColor.GREEN)
                        .append(Component.text(target.name.string, NamedTextColor.WHITE))
                        .append(Component.text(" (filter is ${type ?: "Any"})", NamedTextColor.GRAY))
                        .append(Component.text(": ", NamedTextColor.DARK_GRAY))
                )
                var component = Component.text()
                for ((i, entry) in metadata.toList().withIndex()) {
                    component = component.append(Component.text("${entry.first}=${entry.second.value}", NamedTextColor.GRAY))
                    if (i < metadata.size - 1) {
                        component = component.append(Component.text(", ", NamedTextColor.DARK_GRAY))
                    }
                }
                ctx.source.sendMessage(component)

            }.exceptionally {
                ctx.source.sendMessage(Component.text("An error occurred while fetching userdata: ${it.message}", NamedTextColor.RED))
            }

            return 1
        }
        fun executeGet(ctx: CommandContext<CommandSourceStack>, target: ServerPlayer, key: String): Int {
            val storage = SkiesGUIs.INSTANCE.storage ?: run {
                ctx.source.sendMessage(Component.text("The storage system is not initialized. Please check the console for errors!", NamedTextColor.RED))
                return 0
            }
            storage.getUserAsync(target).thenApply { userData ->
                val entry = userData.metdadata.get(key) ?: run {
                    ctx.source.sendMessage(Component.text("No metadata found for key '$key' for player ${target.name.string}.", NamedTextColor.RED))
                    return@thenApply
                }

                ctx.source.sendMessage(
                    Component.text("Metadata Get Query", NamedTextColor.GREEN)
                        .append(Component.text(" - ", NamedTextColor.DARK_GRAY))
                        .append(Component.text(target.name.string, NamedTextColor.WHITE))
                        .append(Component.text(" has the key ", NamedTextColor.GRAY))
                        .append(Component.text(key, NamedTextColor.WHITE))
                        .append(Component.text(" set as ", NamedTextColor.GRAY))
                        .append(Component.text(entry.value.toString(), NamedTextColor.WHITE))
                        .append(Component.text(" (type is ${entry.type.name})", NamedTextColor.GRAY))
                )
            }.exceptionally {
                ctx.source.sendMessage(Component.text("An error occurred while fetching userdata: ${it.message}", NamedTextColor.RED))
            }

            return 1
        }
        fun executeSet(ctx: CommandContext<CommandSourceStack>, target: ServerPlayer, key: String, type: MetadataType, value: String): Int {
            val storage = SkiesGUIs.INSTANCE.storage ?: run {
                ctx.source.sendMessage(Component.text("The storage system is not initialized. Please check the console for errors!", NamedTextColor.RED))
                return 0
            }
            storage.getUserAsync(target).thenApply { userData ->
                val (any, error) = type.parseString(value)
                if (any == null) {
                    ctx.source.sendMessage(Component.text("Failed to parse value '$value' as type ${type.name}: $error", NamedTextColor.RED))
                    return@thenApply
                }

                userData.metdadata[key] = MetadataValue(type, any)
                if (!storage.saveUser(target, userData)) {
                    ctx.source.sendMessage(Component.text("Failed to save metadata for player ${target.name.string}.", NamedTextColor.RED))
                    return@thenApply
                }
                ctx.source.sendMessage(
                    Component.text("Metadata Set", NamedTextColor.GREEN)
                        .append(Component.text(" - ", NamedTextColor.DARK_GRAY))
                        .append(Component.text(target.name.string, NamedTextColor.WHITE))
                        .append(Component.text(" now has the key ", NamedTextColor.GRAY))
                        .append(Component.text(key, NamedTextColor.WHITE))
                        .append(Component.text(" set as ", NamedTextColor.GRAY))
                        .append(Component.text(any.toString(), NamedTextColor.WHITE))
                        .append(Component.text(" (type is ${type})", NamedTextColor.GRAY))
                )
            }.exceptionally {
                ctx.source.sendMessage(Component.text("An error occurred while fetching userdata: ${it.message}", NamedTextColor.RED))
            }

            return 1
        }
        fun executeRemove(ctx: CommandContext<CommandSourceStack>, target: ServerPlayer, key: String): Int {
            val storage = SkiesGUIs.INSTANCE.storage ?: run {
                ctx.source.sendMessage(Component.text("The storage system is not initialized. Please check the console for errors!", NamedTextColor.RED))
                return 0
            }
            storage.getUserAsync(target).thenApply { userData ->
                val entry = userData.metdadata.get(key) ?: run {
                    ctx.source.sendMessage(Component.text("No metadata found for key '$key' for player ${target.name.string}.", NamedTextColor.RED))
                    return@thenApply
                }

                userData.metdadata.remove(key)
                if (!storage.saveUser(target, userData)) {
                    ctx.source.sendMessage(Component.text("Failed to save metadata for player ${target.name.string}.", NamedTextColor.RED))
                    return@thenApply
                }

                ctx.source.sendMessage(
                    Component.text("Metadata Set", NamedTextColor.GREEN)
                        .append(Component.text(" - ", NamedTextColor.DARK_GRAY))
                        .append(Component.text(target.name.string, NamedTextColor.WHITE))
                        .append(Component.text(" no longer has metadata with the key ", NamedTextColor.GRAY))
                        .append(Component.text(key, NamedTextColor.WHITE))
                )
            }.exceptionally {
                ctx.source.sendMessage(Component.text("An error occurred while fetching userdata: ${it.message}", NamedTextColor.RED))
            }

            return 1
        }
        fun executeClear(ctx: CommandContext<CommandSourceStack>, target: ServerPlayer): Int {
            val storage = SkiesGUIs.INSTANCE.storage ?: run {
                ctx.source.sendMessage(Component.text("The storage system is not initialized. Please check the console for errors!", NamedTextColor.RED))
                return 0
            }
            storage.getUserAsync(target).thenApply { userData ->
                userData.metdadata = HashMap()
                if (!storage.saveUser(target, userData)) {
                    ctx.source.sendMessage(Component.text("Failed to clear metadata for player ${target.name.string}.", NamedTextColor.RED))
                    return@thenApply
                }

                ctx.source.sendMessage(
                    Component.text("Metadata Set", NamedTextColor.GREEN)
                        .append(Component.text(" - ", NamedTextColor.DARK_GRAY))
                        .append(Component.text(target.name.string, NamedTextColor.WHITE))
                        .append(Component.text("'s metadata has been cleared.", NamedTextColor.GRAY))
                )
            }.exceptionally {
                ctx.source.sendMessage(Component.text("An error occurred while fetching userdata: ${it.message}", NamedTextColor.RED))
            }

            return 1
        }
        fun executeToggle(ctx: CommandContext<CommandSourceStack>, target: ServerPlayer, key: String): Int {
            val storage = SkiesGUIs.INSTANCE.storage ?: run {
                ctx.source.sendMessage(Component.text("The storage system is not initialized. Please check the console for errors!", NamedTextColor.RED))
                return 0
            }
            storage.getUserAsync(target).thenApply { userData ->
                val entry = userData.metdadata.get(key) ?: run {
                    ctx.source.sendMessage(Component.text("No metadata found for key '$key' for player ${target.name.string}.", NamedTextColor.RED))
                    return@thenApply
                }

                if (entry.type != MetadataType.BOOLEAN) {
                    ctx.source.sendMessage(Component.text("Metadata with key '$key' is not of type BOOLEAN for player ${target.name.string}.", NamedTextColor.RED))
                    return@thenApply
                }

                entry.value = !(entry.value as Boolean)

                userData.metdadata[key] = entry
                if (!storage.saveUser(target, userData)) {
                    ctx.source.sendMessage(Component.text("Failed to save metadata for player ${target.name.string}.", NamedTextColor.RED))
                    return@thenApply
                }

                ctx.source.sendMessage(
                    Component.text("Metadata Toggle", NamedTextColor.GREEN)
                        .append(Component.text(" - ", NamedTextColor.DARK_GRAY))
                        .append(Component.text(target.name.string, NamedTextColor.WHITE))
                        .append(Component.text(" had their metadata with the key ", NamedTextColor.GRAY))
                        .append(Component.text(key, NamedTextColor.WHITE))
                        .append(Component.text(" toggled to ", NamedTextColor.GRAY))
                        .append(Component.text(entry.value.toString(), NamedTextColor.WHITE))
                )
            }.exceptionally {
                ctx.source.sendMessage(Component.text("An error occurred while fetching userdata: ${it.message}", NamedTextColor.RED))
            }

            return 1
        }
        fun executeShift(ctx: CommandContext<CommandSourceStack>, target: ServerPlayer, key: String, amount: String): Int {
            val storage = SkiesGUIs.INSTANCE.storage ?: run {
                ctx.source.sendMessage(Component.text("The storage system is not initialized. Please check the console for errors!", NamedTextColor.RED))
                return 0
            }
            storage.getUserAsync(target).thenApply { userData ->
                val entry = userData.metdadata.get(key) ?: run {
                    ctx.source.sendMessage(Component.text("No metadata found for key '$key' for player ${target.name.string}.", NamedTextColor.RED))
                    return@thenApply
                }

                if (entry.type != MetadataType.INTEGER && entry.type != MetadataType.LONG && entry.type != MetadataType.DOUBLE) {
                    ctx.source.sendMessage(Component.text("Metadata with key '$key' is not of type INT/LONG/DOUBLE for player ${target.name.string}.", NamedTextColor.RED))
                    return@thenApply
                }

                if (!entry.type.isValid(amount)) {
                    ctx.source.sendMessage(Component.text("Value '$amount' is not a valid ${entry.type.name} for player ${target.name.string}.", NamedTextColor.RED))
                    return@thenApply
                }

                entry.value = when (entry.type) {
                    MetadataType.INTEGER -> (entry.value as Int + (amount.toIntOrNull() ?: 0))
                    MetadataType.LONG -> (entry.value as Long + (amount.toLongOrNull() ?: 0))
                    MetadataType.DOUBLE -> (entry.value as Double + (amount.toDoubleOrNull() ?: 0.0))
                    else -> entry.value
                }

                userData.metdadata[key] = entry
                if (!storage.saveUser(target, userData)) {
                    ctx.source.sendMessage(Component.text("Failed to save metadata for player ${target.name.string}.", NamedTextColor.RED))
                    return@thenApply
                }

                ctx.source.sendMessage(
                    Component.text("Metadata Shift", NamedTextColor.GREEN)
                        .append(Component.text(" - ", NamedTextColor.DARK_GRAY))
                        .append(Component.text(target.name.string, NamedTextColor.WHITE))
                        .append(Component.text(" had their metadata with the key ", NamedTextColor.GRAY))
                        .append(Component.text(key, NamedTextColor.WHITE))
                        .append(Component.text(" shifted by ", NamedTextColor.GRAY))
                        .append(Component.text(amount, NamedTextColor.WHITE))
                        .append(Component.text(" to ", NamedTextColor.GRAY))
                        .append(Component.text(entry.value.toString(), NamedTextColor.WHITE))
                )
            }.exceptionally {
                ctx.source.sendMessage(Component.text("An error occurred while fetching userdata: ${it.message}", NamedTextColor.RED))
            }

            return 1
        }
    }
}
