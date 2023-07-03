package com.example.services

import com.example.client.catfact.CatFactClient
import com.example.model.CatFact
import com.example.repository.FastInMemoryCatFactRepository
import com.example.repository.SlowInMemoryCatFactRepository
import com.example.repository.database.CatFactRepository
import kotlinx.coroutines.Deferred
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.ResultRow
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

    suspend fun findFirstFactOrNullAsync(): Deferred<CatFact?> = catFactRepository.findFirstWhereOrNullAsync { Op.TRUE }
    suspend fun findFactByIdOrNullAsync(id: Int): Deferred<CatFact?> = catFactRepository.findFirstCatFactByIdAsync(id)
    suspend fun insertManyFactsAsync(catFacts: List<CatFact>): Deferred<List<ResultRow>> = catFactRepository.insertManyFastAsync(catFacts)
}
