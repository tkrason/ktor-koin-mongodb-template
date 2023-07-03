package com.example.repository.database

import com.example.application.Db
import com.example.model.CatFact
import com.example.repository.AsyncCrudRepository
import com.example.repository.database.table.CatFactTable
import kotlinx.coroutines.Deferred
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.statements.BatchInsertStatement
import org.koin.core.annotation.Singleton

@Singleton
class CatFactRepository(
    database: Db,
) : AsyncCrudRepository<CatFactTable, CatFact>(database, CatFactTable) {

    override fun resultRowToModel(resultRow: ResultRow): CatFact {
        return CatFact(fact = resultRow[CatFactTable.catFact])
    }

    override fun BatchInsertStatement.toBatchInsertStatement(model: CatFact) {
        this[CatFactTable.catFact] = model.fact
    }

    suspend fun findFirstCatFactByIdAsync(id: Int): Deferred<CatFact?> =
        findFirstWhereOrNullAsync { CatFactTable.id eq id }
}
