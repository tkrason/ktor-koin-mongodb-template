package com.example.services

import com.example.client.catfact.CatFactClient
import com.example.model.CatFact
import com.example.repository.CatFactRepository
import com.mongodb.client.model.Filters
import kotlinx.coroutines.flow.firstOrNull
import org.bson.types.ObjectId
import org.koin.core.annotation.Singleton

@Singleton
class CatFactService(
    private val catFactClient: CatFactClient,
    private val catFactRepository: CatFactRepository,
) {
    suspend fun getFactFromApi(): CatFact = catFactClient.getCatFact()

    suspend fun countAll() = catFactRepository.count()
    suspend fun save(catFact: CatFact) = catFactRepository.insertOne(catFact)
    suspend fun findFactByIdOrNull(id: String) = catFactRepository.findFirstOrNull {
        Filters.eq(CatFact::id.name, ObjectId(id))
    }

    suspend fun findFirstFactOrNull() = catFactRepository.findManyAsFlow().firstOrNull()

    suspend fun insertManyFacts(catFacts: List<CatFact>): Unit = catFactRepository.insertMany(catFacts).let { }

    suspend fun deleteWhereCatFactMatching(fact: String) = catFactRepository.deleteWhere {
        Filters.eq(CatFact::fact.name, fact)
    }
}
