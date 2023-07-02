package com.example.repository.database

import com.example.application.Db
import com.example.model.CatFact
import com.example.repository.database.table.CatFactTable
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.selectAll
import org.koin.core.annotation.Singleton

@Singleton
class CatFactRepository(
    private val database: Db,
) {

    suspend fun findFirstCatFact() = database.asyncExecuteTransaction {
        CatFactTable
            .selectAll()
            .take(1)
            .map { CatFact(fact = it[CatFactTable.catFact]) }
            .first()
    }

    suspend fun saveAll(catFacts: List<CatFact>) = database.asyncExecuteTransaction {
        CatFactTable.batchInsert(catFacts) {
            this[CatFactTable.catFact] = it.fact
        }
    }
}
