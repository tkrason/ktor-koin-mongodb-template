package com.example.application.config

data class DatabaseConfig(
    val url: String,
    val driver: String,
    val user: String,
    val password: String,
    val databaseName: String,
)
