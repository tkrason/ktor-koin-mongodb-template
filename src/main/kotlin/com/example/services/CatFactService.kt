package com.example.services

import com.example.client.catfact.CatFactClient
import com.example.model.CatFact
import com.example.repository.FastInMemoryCatFactRepository
import com.example.repository.SlowInMemoryCatFactRepository
import com.example.repository.database.CatFactRepository
import com.example.repository.database.table.CatFactTable
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.koin.core.annotation.Singleton
import java.util.UUID

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
    fun findFactByIdOrNull(id: UUID): CatFact? = catFactRepository.findFirstCatFactById(id)
    fun insertManyFacts(catFacts: List<CatFact>): Unit = catFactRepository.insertManyFastAsync(catFacts)

    fun count(): Long = catFactRepository.count()
    fun deleteWhereCatFactMatching(catFact: CatFact): Int = catFactRepository.deleteWhere { CatFactTable.catFact eq catFact.fact }
}
