package com.pokeskies.skiesguis.storage

import com.pokeskies.skiesguis.config.StorageOptions
import com.pokeskies.skiesguis.data.UserData
import com.pokeskies.skiesguis.storage.database.MongoStorage
import com.pokeskies.skiesguis.storage.database.sql.SQLStorage
import com.pokeskies.skiesguis.storage.file.FileStorage
import net.minecraft.server.level.ServerPlayer
import java.util.*
import java.util.concurrent.CompletableFuture

interface IStorage {
    companion object {
        fun load(config: StorageOptions): IStorage {
            return when (config.type) {
                StorageType.JSON -> FileStorage()
                StorageType.MONGO -> MongoStorage(config)
                StorageType.MYSQL, StorageType.SQLITE -> SQLStorage(config)
            }
        }
    }

    fun getUser(uuid: UUID): UserData
    fun getUser(player: ServerPlayer): UserData = getUser(player.uuid)
    fun saveUser(uuid: UUID, userData: UserData): Boolean
    fun saveUser(player: ServerPlayer, userData: UserData): Boolean = saveUser(player.uuid, userData)

    fun getUserAsync(uuid: UUID): CompletableFuture<UserData>
    fun getUserAsync(player: ServerPlayer): CompletableFuture<UserData> = getUserAsync(player.uuid)
    fun saveUserAsync(uuid: UUID, userData: UserData): CompletableFuture<Boolean>
    fun saveUserAsync(player: ServerPlayer, userData: UserData): CompletableFuture<Boolean> = saveUserAsync(player.uuid, userData)

    fun close() {}
}
