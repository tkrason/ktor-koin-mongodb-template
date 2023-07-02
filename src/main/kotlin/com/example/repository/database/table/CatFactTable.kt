package com.example.repository.database.table

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object CatFactTable : Table() {
    val id: Column<Int> = integer("id").autoIncrement()
    val catFact: Column<String> = varchar("catFact", length = 256)
}
