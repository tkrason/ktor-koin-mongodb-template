package com.example.services

import com.example.model.Model
import com.example.repository.MongoCrudRepository
import com.mongodb.client.model.Filters
import kotlinx.coroutines.flow.firstOrNull
import org.bson.types.ObjectId

abstract class ModelService<MODEL : Model>(
    private val repository: MongoCrudRepository<MODEL>,
) {

    suspend fun countAll() = repository.count()

    suspend fun save(model: MODEL) = repository.insertOne(model).let { }
    suspend fun saveMany(models: List<MODEL>) = repository.insertMany(models).let { }

    suspend fun findModelByIdOrNull(id: String) = repository.findFirstOrNull { Filters.eq("id", ObjectId(id)) }
    suspend fun findFirstFactOrNull() = repository.findManyAsFlow().firstOrNull()
}
