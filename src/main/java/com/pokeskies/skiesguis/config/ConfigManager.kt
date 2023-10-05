package com.pokeskies.skiesguis.config

import com.google.common.collect.BiMap
import com.google.common.collect.HashBiMap
import com.google.gson.JsonParser
import com.google.gson.stream.JsonReader
import com.pokeskies.skiesguis.SkiesGUIs
import com.pokeskies.skiesguis.utils.Utils
import java.io.*
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption


class ConfigManager(val configDir: File) {
    lateinit var config: MainConfig

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
                Utils.error("Failed to copy the default config file: $e - ${e.message}")
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
                Utils.error("Failed to copy the default GUI file: " + e.message)
            }
        }
    }

    private fun loadGUIs() {
        GUIS.clear()

        val dir = configDir.resolve("guis")
        if (dir.exists() && dir.isDirectory) {
            val files = dir.listFiles()
            if (files != null) {
                for (file in files) {
                    val fileName = file.name
                    if (file.isFile && fileName.contains(".json")) {
                        val id = fileName.substring(0, fileName.lastIndexOf(".json"))
                        val jsonReader = JsonReader(InputStreamReader(FileInputStream(file), Charsets.UTF_8))
                        try {
                            GUIS[id] = SkiesGUIs.INSTANCE.gson.fromJson(JsonParser.parseReader(jsonReader), GuiConfig::class.java)
                            Utils.debug("Successfully read and loaded the file $fileName!", true)
                        } catch (ex: Exception) {
                            Utils.error("Error while trying to parse the file $fileName as a GUI!")
                            ex.printStackTrace()
                        }
                    } else {
                        Utils.error("File $fileName is either not a file or is not a .json file!")
                    }
                }
            }
        } else {
            Utils.error("The GUIs directory either does not exist or is not a directory!")
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
                    value = SkiesGUIs.INSTANCE.gson.fromJson(jsonReader, classObject)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return value
    }

    fun <T> saveFile(filename: String?, `object`: T) {
        val file = File(configDir, filename)
        try {
            FileWriter(file).use { fileWriter ->
                fileWriter.write(SkiesGUIs.INSTANCE.gson.toJson(`object`))
                fileWriter.flush()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}