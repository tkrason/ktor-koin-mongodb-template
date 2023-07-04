package com.example.repository

import com.example.application.fastBatchInsert
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.statements.BatchInsertStatement

abstract class CrudRepository<TABLE : Table, MODEL>(
    private val table: TABLE,
) {

    abstract fun resultRowToModel(resultRow: ResultRow): MODEL
    abstract fun BatchInsertStatement.toBatchInsertStatement(model: MODEL)

    fun findAll(): List<MODEL> {
        return table
            .selectAll()
            .map { resultRowToModel(it) }
    }

    fun findFirstWhereOrNull(where: SqlExpressionBuilder.() -> Op<Boolean>): MODEL? {
        return table
            .select(where)
            .firstOrNull()
            ?.let { resultRowToModel(it) }
    }

    fun findAllWhereAsync(where: SqlExpressionBuilder.() -> Op<Boolean>): List<MODEL> {
        return table
            .select(where = where)
            .map { resultRowToModel(it) }
    }

    fun insertManyFastAsync(toInsert: List<MODEL>) {
        table.fastBatchInsert(data = toInsert) { toBatchInsertStatement(it) }
    }
}
