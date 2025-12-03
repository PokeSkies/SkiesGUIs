package com.pokeskies.skiesguis.storage.file

import com.pokeskies.skiesguis.SkiesGUIs
import com.pokeskies.skiesguis.config.ConfigManager
import com.pokeskies.skiesguis.data.UserData
import com.pokeskies.skiesguis.storage.IStorage
import java.util.*
import java.util.concurrent.CompletableFuture

class FileStorage : IStorage {
    private var fileData: FileData = ConfigManager.loadFile(STORAGE_FILENAME, FileData(), true)

    companion object {
        private const val STORAGE_FILENAME = "storage.json"
    }

    override fun getUser(uuid: UUID): UserData {
        val userData = fileData.userdata[uuid]
        return userData ?: UserData(uuid)
    }

    override fun saveUser(uuid: UUID, userData: UserData): Boolean {
        fileData.userdata[uuid] = userData
        return ConfigManager.saveFile(STORAGE_FILENAME, fileData)
    }

    override fun getUserAsync(uuid: UUID): CompletableFuture<UserData> {
        return CompletableFuture.supplyAsync({
            getUser(uuid)
        }, SkiesGUIs.INSTANCE.asyncExecutor)
    }

    override fun saveUserAsync(uuid: UUID, userData: UserData): CompletableFuture<Boolean> {
        return CompletableFuture.supplyAsync({
            saveUser(uuid, userData)
        }, SkiesGUIs.INSTANCE.asyncExecutor)
    }
}
