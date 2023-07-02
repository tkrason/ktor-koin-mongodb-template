package com.example

import com.example.application.Server
import com.example.application.config.createConfigModule
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.context.startKoin
import org.koin.ksp.generated.module

@Module
@ComponentScan
class ApplicationModule

private const val ENV_VARIABLE_NAME = "ENV"
private const val DEFAULT_ENV = "env"

fun main() {
    val environment = System.getenv(ENV_VARIABLE_NAME) ?: DEFAULT_ENV

    val koin = startKoin {
        modules(
            createConfigModule(environment),
            ApplicationModule().module,
        )
    }

    val server = koin.koin.get<Server>()
    server.startServer()
}
