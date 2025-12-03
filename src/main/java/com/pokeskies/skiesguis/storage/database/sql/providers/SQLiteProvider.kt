package com.pokeskies.skiesguis.storage.database.sql.providers

import com.pokeskies.skiesguis.SkiesGUIs
import com.pokeskies.skiesguis.config.StorageOptions
import com.zaxxer.hikari.HikariConfig
import java.io.File

class SQLiteProvider(config: StorageOptions) : HikariCPProvider(config) {
    override fun getConnectionURL(): String = String.format(
        "jdbc:sqlite:%s",
        File(SkiesGUIs.INSTANCE.configDir, "storage.db").toPath().toAbsolutePath()
    )

    override fun getDriverClassName(): String = "org.sqlite.JDBC"
    override fun getDriverName(): String = "sqlite"
    override fun configure(config: HikariConfig) {}
}
