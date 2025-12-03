package com.pokeskies.skiesguis.storage.database.sql

import java.sql.Connection
import java.sql.SQLException

/**
 * @author Pedro Souza
 * @since 03/12/2023
 */
interface ConnectionProvider {
    @Throws(SQLException::class)
    fun init()
    @Throws(SQLException::class)
    fun shutdown()
    @Throws(SQLException::class)
    fun createConnection(): Connection
    fun getName(): String
    fun isInitialized(): Boolean
}