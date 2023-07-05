package com.example.application.config

import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.PropertySource
import org.koin.dsl.module

fun createConfigModule(environment: String) = module { single { loadConfig(environment) } }

data class Config(
    val port: Int,
    val apiKey: String,
    val catFactConfig: CatFactConfig,
    val mongoConfig: MongoConfig,
)

private fun loadConfig(environment: String): Config {
    return ConfigLoaderBuilder
        .default()
        .addSource(PropertySource.resource("/config/$environment.yaml"))
        .strict()
        .build()
        .loadConfigOrThrow()
}
