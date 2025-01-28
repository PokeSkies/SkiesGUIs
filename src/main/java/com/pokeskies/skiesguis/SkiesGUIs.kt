package com.pokeskies.skiesguis

import com.pokeskies.skiesguis.commands.BaseCommands
import com.pokeskies.skiesguis.commands.GUICommands
import com.pokeskies.skiesguis.config.ConfigManager
import com.pokeskies.skiesguis.economy.IEconomyService
import com.pokeskies.skiesguis.gui.ChestGUI
import com.pokeskies.skiesguis.placeholders.PlaceholderManager
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.ServerStarting
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.ServerStopped
import net.fabricmc.loader.api.FabricLoader
import net.kyori.adventure.platform.fabric.FabricServerAudiences
import net.minecraft.nbt.NbtOps
import net.minecraft.nbt.Tag
import net.minecraft.resources.RegistryOps
import net.minecraft.server.MinecraftServer
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.graalvm.polyglot.Engine
import java.io.File
import java.util.*

class SkiesGUIs : ModInitializer {
    companion object {
        lateinit var INSTANCE: SkiesGUIs
        val MOD_ID: String = "skiesguis"
        val LOGGER: Logger = LogManager.getLogger("skiesguis")
    }

    lateinit var configDir: File
    lateinit var configManager: ConfigManager

    var economyService: IEconomyService? = null
    lateinit var placeholderManager: PlaceholderManager

    var adventure: FabricServerAudiences? = null
    lateinit var server: MinecraftServer
    lateinit var nbtOpts: RegistryOps<Tag>

    lateinit var graalEngine: Engine

    var inventoryControllers: MutableMap<UUID, ChestGUI.InventoryController> = mutableMapOf()

    override fun onInitialize() {
        INSTANCE = this

        this.configDir = File(FabricLoader.getInstance().configDirectory, "skiesguis")
        this.configManager = ConfigManager(configDir)

        this.economyService = IEconomyService.getEconomyService(configManager.config.economy)
        this.placeholderManager = PlaceholderManager()

        this.graalEngine = Engine.newBuilder()
            .option("engine.WarnInterpreterOnly", "false")
            .build()

        ServerLifecycleEvents.SERVER_STARTING.register(ServerStarting { server: MinecraftServer ->
            this.adventure = FabricServerAudiences.of(
                server
            )
            this.server = server
            this.nbtOpts = server.registryAccess().createSerializationContext(NbtOps.INSTANCE)
        })
        ServerLifecycleEvents.SERVER_STOPPED.register(ServerStopped { server: MinecraftServer ->
            this.adventure = null
        })
        CommandRegistrationCallback.EVENT.register { dispatcher, _, _ ->
            BaseCommands().register(
                dispatcher
            )
            GUICommands().register(
                dispatcher
            )
        }
    }

    fun reload() {
        this.configManager.reload()
        this.economyService = IEconomyService.getEconomyService(configManager.config.economy)
        this.placeholderManager = PlaceholderManager()
    }
}
