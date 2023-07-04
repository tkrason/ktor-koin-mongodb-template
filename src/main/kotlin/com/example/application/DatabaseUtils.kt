package com.example.application

import com.example.application.config.Config
import com.example.repository.database.table.CatFactTable
import io.ktor.server.application.ApplicationCall
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.util.KtorDsl
import io.ktor.util.pipeline.PipelineContext
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.statements.BatchInsertStatement
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransactionAsync
import org.koin.core.annotation.Singleton

@Singleton
class DatabaseUtils(
    config: Config,
) {
    private val instance = Database.connect(
        // reWriteBatchedInserts=true provides 2-3x speed improvement: https://github.com/JetBrains/Exposed/wiki/DSL#batch-insert
        url = "${config.databaseConfig.url}${config.databaseConfig.databaseName}?reWriteBatchedInserts=true",
        driver = config.databaseConfig.driver,
        user = config.databaseConfig.user,
        password = config.databaseConfig.password,
    ).also {
        // we want to block until all tables are ready
        runBlocking { createMissingTables() }
    }

    private suspend fun createMissingTables() {
        executeInNewTransactionAsync { SchemaUtils.createMissingTablesAndColumns(CatFactTable) }.await()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val postgresDbDispatcher = Dispatchers.IO.limitedParallelism(16)

    /**
     *
     */
    suspend fun inTransaction(block: suspend () -> Unit) =
        newSuspendedTransaction(context = postgresDbDispatcher, db = instance) {
            block()
        }

    /**
     * Given block will be executed in the asynchronous way, however new transaction will be created.
     * For example, given the code:
     * ```
     * transaction {
     *
     *   executeInNewTransactionAsync {
     *      // code here
     *   }
     *
     *   rollBack()
     * }
     *```
     * The code in the `executeInNewTransactionAsync` would still be committed to DB.
     */
    suspend fun <T> executeInNewTransactionAsync(block: suspend () -> T): Deferred<T> {
        return suspendedTransactionAsync(context = postgresDbDispatcher, db = instance) {
            block()
        }
    }
}

/**
 * Speeds up batch insert considerably, by now waiting for DB to fill in generated values (e.g. id of row).
 * Use when it's not necessary to return the generated ID's back to the user.
 */
fun <T> Table.fastBatchInsert(data: List<T>, block: BatchInsertStatement.(T) -> Unit) =
    batchInsert(data = data, shouldReturnGeneratedValues = false) { block(it) }

/**
 * Wraps GET request with transaction, allowing accessing DB.
 */
@KtorDsl
fun Route.transactionalGet(
    databaseUtils: DatabaseUtils,
    path: String,
    routeDefinition: suspend PipelineContext<Unit, ApplicationCall>.() -> Unit,
) = get(path) { databaseUtils.inTransaction { routeDefinition() } }

/**
 * Wraps POST request with transaction, allowing accessing DB.
 */
@KtorDsl
fun Route.transactionalPost(
    databaseUtils: DatabaseUtils,
    path: String,
    routeDefinition: suspend PipelineContext<Unit, ApplicationCall>.() -> Unit,
) = post(path) { databaseUtils.inTransaction { routeDefinition() } }

/**
 * Wraps DELETE request with transaction, allowing accessing DB.
 */
@KtorDsl
fun Route.transactionalDelete(
    databaseUtils: DatabaseUtils,
    path: String,
    routeDefinition: suspend PipelineContext<Unit, ApplicationCall>.() -> Unit,
) = delete(path) { databaseUtils.inTransaction { routeDefinition() } }
