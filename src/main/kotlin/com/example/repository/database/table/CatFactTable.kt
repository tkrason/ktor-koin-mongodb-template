package com.example.repository.database.table

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.Column

object CatFactTable : UUIDTable() {
    val catFact: Column<String> = varchar("catFact", length = 256)
}
