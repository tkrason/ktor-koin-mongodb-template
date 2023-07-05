package com.example.repository

import com.example.application.Mongo
import com.example.model.Model
import com.mongodb.client.model.Filters
import com.mongodb.kotlin.client.coroutine.MongoCollection
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import org.bson.BsonValue
import org.bson.conversions.Bson

abstract class MongoCrudRepository<MODEL : Model>(
    mongo: Mongo,
    databaseName: String,
) {

    private val database: MongoDatabase = mongo.client.getDatabase(databaseName)
    private val collection by lazy { database.selectRepositoryCollection() }

    abstract fun MongoDatabase.selectRepositoryCollection(): MongoCollection<MODEL>

    suspend fun <RETURN> withCollection(block: suspend MongoCollection<MODEL>.() -> RETURN): RETURN {
        return collection.block()
    }

    suspend fun count(filter: (() -> Bson)? = null) = withCollection {
        val elements = if (filter == null) find() else find(filter())
        elements.count()
    }

    suspend fun insertMany(models: List<MODEL>) = withCollection {
        insertMany(models).insertedIds.map { it.value }
    }

    suspend fun insertOne(model: MODEL): BsonValue? = withCollection {
        insertOne(model).insertedId
    }

    suspend fun updateOneById(model: MODEL) = withCollection {
        replaceOne(Filters.eq(Mongo.MONGO_ID_FIELD, model.id), model)
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
