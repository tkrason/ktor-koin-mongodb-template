package com.example.application

import com.example.application.config.Config
import com.example.repository.database.table.CatFactTable
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransactionAsync
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.core.annotation.Singleton

@Singleton
class Db(
    config: Config,
) {
    private val instance = Database.connect(
        url = "${config.databaseConfig.url}${config.databaseConfig.databaseName}",
        driver = config.databaseConfig.driver,
        user = config.databaseConfig.user,
        password = config.databaseConfig.password,
    ).also {
        // we want to block until all tables are ready
        runBlocking { createMissingTables() }
    }

    private suspend fun createMissingTables() {
        asyncExecuteTransaction { SchemaUtils.createMissingTablesAndColumns(CatFactTable) }.await()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val postgresDbDispatcher = Dispatchers.IO.limitedParallelism(16)

    suspend fun <T> asyncExecuteTransaction(block: () -> T): Deferred<T> {
        return suspendedTransactionAsync(postgresDbDispatcher) {
            transaction(instance) { block() }
        }
    }
}
