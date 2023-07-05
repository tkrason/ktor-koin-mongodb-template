package com.example.repository

import com.example.application.Mongo
import com.example.model.CatFact
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import org.koin.core.annotation.Singleton

@Singleton
class CatFactRepository(mongo: Mongo) : MongoCrudRepository<CatFact>(
    mongo = mongo,
    databaseName = "ktor-sample",
) {
    override fun MongoDatabase.selectRepositoryCollection() = getCollection<CatFact>("cat-facts")
}
