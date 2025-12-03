package com.pokeskies.skiesguis.storage.database

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.MongoCredential
import com.mongodb.ServerAddress
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters
import com.mongodb.client.model.ReplaceOptions
import com.mongodb.connection.ClusterSettings
import com.pokeskies.skiesguis.SkiesGUIs
import com.pokeskies.skiesguis.config.StorageOptions
import com.pokeskies.skiesguis.data.UserData
import com.pokeskies.skiesguis.storage.IStorage
import com.pokeskies.skiesguis.utils.UUIDCodec
import com.pokeskies.skiesguis.utils.Utils
import org.bson.UuidRepresentation
import org.bson.codecs.configuration.CodecRegistries
import org.bson.codecs.pojo.PojoCodecProvider
import java.io.IOException
import java.util.*
import java.util.concurrent.CompletableFuture

class MongoStorage(config: StorageOptions) : IStorage {
    private var mongoClient: MongoClient? = null
    private var mongoDatabase: MongoDatabase? = null
    private var userdataCollection: MongoCollection<UserData>? = null

    init {
        try {
            var settings = MongoClientSettings.builder()
                .uuidRepresentation(UuidRepresentation.STANDARD)

            settings = if (config.urlOverride.isNotEmpty()) {
                settings.applyConnectionString(ConnectionString(config.urlOverride))
            } else {
                settings.credential(MongoCredential.createCredential(
                    config.username,
                    "admin",
                    config.password.toCharArray()
                )).applyToClusterSettings { builder: ClusterSettings.Builder ->
                    builder.hosts(listOf(ServerAddress(config.host, config.port)))
                }
            }

            this.mongoClient = MongoClients.create(settings.build())

            val codecRegistry = CodecRegistries.fromRegistries(
                MongoClientSettings.getDefaultCodecRegistry(),
                CodecRegistries.fromCodecs(UUIDCodec()),
                CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build())
            )

            this.mongoDatabase = mongoClient!!.getDatabase(config.database)
                .withCodecRegistry(codecRegistry)
            this.userdataCollection = this.mongoDatabase!!.getCollection("userdata", UserData::class.java)
        } catch (e: Exception) {
            throw IOException("Error while attempting to setup Mongo Database: $e")
        }
    }

    override fun getUser(uuid: UUID): UserData {
        if (mongoDatabase == null) {
            Utils.printError("There was an error while attempting to fetch data from the Mongo database!")
            return UserData(uuid)
        }
        return userdataCollection?.find(Filters.eq("_id", uuid))?.firstOrNull() ?: UserData(uuid)
    }

    override fun saveUser(uuid: UUID, userData: UserData): Boolean {
        if (mongoDatabase == null) {
            Utils.printError("There was an error while attempting to save data to the Mongo database!")
            return false
        }
        val query = Filters.eq("_id", uuid)
        val result = this.userdataCollection?.replaceOne(query, userData, ReplaceOptions().upsert(true))

        return result?.wasAcknowledged() ?: false
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

    override fun close() {
        mongoClient?.close()
    }
}
