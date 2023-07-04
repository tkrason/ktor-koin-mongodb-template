package com.example.services

import com.example.client.catfact.CatFactClient
import com.example.model.CatFact
import com.example.repository.FastInMemoryCatFactRepository
import com.example.repository.SlowInMemoryCatFactRepository
import com.example.repository.database.CatFactRepository
import org.jetbrains.exposed.sql.Op
import org.koin.core.annotation.Singleton

@Singleton
class CatFactService(
    private val catFactClient: CatFactClient,
    private val fastInMemoryCatFactRepository: FastInMemoryCatFactRepository,
    private val slowInMemoryCatFactRepository: SlowInMemoryCatFactRepository,
    private val catFactRepository: CatFactRepository,
) {
    suspend fun getFactFromApi(): CatFact = catFactClient.getCatFact()

    fun getFastCatFact(): CatFact = fastInMemoryCatFactRepository.getCatFact()
    suspend fun getSlowCatFact(): CatFact = slowInMemoryCatFactRepository.getCatFact()

    fun findFirstFactOrNull(): CatFact? = catFactRepository.findFirstWhereOrNull { Op.TRUE }
    fun findFactByIdOrNull(id: Int): CatFact? = catFactRepository.findFirstCatFactById(id)
    fun insertManyFacts(catFacts: List<CatFact>): Unit = catFactRepository.insertManyFastAsync(catFacts)
}
