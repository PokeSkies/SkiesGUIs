package com.pokeskies.skiesguis.config

import com.google.common.collect.BiMap
import com.google.common.collect.HashBiMap
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.google.gson.stream.JsonReader
import com.pokeskies.skiesguis.SkiesGUIs
import com.pokeskies.skiesguis.config.actions.Action
import com.pokeskies.skiesguis.config.actions.ActionType
import com.pokeskies.skiesguis.config.actions.ClickType
import com.pokeskies.skiesguis.config.requirements.ComparisonType
import com.pokeskies.skiesguis.config.requirements.Requirement
import com.pokeskies.skiesguis.config.requirements.RequirementType
import com.pokeskies.skiesguis.config.tooltips.TooltipConfig
import com.pokeskies.skiesguis.economy.EconomyType
import com.pokeskies.skiesguis.utils.CompoundTagAdaptor
import com.pokeskies.skiesguis.utils.Utils
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.sounds.SoundEvent
import net.minecraft.world.item.Item
import java.io.*
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.stream.Collectors

class ConfigManager(private val configDir: File) {
    private var assetPackage = "assets/${SkiesGUIs.MOD_ID}"

    lateinit var config: MainConfig
    var gson: Gson = GsonBuilder().setPrettyPrinting().disableHtmlEscaping()
        .registerTypeAdapter(Action::class.java, ActionType.ActionTypeAdaptor())
        .registerTypeAdapter(Requirement::class.java, RequirementType.RequirementTypeAdaptor())
        .registerTypeAdapter(ClickType::class.java, ClickType.ClickTypeAdaptor())
        .registerTypeAdapter(ComparisonType::class.java, ComparisonType.ComparisonTypeAdaptor())
        .registerTypeAdapter(EconomyType::class.java, EconomyType.EconomyTypeAdaptor())
        .registerTypeHierarchyAdapter(Item::class.java, Utils.RegistrySerializer(BuiltInRegistries.ITEM))
        .registerTypeHierarchyAdapter(SoundEvent::class.java, Utils.RegistrySerializer(BuiltInRegistries.SOUND_EVENT))
        .registerTypeAdapter(CompoundTag::class.java, CompoundTagAdaptor())
        .create()

    companion object {
        var GUIS: BiMap<String, GuiConfig> = HashBiMap.create()
    }

    init {
        reload()
    }

    fun reload() {
        copyDefaults()
        config = loadFile("config.json", MainConfig::class.java)!!
        loadGUIs()
        loadTooltips(configDir.resolve("tooltips")).forEach { TooltipConfig.TOOLTIPS[it.key] = it.value }
    }

    fun copyDefaults() {
        val classLoader = SkiesGUIs::class.java.classLoader

        configDir.mkdirs()

        attemptDefaultFileCopy(classLoader, "config.json")
        attemptDefaultDirectoryCopy(classLoader, "guis")
    }

    private fun loadGUIs() {
        GUIS.clear()

        val dir = configDir.resolve("guis")
        if (dir.exists() && dir.isDirectory) {
            val filePaths = Files.walk(dir.toPath())
                    .filter { p: Path -> p.toString().endsWith(".json") }
                    .collect(Collectors.toList())

            for (filePath in filePaths) {
                val file = filePath.toFile()
                if (file.isFile) {
                    val id = file.name.substring(0, file.name.lastIndexOf(".json"))
                    val jsonReader = JsonReader(InputStreamReader(FileInputStream(file), Charsets.UTF_8))
                    try {
                        gson.fromJson(JsonParser.parseReader(jsonReader), GuiConfig::class.java)?.let { gui ->
                            gui.id = id
                            GUIS[id] = gui
                            Utils.printInfo("Successfully read and loaded the file ${file.name}!")
                        }
                    } catch (ex: Exception) {
                        Utils.printError("Error while trying to parse the file ${file.name} as a GUI!")
                        ex.printStackTrace()
                    }
                }
            }
        } else {
            Utils.printError("The GUIs directory either does not exist or is not a directory!")
        }
    }

    fun <T : Any> loadFile(filename: String, classObject: Class<T>): T? {
        val file = File(configDir, filename)
        var value: T? = null
        try {
            Files.createDirectories(configDir.toPath())
            try {
                FileReader(file).use { reader ->
                    val jsonReader = JsonReader(reader)
                    value = gson.fromJson(jsonReader, classObject)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return value
    }

    fun <T> saveFile(filename: String, `object`: T) {
        val file = File(configDir, filename)
        try {
            FileWriter(file).use { fileWriter ->
                fileWriter.write(gson.toJson(`object`))
                fileWriter.flush()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun attemptDefaultFileCopy(classLoader: ClassLoader, fileName: String) {
        val file = SkiesGUIs.INSTANCE.configDir.resolve(fileName)
        if (!file.exists()) {
            file.mkdirs()
            try {
                val stream = classLoader.getResourceAsStream("${assetPackage}/$fileName")
                    ?: throw NullPointerException("File not found $fileName")

                Files.copy(stream, file.toPath(), StandardCopyOption.REPLACE_EXISTING)
            } catch (e: Exception) {
                Utils.printError("Failed to copy the default file '$fileName': $e")
            }
        }
    }

    private fun attemptDefaultDirectoryCopy(classLoader: ClassLoader, directoryName: String) {
        val directory = SkiesGUIs.INSTANCE.configDir.resolve(directoryName)
        if (!directory.exists()) {
            directory.mkdirs()
            try {
                val sourceUrl = classLoader.getResource("${assetPackage}/$directoryName")
                    ?: throw NullPointerException("Directory not found $directoryName")
                val sourcePath = Paths.get(sourceUrl.toURI())

                Files.walk(sourcePath).use { stream ->
                    stream.filter { Files.isRegularFile(it) }
                        .forEach { sourceFile ->
                            val destinationFile = directory.resolve(sourcePath.relativize(sourceFile).toString())
                            Files.copy(sourceFile, destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
                        }
                }
            } catch (e: Exception) {
                Utils.printError("Failed to copy the default directory '$directoryName': " + e.message)
            }
        }
    }

    fun loadTooltips(directory: File): MutableMap<String, TooltipConfig> {
        val tooltipConfigs = mutableMapOf<String, TooltipConfig>()
        if (directory.exists() && directory.isDirectory) {
            loadConfigsRecursive(directory, tooltipConfigs) { file ->
                val tooltipConfigName = file.nameWithoutExtension
                try {
                    var path = file.toPath()
                    path = path.subpath(SkiesGUIs.INSTANCE.configDir.toPath().nameCount, path.nameCount)
                    val tooltipConfig = loadFile(path.toString(), TooltipConfig::class.java) ?: TooltipConfig()
                    Pair(tooltipConfigName, tooltipConfig)
                } catch (e: Exception) {
                    SkiesGUIs.LOGGER.error("Error loading tooltip config from ${file.absolutePath}", e)
                    Pair(tooltipConfigName, TooltipConfig())
                }
            }
        }
        return tooltipConfigs
    }

    private fun <K, V> loadConfigsRecursive(directory: File, map: MutableMap<K, V>, loadAction: (File) -> Pair<K, V>) {
        directory.listFiles()?.forEach { file ->
            if (file.isFile && file.name.endsWith(".json")) {
                val (key, value) = loadAction(file)
                map[key] = value
            } else if (file.isDirectory) {
                loadConfigsRecursive(file, map, loadAction)
            }
        }
    }
}
