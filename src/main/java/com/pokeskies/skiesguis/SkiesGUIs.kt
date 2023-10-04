package com.pokeskies.skiesguis

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.pokeskies.skiesguis.commands.GUICommand
import com.pokeskies.skiesguis.config.ConfigManager
import com.pokeskies.skiesguis.config.actions.Action
import com.pokeskies.skiesguis.config.actions.ActionType
import com.pokeskies.skiesguis.config.actions.ClickType
import com.pokeskies.skiesguis.config.requirements.ComparisonType
import com.pokeskies.skiesguis.config.requirements.Requirement
import com.pokeskies.skiesguis.config.requirements.RequirementType
import com.pokeskies.skiesguis.utils.Utils
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.ServerStarting
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.ServerStopped
import net.fabricmc.loader.api.FabricLoader
import net.kyori.adventure.platform.fabric.FabricServerAudiences
import net.minecraft.item.Item
import net.minecraft.nbt.NbtCompound
import net.minecraft.registry.Registries
import net.minecraft.server.MinecraftServer
import net.minecraft.sound.SoundEvent
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

        this.gson = GsonBuilder().setPrettyPrinting().disableHtmlEscaping()
            .registerTypeAdapter(Action::class.java, ActionType.ActionTypeAdaptor())
            .registerTypeAdapter(Requirement::class.java, RequirementType.RequirementTypeAdaptor())
            .registerTypeAdapter(ClickType::class.java, ClickType.ClickTypeAdaptor())
            .registerTypeAdapter(ComparisonType::class.java, ComparisonType.ComparisonTypeAdaptor())
            .registerTypeHierarchyAdapter(Item::class.java, Utils.RegistrySerializer(Registries.ITEM))
            .registerTypeHierarchyAdapter(SoundEvent::class.java, Utils.RegistrySerializer(Registries.SOUND_EVENT))
            .registerTypeHierarchyAdapter(NbtCompound::class.java, Utils.CodecSerializer(NbtCompound.CODEC))
            .create()

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
