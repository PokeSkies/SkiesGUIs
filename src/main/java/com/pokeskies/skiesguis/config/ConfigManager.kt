package com.pokeskies.skiesguis.config

import com.google.common.collect.BiMap
import com.google.common.collect.HashBiMap
import com.google.gson.JsonParser
import com.google.gson.stream.JsonReader
import com.pokeskies.skiesguis.SkiesGUIs
import com.pokeskies.skiesguis.config.tooltips.TooltipConfig
import com.pokeskies.skiesguis.utils.Utils
import java.io.*
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.stream.Collectors

object ConfigManager {
    private var assetPackage = "assets/${SkiesGUIs.MOD_ID}"

    lateinit var CONFIG: MainConfig
    var GUIS: BiMap<String, GuiConfig> = HashBiMap.create()

    fun load() {
        copyDefaults()

        CONFIG = loadFile("config.json", MainConfig())

        loadGUIs()
        loadTooltips(SkiesGUIs.INSTANCE.configDir.resolve("tooltips")).forEach { TooltipConfig.TOOLTIPS[it.key] = it.value }
    }

    fun copyDefaults() {
        val classLoader = SkiesGUIs::class.java.classLoader

        SkiesGUIs.INSTANCE.configDir.mkdirs()

        attemptDefaultFileCopy(classLoader, "config.json")
        attemptDefaultDirectoryCopy(classLoader, "guis")
    }

    private fun loadGUIs() {
        GUIS.clear()

        val dir = SkiesGUIs.INSTANCE.configDir.resolve("guis")
        if (dir.exists() && dir.isDirectory) {
            val files = Files.walk(dir.toPath())
                .filter { path: Path -> Files.isRegularFile(path) }
                .map { it.toFile() }
                .collect(Collectors.toList())
            if (files != null) {
                SkiesGUIs.LOGGER.info("Found ${files.size} GUI files: ${files.map { it.name }}")
                val enabledFiles = mutableListOf<String>()
                for (file in files) {
                    val fileName = file.name
                    if (file.isFile && fileName.contains(".json")) {
                        val id = fileName.substring(0, fileName.lastIndexOf(".json"))
                        val jsonReader = JsonReader(InputStreamReader(FileInputStream(file), Charsets.UTF_8))
                        try {
                            SkiesGUIs.INSTANCE.gson.fromJson(JsonParser.parseReader(jsonReader), GuiConfig::class.java)?.let { gui ->
                                gui.id = id
                                GUIS[id] = gui
                                enabledFiles.add(fileName)
                            }
                        } catch (ex: Exception) {
                            Utils.printError("Error while trying to parse the GUI $fileName!")
                            ex.printStackTrace()
                        }
                    } else {
                        Utils.printError("File $fileName is either not a file or is not a .json file!")
                    }
                }
                Utils.printInfo("Successfully read and loaded the following enabled GUI files: $enabledFiles")
            }
        } else {
            Utils.printError("The 'guis' directory either does not exist or is not a directory!")
        }
    }

    fun <T : Any> loadFile(filename: String, default: T, create: Boolean = false): T {
        val file = File(SkiesGUIs.INSTANCE.configDir, filename)
        var value: T = default
        try {
            Files.createDirectories(SkiesGUIs.INSTANCE.configDir.toPath())
            if (file.exists()) {
                FileReader(file).use { reader ->
                    val jsonReader = JsonReader(reader)
                    value = SkiesGUIs.INSTANCE.gsonPretty.fromJson(jsonReader, default::class.java)
                }
            } else if (create) {
                Files.createFile(file.toPath())
                FileWriter(file).use { fileWriter ->
                    fileWriter.write(SkiesGUIs.INSTANCE.gsonPretty.toJson(default))
                    fileWriter.flush()
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return value
    }

    fun <T> saveFile(filename: String, `object`: T): Boolean {
        val dir = SkiesGUIs.INSTANCE.configDir
        val file = File(dir, filename)
        try {
            FileWriter(file).use { fileWriter ->
                fileWriter.write(SkiesGUIs.INSTANCE.gsonPretty.toJson(`object`))
                fileWriter.flush()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
        return true
    }

    private fun attemptDefaultFileCopy(classLoader: ClassLoader, fileName: String) {
        val file = SkiesGUIs.INSTANCE.configDir.resolve(fileName)
        if (!file.exists()) {
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
                    val tooltipConfig = loadFile(path.toString(), TooltipConfig())
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
