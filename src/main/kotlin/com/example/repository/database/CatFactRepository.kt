package com.example.repository.database

import com.example.model.CatFact
import com.example.repository.CrudRepository
import com.example.repository.database.table.CatFactTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.statements.BatchInsertStatement
import org.koin.core.annotation.Singleton

@Singleton
class CatFactRepository : CrudRepository<CatFactTable, CatFact>(CatFactTable) {

    override fun resultRowToModel(resultRow: ResultRow): CatFact {
        return CatFact(fact = resultRow[CatFactTable.catFact])
    }

    override fun BatchInsertStatement.toBatchInsertStatement(model: CatFact) {
        this[CatFactTable.catFact] = model.fact
    }

    fun findFirstCatFactById(id: Int): CatFact? = findFirstWhereOrNull { CatFactTable.id eq id }
}
