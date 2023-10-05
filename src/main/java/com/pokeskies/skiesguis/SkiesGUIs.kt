package com.pokeskies.skiesguis

import com.pokeskies.skiesguis.commands.BaseCommands
import com.pokeskies.skiesguis.commands.GUICommands
import com.pokeskies.skiesguis.config.ConfigManager
import com.pokeskies.skiesguis.economy.IEconomyService
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.ServerStarting
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.ServerStopped
import net.fabricmc.loader.api.FabricLoader
import net.kyori.adventure.platform.fabric.FabricServerAudiences
import net.minecraft.server.MinecraftServer
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.io.File

class SkiesGUIs : ModInitializer {
    companion object {
        lateinit var INSTANCE: SkiesGUIs
        val LOGGER: Logger = LogManager.getLogger("skiesguis")
    }

    private lateinit var configDir: File
    lateinit var configManager: ConfigManager

    var economyService: IEconomyService? = null

    var adventure: FabricServerAudiences? = null
    var server: MinecraftServer? = null

    override fun onInitialize() {
        INSTANCE = this

        this.configDir = File(FabricLoader.getInstance().configDirectory, "skiesguis")
        this.configManager = ConfigManager(configDir)

        this.economyService = IEconomyService.getEconomyService(configManager.config.currency)

        ServerLifecycleEvents.SERVER_STARTING.register(ServerStarting { server: MinecraftServer? ->
            this.adventure = FabricServerAudiences.of(
                server!!
            )
            this.server = server
        })
        ServerLifecycleEvents.SERVER_STOPPED.register(ServerStopped { server: MinecraftServer? ->
            this.adventure = null
        })
        CommandRegistrationCallback.EVENT.register { dispatcher, _, _ ->
            BaseCommands.register(
                dispatcher
            )
            GUICommands.register(
                dispatcher
            )
        }
    }

    fun reload() {
        this.configManager.reload()
    }
}
