package com.example.services

import com.example.application.Mongo
import com.example.model.Model
import com.example.repository.MongoCrudRepository
import com.mongodb.client.model.Filters
import kotlinx.coroutines.flow.firstOrNull
import org.bson.types.ObjectId

abstract class ModelService<MODEL : Model>(
    private val repository: MongoCrudRepository<MODEL>,
) {

    suspend fun countAll() = repository.count()

    suspend fun save(model: MODEL) = repository.insertOne(model)
    suspend fun saveMany(models: List<MODEL>) = repository.insertMany(models)

    suspend fun updateOne(model: MODEL) = repository.updateOneById(model)

    suspend fun findAll() = repository.findManyAsFlow()
    suspend fun findModelByIdOrNull(id: String) =
        repository.findFirstOrNull { Filters.eq(Mongo.MONGO_ID_FIELD, ObjectId(id)) }

    suspend fun findFirstFactOrNull() = repository.findManyAsFlow().firstOrNull()

    suspend fun deleteOneById(id: String) = repository.deleteWhere { Filters.eq(Mongo.MONGO_ID_FIELD, ObjectId(id)) }
}
