package com.example.repository

import com.example.application.Db
import com.example.application.fastBatchInsert
import kotlinx.coroutines.Deferred
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.statements.BatchInsertStatement

abstract class AsyncCrudRepository<TABLE : Table, MODEL>(
    private val db: Db,
    private val table: TABLE,
) {

    abstract fun resultRowToModel(resultRow: ResultRow): MODEL
    abstract fun BatchInsertStatement.toBatchInsertStatement(model: MODEL)

    suspend fun findAllAsync(): Deferred<List<MODEL>> {
        return db.asyncExecuteTransaction {
            table
                .selectAll()
                .map { resultRowToModel(it) }
        }
    }

    suspend fun findFirstWhereOrNullAsync(where: SqlExpressionBuilder.() -> Op<Boolean>): Deferred<MODEL?> {
        return db.asyncExecuteTransaction {
            table
                .select(where)
                .firstOrNull()
                ?.let { resultRowToModel(it) }
        }
    }

    suspend fun findAllWhereAsync(where: SqlExpressionBuilder.() -> Op<Boolean>): Deferred<List<MODEL>> {
        return db.asyncExecuteTransaction {
            table
                .select(where = where)
                .map { resultRowToModel(it) }
        }
    }

    suspend fun insertManyFastAsync(toInsert: List<MODEL>): Deferred<List<ResultRow>> {
        return db.asyncExecuteTransaction {
            table.fastBatchInsert(data = toInsert) { toBatchInsertStatement(it) }
        }
    }
}
