package com.example.repository.database

import com.example.application.Db
import com.example.application.fastBatchInsert
import com.example.model.CatFact
import com.example.repository.database.table.CatFactTable
import org.jetbrains.exposed.sql.select
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
        CatFactTable.fastBatchInsert(data = catFacts) {
            this[CatFactTable.catFact] = it.fact
        }
    }

    suspend fun findCatFactById(id: Int) = database.asyncExecuteTransaction {
        CatFactTable
            .select { CatFactTable.id eq id }
            .map { CatFact(fact = it[CatFactTable.catFact]) }
    }
}
