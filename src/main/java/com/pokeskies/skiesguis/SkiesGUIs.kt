package com.pokeskies.skiesguis

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.pokeskies.skiesguis.commands.GUICommand
import com.pokeskies.skiesguis.config.ConfigManager
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.ServerStarting
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.ServerStopped
import net.fabricmc.loader.api.FabricLoader
import net.kyori.adventure.platform.fabric.FabricServerAudiences
import net.minecraft.server.MinecraftServer
import org.apache.logging.log4j.LogManager
import java.io.File

class SkiesGUIs : ModInitializer {
    companion object {
        lateinit var INSTANCE: SkiesGUIs
        val LOGGER = LogManager.getLogger("skiesguis")
    }

    lateinit var gson: Gson
    lateinit var configDir: File
    lateinit var configManager: ConfigManager

    var adventure: FabricServerAudiences? = null
    var server: MinecraftServer? = null

    override fun onInitialize() {
        INSTANCE = this

        this.gson = GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()
        this.configDir = File(FabricLoader.getInstance().configDirectory, "skiesguis")
        this.configManager = ConfigManager(configDir)

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
            GUICommand.register(
                dispatcher
            )
        }
    }

    fun reload() {
        this.configManager.reload()
    }
}
