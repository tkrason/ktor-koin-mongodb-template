package com.example.services

import com.example.client.catfact.CatFactClient
import com.example.repository.FastInMemoryCatFactRepository
import com.example.repository.SlowInMemoryCatFactRepository
import org.koin.core.annotation.Singleton

@Singleton
class CatFactService(
    private val catFactClient: CatFactClient,
    private val fastInMemoryCatFactRepository: FastInMemoryCatFactRepository,
    private val slowInMemoryCatFactRepository: SlowInMemoryCatFactRepository,
) {

    suspend fun getFactFromApi() = catFactClient.getCatFact()
    fun getFastCatFact() = fastInMemoryCatFactRepository.getCatFact()
    suspend fun getSlowCatFact() = slowInMemoryCatFactRepository.getCatFact()
}
