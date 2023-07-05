package com.example.application

import com.example.application.config.Config
import com.mongodb.kotlin.client.coroutine.MongoClient
import org.koin.core.annotation.Singleton

@Singleton
class Mongo(
    config: Config,
) {
    companion object {
        val MONGO_ID_FIELD = "_id"
    }

    val client = MongoClient.create(config.mongoConfig.connectionString)
}
