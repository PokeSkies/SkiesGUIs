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
import com.pokeskies.skiesguis.economy.EconomyType
import com.pokeskies.skiesguis.utils.Utils
import net.minecraft.item.Item
import net.minecraft.nbt.NbtCompound
import net.minecraft.registry.Registries
import net.minecraft.sound.SoundEvent
import java.io.*
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.util.stream.Collectors


class ConfigManager(private val configDir: File) {
    lateinit var config: MainConfig
    var gson: Gson = GsonBuilder().setPrettyPrinting().disableHtmlEscaping()
        .registerTypeAdapter(Action::class.java, ActionType.ActionTypeAdaptor())
        .registerTypeAdapter(Requirement::class.java, RequirementType.RequirementTypeAdaptor())
        .registerTypeAdapter(ClickType::class.java, ClickType.ClickTypeAdaptor())
        .registerTypeAdapter(ComparisonType::class.java, ComparisonType.ComparisonTypeAdaptor())
        .registerTypeAdapter(EconomyType::class.java, EconomyType.EconomyTypeAdaptor())
        .registerTypeHierarchyAdapter(Item::class.java, Utils.RegistrySerializer(Registries.ITEM))
        .registerTypeHierarchyAdapter(SoundEvent::class.java, Utils.RegistrySerializer(Registries.SOUND_EVENT))
        .registerTypeHierarchyAdapter(NbtCompound::class.java, Utils.CodecSerializer(NbtCompound.CODEC))
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
    }

    fun copyDefaults() {
        val classLoader = SkiesGUIs::class.java.classLoader

        configDir.mkdirs()

        // Main Config
        val configFile = configDir.resolve("config.json")
        if (!configFile.exists()) {
            try {
                val inputStream: InputStream = classLoader.getResourceAsStream("assets/skiesguis/config.json")
                Files.copy(inputStream, configFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
            } catch (e: Exception) {
                Utils.printError("Failed to copy the default config file: $e - ${e.message}")
            }
        }

        // If the 'guis' directory does not exist, create it and copy the default example GUI
        val guiDirectory = configDir.resolve("guis")
        if (!guiDirectory.exists()) {
            guiDirectory.mkdirs()
            val file = guiDirectory.resolve("example_gui.json")
            try {
                val resourceFile: Path =
                    Path.of(classLoader.getResource("assets/skiesguis/guis/example_gui.json").toURI())
                Files.copy(resourceFile, file.toPath(), StandardCopyOption.REPLACE_EXISTING)
            } catch (e: Exception) {
                Utils.printError("Failed to copy the default GUI file: " + e.message)
            }
        }
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
                        GUIS[id] = gson.fromJson(JsonParser.parseReader(jsonReader), GuiConfig::class.java)
                        Utils.printInfo("Successfully read and loaded the file ${file.name}!")
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
}