package com.pokeskies.skiesguis.storage.database.sql

import com.google.gson.reflect.TypeToken
import com.pokeskies.skiesguis.SkiesGUIs
import com.pokeskies.skiesguis.config.StorageOptions
import com.pokeskies.skiesguis.data.MetadataValue
import com.pokeskies.skiesguis.data.UserData
import com.pokeskies.skiesguis.storage.IStorage
import com.pokeskies.skiesguis.storage.StorageType
import com.pokeskies.skiesguis.storage.database.sql.providers.MySQLProvider
import com.pokeskies.skiesguis.storage.database.sql.providers.SQLiteProvider
import java.lang.reflect.Type
import java.sql.SQLException
import java.util.*
import java.util.concurrent.CompletableFuture

class SQLStorage(private val config: StorageOptions) : IStorage {
    private val connectionProvider: ConnectionProvider = when (config.type) {
        StorageType.MYSQL -> MySQLProvider(config)
        StorageType.SQLITE -> SQLiteProvider(config)
        else -> throw IllegalStateException("Invalid storage type!")
    }
    private val dataType: Type = object : TypeToken<HashMap<String, MetadataValue>>() {}.type

    init {
        connectionProvider.init()
    }

    override fun getUser(uuid: UUID): UserData {
        val userData = UserData(uuid)
        try {
            connectionProvider.createConnection().use {
                val statement = it.createStatement()
                val result = statement.executeQuery(String.format("SELECT * FROM ${config.tablePrefix}userdata WHERE uuid='%s'", uuid.toString()))
                if (result != null && result.next()) {
                    userData.metdadata = result.getString("metdadata")?.let { dataString ->
                        SkiesGUIs.INSTANCE.gson.fromJson(dataString, dataType)
                    } ?: HashMap()
                }
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        return userData
    }

    override fun saveUser(uuid: UUID, userData: UserData): Boolean {
        return try {
            connectionProvider.createConnection().use {
                val statement = it.createStatement()
                statement.execute(String.format("REPLACE INTO ${config.tablePrefix}userdata (uuid, metdadata) VALUES ('%s', '%s')",
                    uuid.toString(),
                    SkiesGUIs.INSTANCE.gson.toJson(userData.metdadata, dataType)
                ))
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override fun getUserAsync(uuid: UUID): CompletableFuture<UserData> {
        return CompletableFuture.supplyAsync({
            try {
                val result = getUser(uuid)
                result
            } catch (e: Exception) {
                UserData(uuid)  // Return default data rather than throwing
            }
        }, SkiesGUIs.INSTANCE.asyncExecutor)
    }

    override fun saveUserAsync(uuid: UUID, userData: UserData): CompletableFuture<Boolean> {
        return CompletableFuture.supplyAsync({
            saveUser(uuid, userData)
        }, SkiesGUIs.INSTANCE.asyncExecutor)
    }

    override fun close() {
        connectionProvider.shutdown()
    }
}
