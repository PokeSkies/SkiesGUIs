package com.pokeskies.skiesguis

import com.pokeskies.skiesguis.commands.BaseCommands
import com.pokeskies.skiesguis.commands.GUICommands
import com.pokeskies.skiesguis.config.ConfigManager
import com.pokeskies.skiesguis.economy.EconomyType
import com.pokeskies.skiesguis.economy.IEconomyService
import com.pokeskies.skiesguis.gui.ChestGUI
import com.pokeskies.skiesguis.placeholders.PlaceholderManager
import com.pokeskies.skiesguis.utils.Scheduler
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
        const val MOD_ID: String = "skiesguis"
        val LOGGER: Logger = LogManager.getLogger(MOD_ID)
    }

    lateinit var configDir: File
    lateinit var configManager: ConfigManager

    private var economyServices: Map<EconomyType, IEconomyService> = emptyMap()
    lateinit var placeholderManager: PlaceholderManager

    var adventure: FabricServerAudiences? = null
    lateinit var server: MinecraftServer
    lateinit var nbtOpts: RegistryOps<Tag>

    lateinit var graalEngine: Engine

    var inventoryControllers: MutableMap<UUID, ChestGUI.InventoryController> = mutableMapOf()

    override fun onInitialize() {
        INSTANCE = this

        this.configDir = File(FabricLoader.getInstance().configDirectory, MOD_ID)
        this.configManager = ConfigManager(configDir)

        this.economyServices = IEconomyService.getLoadedEconomyServices()
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
            this.placeholderManager.registerServices()

            Scheduler.start()
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
        this.economyServices = IEconomyService.getLoadedEconomyServices()
    }

    fun getLoadedEconomyServices(): Map<EconomyType, IEconomyService> {
        return this.economyServices
    }

    fun getEconomyService(economyType: EconomyType?): IEconomyService? {
        return economyType?.let { this.economyServices[it] }
    }

    fun getEconomyServiceOrDefault(economyType: EconomyType?): IEconomyService? {
        return economyType?.let { this.economyServices[it] } ?: this.economyServices.values.firstOrNull()
    }
}
