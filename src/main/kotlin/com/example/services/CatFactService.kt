package com.example.services

import com.example.client.catfact.CatFactClient
import com.example.model.CatFact
import com.example.repository.CatFactRepository
import com.mongodb.client.model.Filters
import org.koin.core.annotation.Singleton

@Singleton
class CatFactService(
    private val catFactClient: CatFactClient,
    private val catFactRepository: CatFactRepository,
) : ModelService<CatFact>(catFactRepository) {

    suspend fun getFactFromApi(): CatFact = catFactClient.getCatFact()

    suspend fun deleteWhereCatFactMatching(fact: String) = catFactRepository.deleteWhere {
        Filters.eq(CatFact::fact.name, fact)
    }
}
