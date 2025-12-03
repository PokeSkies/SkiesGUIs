package com.pokeskies.skiesguis

import com.google.common.util.concurrent.ThreadFactoryBuilder
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.pokeskies.skiesguis.commands.BaseCommands
import com.pokeskies.skiesguis.commands.GUICommands
import com.pokeskies.skiesguis.config.ConfigManager
import com.pokeskies.skiesguis.config.actions.Action
import com.pokeskies.skiesguis.config.actions.ActionType
import com.pokeskies.skiesguis.config.actions.ClickType
import com.pokeskies.skiesguis.config.requirements.ComparisonType
import com.pokeskies.skiesguis.config.requirements.Requirement
import com.pokeskies.skiesguis.config.requirements.RequirementType
import com.pokeskies.skiesguis.data.MetadataValue
import com.pokeskies.skiesguis.economy.EconomyManager
import com.pokeskies.skiesguis.gui.ChestGUI
import com.pokeskies.skiesguis.placeholders.PlaceholderManager
import com.pokeskies.skiesguis.storage.IStorage
import com.pokeskies.skiesguis.storage.StorageType
import com.pokeskies.skiesguis.utils.CompoundTagAdaptor
import com.pokeskies.skiesguis.utils.Scheduler
import com.pokeskies.skiesguis.utils.Utils
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.ServerStarting
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.ServerStopped
import net.fabricmc.loader.api.FabricLoader
import net.kyori.adventure.platform.fabric.FabricServerAudiences
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.nbt.Tag
import net.minecraft.resources.RegistryOps
import net.minecraft.server.MinecraftServer
import net.minecraft.sounds.SoundEvent
import net.minecraft.world.item.Item
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.graalvm.polyglot.Engine
import java.io.File
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class SkiesGUIs : ModInitializer {
    companion object {
        lateinit var INSTANCE: SkiesGUIs
        const val MOD_ID: String = "skiesguis"
        val LOGGER: Logger = LogManager.getLogger(MOD_ID)
    }

    lateinit var configDir: File
    var storage: IStorage? = null

    lateinit var placeholderManager: PlaceholderManager

    var adventure: FabricServerAudiences? = null
    lateinit var server: MinecraftServer
    lateinit var nbtOpts: RegistryOps<Tag>

    lateinit var graalEngine: Engine

    var inventoryControllers: MutableMap<UUID, ChestGUI.InventoryController> = mutableMapOf()

    val asyncExecutor: ExecutorService = Executors.newFixedThreadPool(8, ThreadFactoryBuilder()
        .setNameFormat("SkiesGUIs-Async-%d")
        .setDaemon(true)
        .build())

    var gson: Gson = GsonBuilder().setPrettyPrinting().disableHtmlEscaping()
        .registerTypeAdapter(Action::class.java, ActionType.Adapter())
        .registerTypeAdapter(Requirement::class.java, RequirementType.Adapter())
        .registerTypeAdapter(ClickType::class.java, ClickType.ClickTypeAdaptor())
        .registerTypeAdapter(ComparisonType::class.java, ComparisonType.ComparisonTypeAdaptor())
        .registerTypeAdapter(StorageType::class.java, StorageType.Adapter())
        .registerTypeAdapter(CompoundTag::class.java, CompoundTagAdaptor())
        .registerTypeAdapter(MetadataValue::class.java, MetadataValue.Adapter())
        .registerTypeHierarchyAdapter(Item::class.java, Utils.RegistrySerializer(BuiltInRegistries.ITEM))
        .registerTypeHierarchyAdapter(SoundEvent::class.java, Utils.RegistrySerializer(BuiltInRegistries.SOUND_EVENT))
        .create()

    var gsonPretty: Gson = gson.newBuilder().setPrettyPrinting().create()

    override fun onInitialize() {
        INSTANCE = this

        this.configDir = File(FabricLoader.getInstance().configDirectory, MOD_ID)
        ConfigManager.load()
        this.storage = IStorage.load(ConfigManager.CONFIG.storage)

        this.placeholderManager = PlaceholderManager()
        EconomyManager.init()

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
            BaseCommands().register(dispatcher)
            GUICommands().register(dispatcher)
        }
    }

    fun reload() {
        ConfigManager.load()
        this.storage = IStorage.load(ConfigManager.CONFIG.storage)
    }
}
