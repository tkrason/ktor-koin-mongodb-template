package com.example.repository

import com.example.application.Mongo
import com.mongodb.client.result.InsertManyResult
import com.mongodb.kotlin.client.coroutine.MongoCollection
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import org.bson.conversions.Bson

abstract class MongoCrudRepository<ENTITY : Any>(
    mongo: Mongo,
    databaseName: String,
) {

    private val database: MongoDatabase = mongo.client.getDatabase(databaseName)
    private val collection by lazy { database.selectRepositoryCollection() }

    abstract fun MongoDatabase.selectRepositoryCollection(): MongoCollection<ENTITY>

    suspend fun <RETURN> withCollection(block: suspend MongoCollection<ENTITY>.() -> RETURN): RETURN {
        return collection.block()
    }

    suspend fun count(filter: (() -> Bson)? = null) = withCollection {
        val elements = if (filter == null) find() else find(filter())
        elements.count()
    }

    suspend fun insertMany(entities: List<ENTITY>): InsertManyResult = withCollection {
        insertMany(entities)
    }

    suspend fun insertOne(entity: ENTITY) = withCollection {
        insertOne(entity)
    }

    suspend fun findManyAsFlow(filter: (() -> Bson)? = null) = withCollection {
        if (filter == null) find() else find(filter())
    }

    suspend fun findFirstOrNull(filter: () -> Bson) = withCollection {
        findManyAsFlow(filter).firstOrNull()
    }

    suspend fun findAllModels(filter: () -> Bson) = withCollection {
        findManyAsFlow(filter).toList()
    }

    suspend fun aggregationFlow(pipeline: () -> List<Bson>) = withCollection {
        aggregate(pipeline())
    }

    suspend fun deleteWhere(filter: () -> Bson) = withCollection {
        deleteMany(filter()).deletedCount
    }
}
