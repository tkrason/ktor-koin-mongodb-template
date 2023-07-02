package com.example.services

import com.example.client.catfact.CatFactClient
import com.example.model.CatFact
import com.example.repository.FastInMemoryCatFactRepository
import com.example.repository.SlowInMemoryCatFactRepository
import com.example.repository.database.CatFactRepository
import org.koin.core.annotation.Singleton

@Singleton
class CatFactService(
    private val catFactClient: CatFactClient,
    private val fastInMemoryCatFactRepository: FastInMemoryCatFactRepository,
    private val slowInMemoryCatFactRepository: SlowInMemoryCatFactRepository,
    private val catFactRepository: CatFactRepository,
) {
    suspend fun getFactFromApi() = catFactClient.getCatFact()
    fun getFastCatFact() = fastInMemoryCatFactRepository.getCatFact()
    suspend fun getSlowCatFact() = slowInMemoryCatFactRepository.getCatFact()
    suspend fun asyncGetFactFromDb() = catFactRepository.findFirstCatFact()
    suspend fun asyncSaveFactsToDb(catFacts: List<CatFact>) = catFactRepository.saveAll(catFacts)
}
